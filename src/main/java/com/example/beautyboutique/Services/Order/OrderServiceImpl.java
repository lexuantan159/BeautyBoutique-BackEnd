package com.example.beautyboutique.Services.Order;
import com.example.beautyboutique.DTOs.Responses.Order.CancelOrder;
import com.example.beautyboutique.DTOs.Responses.Order.CreatedOrder;
import com.example.beautyboutique.DTOs.Responses.Order.PageOrder;
import com.example.beautyboutique.DTOs.Responses.Order.UpdateOrder;
import com.example.beautyboutique.Models.*;
import com.example.beautyboutique.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ShipDetailRepository shipDetailRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    StatusRepository statusRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    ProductRepository productRepository;


    public BigDecimal sumPriceItem(Integer[] cartItemIds) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        // handle create order details
        if (cartItemIds == null) {
            System.out.println("cartItemsId is null!");
            // Handle the null case, throw an exception, or return an error
        }
        for (Integer cartItemId : cartItemIds) {
            Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
            if (cartItemOptional.isPresent()) {
                CartItem cartItem = cartItemOptional.get();
                totalPrice = totalPrice.add(cartItem.getTotalPrice());
            }
        }
        return totalPrice;
    }

    @Override
    public CreatedOrder createOrder(Integer userId, Integer shipDetailId, Integer deliveryId, Integer paymentId, Integer[] cartItemsId) {
        try {
            // handle check existing
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ExpressionException("User not found!"));
            ShipDetail shipDetail = shipDetailRepository.findById(shipDetailId)
                    .orElseThrow(() -> new ExpressionException("Ship Detail not found!"));
            Delivery delivery = deliveryRepository.findById(deliveryId)
                    .orElseThrow(() -> new ExpressionException("Delivery not found!"));
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new ExpressionException("Payment not found!"));
            OrderStatus orderStatus = statusRepository.findById(1)
                    .orElseThrow(() -> new ExpressionException("Status not found!"));

            // handle create order
            Orders order = new Orders(shipDetail, sumPriceItem(cartItemsId), delivery, payment, orderStatus);
            orderRepository.save(order);

            System.out.println("cartItemsId: " + cartItemsId[0]);
            // handle create order details
            if (cartItemsId == null) {
                // Handle the null case, throw an exception, or return an error
                return new CreatedOrder(false, "cartItemsId is null!");
            }
            for (Integer cartItemId : cartItemsId) {
                handleOrderDetailCreation(order, cartItemId);
            }

            return new CreatedOrder(true, "Create Order successfully!");
        } catch (Exception e) {
            // Log the exception
            System.out.println("Error creating order: " + e.getMessage());
            return new CreatedOrder(false, "Create Order fail!!");
        }
    }

    @Override
    public PageOrder getOrderHistory(Integer userId, Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
        try {
            System.out.println(">>>>>>>>>>>>>>>>>" + userId);
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            if (sortDir != "None") {
                // Create Sorted instance
                Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending();
                // create Pageable instance
                pageable = PageRequest.of(pageNo, pageSize, sort);

            }
            Page<Orders> pageOrder = orderRepository.findByConditions(userId, pageable);
            Integer totalOrders = pageOrder.getTotalPages();
            List<Orders> listOrders = pageOrder.getContent();
            return new PageOrder(totalOrders, listOrders);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new PageOrder();
        }
    }

    @Override
    public CancelOrder cancelOrder(Integer userId, Integer orderId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            return new CancelOrder(false, "User not found!");

        User ownerOrder = userOptional.get();
        Optional<Orders> ordersOptional = orderRepository.findById(orderId);
        if (ordersOptional.isEmpty())
            return new CancelOrder(false, "Order not found!");

        // handle check owner order
        Orders orders = ordersOptional.get();
        User user = ordersOptional.get().getShipDetail().getUser();
        if (ownerOrder.getId() != user.getId())
            return new CancelOrder(false, "You can cancel this order!");

        // handle check status can change order
        Integer statusId = orders.getOrderStatus().getId();
        if (statusId > 1)
            return new CancelOrder(false, "The order has been shipped and you cannot cancel the order!");

        return handleCancelOrder(orders);
    }

    @Override
    public UpdateOrder updateShipDetailOrder(Integer userId, Integer orderId, Integer shipDetailId) {

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            return new UpdateOrder(false, "User not found!");

        User ownerOrder = userOptional.get();
        Optional<Orders> ordersOptional = orderRepository.findById(orderId);
        if (ordersOptional.isEmpty())
            return new UpdateOrder(false, "Order not found!");

        // handle check owner order
        Orders orders = ordersOptional.get();
        User user = ordersOptional.get().getShipDetail().getUser();
        // handle check status can change order
        Integer statusId = orders.getOrderStatus().getId();
        if (statusId > 1)
            return new UpdateOrder(false, "The order has been shipped and you cannot update the order!");

        Optional<ShipDetail> shipDetailOptional = shipDetailRepository.findById(shipDetailId);
        if (shipDetailOptional.isEmpty())
            return new UpdateOrder(false, "Ship detail not found!");
        // handle update order though ship detail
        ShipDetail shipDetail = shipDetailOptional.get();
        return updateAddressOrder(orders, shipDetail);
    }

    private UpdateOrder updateAddressOrder(Orders order, ShipDetail shipDetail) {
        try {
            order.setShipDetail(shipDetail);
            orderRepository.save(order);
            return new UpdateOrder(true, "Update ship detail successfully!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new UpdateOrder(false, "Update ship detail fail!");
        }
    }

    @Override
    public UpdateOrder updateStatusOrder(Integer userId, Integer orderId, Boolean isAccept) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            return new UpdateOrder(false, "User not found!");

        Optional<Orders> ordersOptional = orderRepository.findById(orderId);
        if (ordersOptional.isEmpty())
            return new UpdateOrder(false, "Order not found!");
        // handle set status
        Integer statusPending = isAccept ? 2 : 4;
        Optional<OrderStatus> orderStatusOptional = statusRepository.findById(statusPending);
        if (orderStatusOptional.isEmpty())
            return new UpdateOrder(false, "Order status not found!");

        // handle check update status
        Orders orders = ordersOptional.get();
        OrderStatus orderStatus = orderStatusOptional.get();
        return updateStatus(orders, orderStatus);
    }

    private UpdateOrder updateStatus(Orders order, OrderStatus status) {
        try {
            order.setOrderStatus(status);
            orderRepository.save(order);
            return new UpdateOrder(true, "Order approved successfully!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new UpdateOrder(false, "Order approved fail!");
        }
    }

    private CancelOrder handleCancelOrder(Orders order) {
        try {
            orderRepository.delete(order);
            return new CancelOrder(true, "Cancel order successfully!");
        } catch (Exception e) {
            return new CancelOrder(false, "Cancel order fail!");

        }
    }

    private void handleOrderDetailCreation(Orders order, Integer cartItemId) {
        cartItemRepository.findById(cartItemId).ifPresent(cartItem -> {
            Product product = cartItem.getProduct();
            Integer inStock = product.getQuantity();
            Integer quantityBuy = cartItem.getQuantity();

            // check quantity in stock
            if (inStock - quantityBuy <= 0)
                throw new ExpressionException("Not enough quantity!");

            // handle quantity products
            product.setQuantity(inStock - quantityBuy);
            productRepository.save(product);

            Integer quantity = cartItem.getQuantity();
            // handle create order details
            OrderDetail orderDetail = new OrderDetail(order, product, quantity);
            orderDetailRepository.save(orderDetail);

            // handle delete cart Item
            cartItemRepository.delete(cartItem);
        });
    }

}
