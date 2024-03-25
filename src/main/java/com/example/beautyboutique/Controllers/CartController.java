package com.example.beautyboutique.Controllers;

import com.example.beautyboutique.DTOs.Responses.Cart.CartResponse;
import com.example.beautyboutique.DTOs.Responses.Cart.PageCart;
import com.example.beautyboutique.DTOs.Responses.ResponseDTO;
import com.example.beautyboutique.DTOs.Responses.ResponseMessage;
import com.example.beautyboutique.Models.Cart;
import com.example.beautyboutique.Models.CartItem;
import com.example.beautyboutique.Services.Cart.CartServiceImpl;
import com.example.beautyboutique.Services.JWTService;
import com.example.beautyboutique.Services.JWTServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/cart")
public class CartController {


    @Autowired
    CartServiceImpl cartService;
    @Autowired
    JWTServiceImpl jwtService;


    @GetMapping(value = "/get-cart")
    public ResponseEntity<?> getCart(@RequestParam(value = "pageNo", required = false, defaultValue = "0") Integer pageNo,
                                     @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize,
                                     HttpServletRequest request) {

        try {
            Integer userId = jwtService.getUserIdByToken(request);
            PageCart pageCart = cartService.getCartByUserId(userId, pageNo, pageSize);
            List<CartItem> cartItems = pageCart.getCartItems();
            Integer totalPages = pageCart.getTotalPages();
            Integer quantity = cartItems.size();
            BigDecimal totalPrice = pageCart.getTotalPrice();
            return new ResponseEntity<>(new CartResponse(0, "Get cart successfully!", cartItems, totalPages, totalPrice, quantity, 200), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/update-cart")
    public ResponseEntity<?> updateCart(@RequestParam(value = "cartItemId") Integer cartItemId,
                                        @RequestParam(value = "quantity") Integer quantity,
                                        HttpServletRequest request) {
        try {
            Integer userId = jwtService.getUserIdByToken(request);
            Boolean isUpdate = cartService.updateCart(userId, cartItemId, quantity);
            if (isUpdate)
                return new ResponseEntity<>("Update cart successfully!", HttpStatus.OK);
            return new ResponseEntity<>("Update cart fail!", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/delete-cart")
    public ResponseEntity<?> deleteCart(@RequestParam(value = "cartItemId") Integer cartItemId,
                                        HttpServletRequest request) {
        try {
            Integer userId = jwtService.getUserIdByToken(request);
            Boolean isDelete = cartService.deleteCartItem(userId, cartItemId);
            if (isDelete)
                return new ResponseEntity<>("Delete cart item successfully!", HttpStatus.OK);
            return new ResponseEntity<>("Delete cart item fail!", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/add-to-cart")

    public ResponseEntity<?> addToCart(@RequestParam(value = "productId") Integer productId,
                                       @RequestParam(value = "quantity") Integer quantity,
                                       HttpServletRequest request) {
        try {

            Integer userId = jwtService.getUserIdByToken(request);
            ResponseDTO addCart = cartService.addToCard(userId, productId,quantity);
            if (addCart.getIsSuccess()) {
                return new ResponseEntity<>(addCart.getMessage(), HttpStatus.OK);
            }
            return new ResponseEntity<>(addCart.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Internal Server Error!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
