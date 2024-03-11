package com.example.beautyboutique.Controllers;

import com.example.beautyboutique.DTOs.Responses.Cart.CartResponse;
import com.example.beautyboutique.DTOs.Responses.Cart.PageCart;
import com.example.beautyboutique.Models.Cart;
import com.example.beautyboutique.Models.CartItem;
import com.example.beautyboutique.Services.Cart.CartServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {


    @Autowired
    CartServiceImpl cartService;

    @GetMapping(value = "/get-cart")
    public ResponseEntity<?> getCart(@RequestParam(value = "userId", required = true) Integer userId,
                                     @RequestParam(value = "pageNo", required = false, defaultValue = "0") Integer pageNo,
                                     @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        try {
             PageCart pageCart = cartService.getCartByUserId(userId,pageNo,pageSize);
             List<CartItem> cartItems = pageCart.getCartItems();
             Integer totalPages= pageCart.getTotalPages();
             Integer quantity = cartItems.size();
            BigDecimal totalPrice = pageCart.getTotalPrice();
            return new ResponseEntity<>(new CartResponse(0,"Get cart successfully!", cartItems,totalPages, totalPrice,quantity,200 ), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("sdfds", HttpStatus.BAD_REQUEST);
        }
    }
}
