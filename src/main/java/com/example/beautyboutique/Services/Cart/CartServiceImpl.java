package com.example.beautyboutique.Services.Cart;

import com.example.beautyboutique.DTOs.Responses.Cart.PageCart;
import com.example.beautyboutique.DTOs.Responses.ResponseDTO;
import com.example.beautyboutique.Models.Cart;
import com.example.beautyboutique.Models.CartItem;
import com.example.beautyboutique.Models.Product;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Repositories.CartItemRepository;
import com.example.beautyboutique.Repositories.CartRepository;
import com.example.beautyboutique.Repositories.ProductRepository;
import com.example.beautyboutique.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;

    public BigDecimal calculateTotalPrice(List<CartItem> cartItems) {
        BigDecimal total = BigDecimal.ZERO; // Tổng giá trị khởi đầu từ 0

        for (CartItem cartItem : cartItems) {
//            BigDecimal itemTotalPrice = cartItem.getProduct().getSalePrice().multiply(new BigDecimal(cartItem.getQuantity()));
            BigDecimal itemTotalPrice = cartItem.getTotalPrice();
            total = total.add(itemTotalPrice);
        }
        return total;
    }

    @Override
    public PageCart getCartByUserId(Integer userId, Integer pageNo, Integer pageSize) {
        try {
            Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
            if (cartOptional.isPresent()) {
                Integer cartId = cartOptional.get().getId();
                List<CartItem> cartItems = cartItemRepository.getCartItemsByCart_Id(cartId);
                BigDecimal totalPrice = calculateTotalPrice(cartItems);
                return new PageCart(totalPrice, 1, cartItems);
            } else {
                return new PageCart();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new PageCart();
        }
    }

    @Override
    public Boolean updateCart(Integer userId, Integer cartItemId, Integer quantity) {
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        if (cartOptional.isEmpty()) {
            return false;
        }
        Integer cartId = cartOptional.get().getId();
        Optional<CartItem> cartItemOptional = cartItemRepository.findByIdAndCart_Id(cartItemId, cartId);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            if (quantity > 0) {
                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);
                return true;
            }
            return false;
        }
        return false;

    }

    @Override
    public Boolean deleteCartItem(Integer userId, Integer cartItemId) {
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        if (cartOptional.isEmpty()) {
            return false;
        }
        Integer cartId = cartOptional.get().getId();
        Optional<CartItem> cartItemOptional = cartItemRepository.findByIdAndCart_Id(cartItemId, cartId);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            cartItemRepository.delete(cartItem);
            return true;
        }
        return false;
    }

    @Override
    public ResponseDTO addToCard(Integer userId, Integer productId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty())
                return new ResponseDTO(false, "User not found!");
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isEmpty())
                return new ResponseDTO(false, "Product not found!");
            Product product = productOptional.get();
            BigDecimal priceSaleProduct = product.getSalePrice();
            Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
            if (cartOptional.isPresent()) {
                Cart cart = cartOptional.get();
                BigDecimal totalPriceInCart = cart.getTotalPrice();
                cart.setTotalPrice(totalPriceInCart.add(priceSaleProduct));
                cartRepository.save(cart);
                Optional<CartItem> cartItemOptional = cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), productId);
                if (cartItemOptional.isPresent()) {
                    CartItem cartItem = cartItemOptional.get();
                    Integer quantityInCartItem = cartItem.getQuantity();
                    BigDecimal totalPriceInCartItem = cartItem.getTotalPrice();
                    BigDecimal newTotalPrice = (totalPriceInCartItem.add(priceSaleProduct));
                    cartItem.setQuantity(quantityInCartItem + 1);
                    cartItem.setTotalPrice(newTotalPrice);
                    cartItemRepository.save(cartItem);
                    return new ResponseDTO(true, "Add to cart successfully!");
                } else {
                    CartItem cartItem = new CartItem(cart,product,1,priceSaleProduct);
                    cartItemRepository.save(cartItem);
                    return new ResponseDTO(true, "Add to cart successfully!");
                }
            } else {
                Cart cart = new Cart(BigDecimal.valueOf(0), userOptional.get());
                cartRepository.save(cart);
                CartItem cartItem = new CartItem(cart, product, 1,priceSaleProduct);

                cartItemRepository.save(cartItem);
                cart.setTotalPrice(product.getSalePrice());
                cartRepository.save(cart);
                return new ResponseDTO(true, "Add to cart successfully!");
            }
        } catch (Exception e) {

            return new ResponseDTO(false, "Add cart fail!");
        }
    }

}
