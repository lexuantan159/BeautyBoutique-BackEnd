package com.example.beautyboutique.Controllers;

import com.example.beautyboutique.DTOs.Requests.Payment.CartItemId;
import com.example.beautyboutique.DTOs.Responses.Order.*;
import com.example.beautyboutique.DTOs.Responses.ResponseMessage;
import com.example.beautyboutique.Models.Orders;
import com.example.beautyboutique.Models.Payment;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Repositories.UserRepository;
import com.example.beautyboutique.Services.Order.OrderServiceImpl;
import com.example.beautyboutique.Services.ZaloPay.ZaloPayServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NonNull;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired
    ZaloPayServiceImpl zaloPayService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderServiceImpl orderService;

    @GetMapping(value = "/order-histories")
    public ResponseEntity<?> getOrderHistories(@RequestParam(required = false, name = "userId") Integer userId,
                                               @RequestParam(defaultValue = "1", required = false, name = "pageNo") Integer pageNo,
                                               @RequestParam(defaultValue = "4", required = false, name = "pageSize") Integer pageSize,
                                               @RequestParam(defaultValue = "createdAt", required = false, name = "sortBy") String sortBy,
                                               @RequestParam(defaultValue = "None", required = false, name = "sortDir") String sortDir) {
        try {
            PageOrder pageOrder = orderService.getOrderHistory(userId, pageNo - 1, pageSize, sortBy, sortDir);
            Integer totalPages = pageOrder.getTotalPages();
            List<Orders> orderHistories = pageOrder.getOrders();
            Integer quantity = orderHistories.size();
            return new ResponseEntity<>(new OrderHistories(0, "Get order history successfully!", orderHistories, totalPages, quantity), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/create-payment", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    }, produces =
            MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody ResponseEntity<?> createPayment(CartItemId cartItemId, @RequestParam(name = "userId") Integer userId) throws JSONException, IOException {

        try {
            Map<String, Object> resultPayment = zaloPayService.createOrder(userId, cartItemId.getCartItemsId());
            if ((int) resultPayment.get("returncode") == 1) {
                String paymentURL = (String) resultPayment.get("orderurl");
                String zpTransToken = (String) resultPayment.get("zptranstoken");
                String appTransId = (String) resultPayment.get("apptransid");
                return new ResponseEntity<>(new PaymentResponse("Create payment successfully!", paymentURL, zpTransToken, appTransId), HttpStatus.CREATED);
            }
            if ((int) resultPayment.get("returncode") == -4 && resultPayment.get("returnmessage") == "Not enough quantity!")
                return new ResponseEntity<>(new ResponseMessage("Not enough quantity in stock!"), HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>(new ResponseMessage("Create payment fail!"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ResponseMessage("Internal Server!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/create-order", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    }, produces =
            MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody ResponseEntity<?> createOrder(CartItemId cartItemId,
                                                       @RequestParam(name = "zpTransToken", required = false) String zpTransToken,
                                                       @RequestParam(name = "userId") Integer userId,
                                                       @RequestParam(name = "shipDetailId") Integer shipDetailId,
                                                       @RequestParam(name = "deliveryId") Integer deliveryId,
                                                       @RequestParam(name = "paymentId") Integer paymentId,
                                                       @RequestParam(name = "voucherId") Integer voucherId

    ) throws JSONException, URISyntaxException, IOException {
        try {
            Map<String, Object> result = zaloPayService.statusOrder(zpTransToken);
            System.out.println(result.get("returncode"));
            System.out.println(result.get("returnmessage"));
            if (zpTransToken != null) {
                if ((int) result.get("returncode") == 1 && result.get("returnmessage").toString().equals("Giao dịch thành công")) {
                    CreatedOrder createdOrder = orderService.createOrder(userId, shipDetailId, deliveryId, paymentId, voucherId, cartItemId.getCartItemsId());
                    if (createdOrder.getStatus())
                        return new ResponseEntity<>(createdOrder.getMessage(), HttpStatus.CREATED);
                    else
                        return new ResponseEntity<>(createdOrder.getMessage(), HttpStatus.BAD_REQUEST);
                } else {
                    return new ResponseEntity<>("Zalo payment failed!", HttpStatus.BAD_REQUEST);
                }
            }
            // order with payment local
            CreatedOrder createdOrder = orderService.createOrder(userId, shipDetailId, deliveryId, paymentId, voucherId, cartItemId.getCartItemsId());
            if (createdOrder.getStatus())
                return new ResponseEntity<>(createdOrder.getMessage(), HttpStatus.CREATED);
            else
                return new ResponseEntity<>(createdOrder.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Internal Server!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/update-order")
    public ResponseEntity<?> updateOrder(@RequestParam(name = "userId") Integer userId,
                                         @RequestParam(name = "orderId") Integer orderId,
                                         @RequestParam(name = "shipDetailId") Integer shipDetailId) {
        try {
            UpdateOrder updateOrder = orderService.updateShipDetailOrder(userId, orderId, shipDetailId);
            Boolean isUpdated = updateOrder.getIsUpdated();
            String message = updateOrder.getMessage();
            return new ResponseEntity<>(new ResponseMessage(message), isUpdated ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ResponseMessage("Internal Server!"), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/cancel-order")
    public ResponseEntity<?> cancelOrder(@RequestParam(name = "userId") Integer userId,
                                         @RequestParam(name = "orderId") Integer orderId) {
        try {
            CancelOrder cancelOrder = orderService.cancelOrder(userId, orderId);
            Boolean isCancelled = cancelOrder.getIsCancelled();
            String message = cancelOrder.getMessage();
            return new ResponseEntity<>(new ResponseMessage(message), isCancelled ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ResponseMessage("Internal Server!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping(value = "/approve-order")
    public ResponseEntity<?> acceptOrder(@RequestParam(name = "userId") Integer userId,
                                         @RequestParam(name = "orderId") Integer orderId,
                                         @RequestParam(name = "isAccept") Boolean isAccept) {
        try {
            UpdateOrder updateOrder = orderService.updateStatusOrder(userId, orderId, isAccept);
            Boolean isUpdated = updateOrder.getIsUpdated();
            String message = updateOrder.getMessage();
            return new ResponseEntity<>(new ResponseMessage(message), isUpdated ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ResponseMessage("Internal Server!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
