package com.example.beautyboutique.Controllers;

import com.example.beautyboutique.DTOs.Requests.Payment.CartItemId;
import com.example.beautyboutique.DTOs.Responses.Order.*;
import com.example.beautyboutique.DTOs.Responses.ResponseDTO;
import com.example.beautyboutique.DTOs.Responses.ResponseMessage;
import com.example.beautyboutique.Models.Orders;
import com.example.beautyboutique.Models.Payment;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Repositories.UserRepository;
import com.example.beautyboutique.Services.JWTServiceImpl;
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

    @Autowired
    JWTServiceImpl jwtService;


    @GetMapping(value = "/get-all-orders")
    public ResponseEntity<?> getAllOrders(@RequestParam(defaultValue = "1", required = false, name = "pageNo") Integer pageNo,
                                               @RequestParam(defaultValue = "4", required = false, name = "pageSize") Integer pageSize,
                                               @RequestParam(defaultValue = "createdAt", required = false, name = "sortBy") String sortBy,
                                               @RequestParam(defaultValue = "None", required = false, name = "sortDir") String sortDir,
                                               HttpServletRequest request) {
        try {
            PageOrder pageOrder = orderService.getAllOrder(pageNo - 1, pageSize, sortBy, sortDir);
            Integer totalPages = pageOrder.getTotalPages();
            List<Orders> orderHistories = pageOrder.getOrders();
            Integer quantity = orderHistories.size();
            return new ResponseEntity<>(new OrderHistories(0, "Get all orders successfully!", orderHistories, totalPages, quantity), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/get-summary-orders")
    public ResponseEntity<?> getOrderHistories(HttpServletRequest request) {
        try {
            OrdersSummaryDTO ordersSummaryDTO = orderService.summaryOrders();
            return new ResponseEntity<>(ordersSummaryDTO, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error"), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "/order-histories")
    public ResponseEntity<?> getOrderHistories(@RequestParam(defaultValue = "1", required = false, name = "pageNo") Integer pageNo,
                                               @RequestParam(defaultValue = "4", required = false, name = "pageSize") Integer pageSize,
                                               @RequestParam(defaultValue = "createdAt", required = false, name = "sortBy") String sortBy,
                                               @RequestParam(defaultValue = "None", required = false, name = "sortDir") String sortDir,
                                               HttpServletRequest request) {
        try {
            Integer userId = jwtService.getUserIdByToken(request);
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
    public @ResponseBody ResponseEntity<?> createPayment(CartItemId cartItemId, Integer voucherId, HttpServletRequest request) throws JSONException, IOException {

        try {
            Integer userId = jwtService.getUserIdByToken(request);
            Map<String, Object> resultPayment = zaloPayService.createOrder(userId, cartItemId.getCartItemsId() , voucherId);
            if ((int) resultPayment.get("returncode") == 1) {
                String paymentURL = (String) resultPayment.get("orderurl");
                String zpTransToken = (String) resultPayment.get("zptranstoken");
                String appTransId = (String) resultPayment.get("apptransid");
                return new ResponseEntity<>(new PaymentResponse("Create payment successfully!", paymentURL, appTransId, zpTransToken), HttpStatus.CREATED);
            }
            if ((int) resultPayment.get("returncode") == -4 && resultPayment.get("returnmessage") == "Not enough quantity!")
                return new ResponseEntity<>(new ResponseMessage("Not enough quantity in stock!"), HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>(new ResponseMessage("Create payment fail!"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println("Payment: " + e.getMessage());
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error!"), HttpStatus.INTERNAL_SERVER_ERROR);
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
                                                       @RequestParam(name = "shipDetailId") Integer shipDetailId,
                                                       @RequestParam(name = "deliveryId") Integer deliveryId,
                                                       @RequestParam(name = "paymentId") Integer paymentId,
                                                       @RequestParam(name = "voucherId", required = false) Integer voucherId,
                                                       HttpServletRequest request

    ) throws JSONException, URISyntaxException, IOException {
        try {
            Integer userId = jwtService.getUserIdByToken(request);
            if (zpTransToken != null) {
                Map<String, Object> result = zaloPayService.statusOrder(zpTransToken);
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
            return new ResponseEntity<>("Internal Server Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/update-order")
    public ResponseEntity<?> updateOrder(@RequestParam(name = "orderId") Integer orderId,
                                         @RequestParam(name = "shipDetailId") Integer shipDetailId,
                                         HttpServletRequest request) {
        try {
            Integer userId = jwtService.getUserIdByToken(request);
            UpdateOrder updateOrder = orderService.updateShipDetailOrder(userId, orderId, shipDetailId);
            Boolean isUpdated = updateOrder.getIsUpdated();
            String message = updateOrder.getMessage();
            return new ResponseEntity<>(new ResponseMessage(message), isUpdated ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error!"), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/cancel-order")
    public ResponseEntity<?> cancelOrder(@RequestParam(name = "orderId") Integer orderId,
                                         HttpServletRequest request) {
        try {
            Integer userId = jwtService.getUserIdByToken(request);
            CancelOrder cancelOrder = orderService.cancelOrder(userId, orderId);
            Boolean isCancelled = cancelOrder.getIsCancelled();
            String message = cancelOrder.getMessage();
            return new ResponseEntity<>(new ResponseMessage(message), isCancelled ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping(value = "/approve-order")
    public ResponseEntity<?> acceptOrder(@RequestParam(name = "orderId") Integer orderId,
                                         @RequestParam(name = "isAccept") Boolean isAccept,
                                         HttpServletRequest request) {
        try {
            Integer userId = jwtService.getUserIdByToken(request);
            UpdateOrder updateOrder = orderService.updateStatusOrder(userId, orderId, isAccept);
            Boolean isUpdated = updateOrder.getIsUpdated();
            String message = updateOrder.getMessage();
            return new ResponseEntity<>(new ResponseMessage(message), isUpdated ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping(value = "/change-status")
    public ResponseEntity<?> changeStatus(
            @RequestParam(name = "statusId") Integer statusId,
            @RequestParam(name = "orderItemId") Integer orderItemId,
            HttpServletRequest request) {
        try {
            Integer userId = jwtService.getUserIdByToken(request);
            ResponseDTO responseDTO = orderService.changeStatusOrder(userId, orderItemId, statusId);
            if (responseDTO.getIsSuccess())
                return new ResponseEntity<>(new ResponseMessage(responseDTO.getMessage()), HttpStatus.OK);
            return new ResponseEntity<>(new ResponseMessage(responseDTO.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
