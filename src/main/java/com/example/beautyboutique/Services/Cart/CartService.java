package com.example.beautyboutique.Services.Cart;

import com.example.beautyboutique.DTOs.Responses.Cart.PageCart;
import com.example.beautyboutique.Models.CartItem;
import org.springframework.data.domain.Page;

public interface CartService {

   PageCart getCartByUserId(Integer  userId, Integer pageNo, Integer pageSize);
   Boolean updateCart(Integer  userId, Integer cartItemId, Integer quantity);
   Boolean deleteCartItem(Integer  userId, Integer cartItemId);
}
