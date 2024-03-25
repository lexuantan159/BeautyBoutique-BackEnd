package com.example.beautyboutique.Services.Cart;

import com.example.beautyboutique.DTOs.Responses.Cart.PageCart;
import com.example.beautyboutique.DTOs.Responses.ResponseDTO;
import org.apache.catalina.util.Introspection;


public interface CartService {
   PageCart getCartByUserId(Integer  userId, Integer pageNo, Integer pageSize);
   Boolean updateCart(Integer  userId, Integer cartItemId, Integer quantity);
   Boolean deleteCartItem(Integer  userId, Integer cartItemId);
   ResponseDTO addToCard (Integer userId , Integer productId , Integer quantity);
}
