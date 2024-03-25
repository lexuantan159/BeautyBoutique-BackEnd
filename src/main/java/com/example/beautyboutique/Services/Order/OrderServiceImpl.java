package com.example.beautyboutique.Services.Order;

import com.example.beautyboutique.DTOs.Responses.Order.*;
import com.example.beautyboutique.DTOs.Responses.ResponseDTO;
import com.example.beautyboutique.Models.*;
import com.example.beautyboutique.Repositories.*;
import jakarta.transaction.Transactional;
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
    @Autowired
    VoucherRepository voucherRepository;


    public BigDecimal sumPriceItem(Integer[] cartItemIds) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        // handle create order details
        if (cartItemIds == null) {
            System.out.println("cartItemsId is null!");
            // Handle the null case, throw an exception, or return an error
        }
        assert cartItemIds != null;
        for (Integer cartItemId : cartItemIds) {
            Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
            if (cartItemOptional.isPresent()) {
                CartItem cartItem = cartItemOptional.get();
                totalPrice = totalPrice.add(cartItem.getTotalPrice());
            }
        }
        return totalPrice;
    }

    @Transactional
    @Override
    public CreatedOrder createOrder(Integer userId, Integer shipDetailId, Integer deliveryId, Integer paymentId, Integer voucherId, Integer[] cartItemsId) {
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

            BigDecimal totalPrice = sumPriceItem(cartItemsId);
            if (cartItemsId.length > 0 && totalPrice.compareTo(BigDecimal.ZERO) == 0) {
                return new CreatedOrder(true, "Create Order fail, cart item not found!");
            }
            Orders order = new Orders(shipDetail, totalPrice, delivery, payment, orderStatus);
            if (voucherId != null) {
                Optional<Voucher> voucherOptional = voucherRepository.findById(voucherId);
                Voucher voucher = voucherOptional.orElse(null);
                order.setVoucher(voucher);
            }
            if (!isEnoughQuantity(cartItemsId))
                return new CreatedOrder(false, "Not enough quantity!");
            order = orderRepository.save(order);
            // handle create order details
            return handleOrderDetailCreation(order, cartItemsId);

        } catch (Exception e) {
            // Log the exception
            System.out.println("Error creating order: " + e.getMessage());
            return new CreatedOrder(false, "Create Order fail!");
        }
    }

    @Override
    public PageOrder getOrderHistory(Integer userId, Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
        try {
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
    public PageOrder getAllOrder(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
        try {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            if (sortDir != "None") {
                // Create Sorted instance
                Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending();
                // create Pageable instance
                pageable = PageRequest.of(pageNo, pageSize, sort);
            }
            Page<Orders> pageOrder = orderRepository.findByConditions(null, pageable);
            Integer totalOrders = pageOrder.getTotalPages();
            List<Orders> listOrders = pageOrder.getContent();
            return new PageOrder(totalOrders, listOrders);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new PageOrder();
        }
    }

    @Override
    public OrdersSummaryDTO summaryOrders() {
        OrderSummary orderSummary = orderRepository.getOrdersSummary();
        return new OrdersSummaryDTO(orderSummary.getCancelledOrders(), orderSummary.getDeliveredOrders(), orderSummary.getTotalPrice(), orderSummary.getTotalOrders());
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

        Integer idCancelOrderStatus = 4;
        Optional<OrderStatus> statusOptional = statusRepository.findById(idCancelOrderStatus);
        if (statusOptional.isEmpty())
            return new CancelOrder(false, "Status not found!");
        OrderStatus orderStatus = statusOptional.get();

        return handleCancelOrder(orders, orderStatus);
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
        Integer statusPending = isAccept ? 2 : 4;
        Optional<OrderStatus> orderStatusOptional = statusRepository.findById(statusPending);
        if (orderStatusOptional.isEmpty())
            return new UpdateOrder(false, "Order status not found!");

        // handle check update status
        Orders orders = ordersOptional.get();
        if (orders.getOrderStatus().getId() == 2 || orders.getOrderStatus().getId() == 4)
            return new UpdateOrder(true, "You have approved this order!");
        OrderStatus orderStatus = orderStatusOptional.get();
        return updateStatus(orders, orderStatus);
    }

    @Override
    public ResponseDTO changeStatusOrder(Integer userId, Integer orderItemId, Integer statusId) {
        try {
            Optional<Orders> orderDetailOptional = orderRepository.findById(orderItemId);
            if (orderDetailOptional.isEmpty())
                return new ResponseDTO(false, "Order Detail not found!");
            // handle set status
            Optional<OrderStatus> orderStatusOptional = statusRepository.findById(statusId);
            if (orderStatusOptional.isEmpty())
                return new ResponseDTO(false, "Order not found!");

            Orders orderDetail = orderDetailOptional.get();
            Integer statusCheck = orderDetail.getOrderStatus().getId();
            if (statusCheck == 4 || statusCheck == 1) {
                return new ResponseDTO(true, "Can't Change Status Order!");
            }
            OrderStatus orderStatus = orderStatusOptional.get();

            orderDetail.setOrderStatus(orderStatus);
            orderRepository.save(orderDetail);
            return new ResponseDTO(true, "Change Status Order successfully!");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseDTO(false, "Change Status Order failed");
        }
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

    private CancelOrder handleCancelOrder(Orders order, OrderStatus orderStatus) {
        try {
            List<OrderDetail> orderDetails = order.getOrdersDetails();
            for (OrderDetail orderDetail : orderDetails) {
                Product product = orderDetail.getProduct();
                Integer quantity = orderDetail.getQuantity();
                if (product != null) {
                    if (quantity != null && quantity > 0) {
                        product.setQuantity(product.getQuantity() + quantity);
                        productRepository.save(product);
                    }
                }
            }
            order.setOrderStatus(orderStatus);
            orderRepository.save(order);
            return new CancelOrder(true, "Cancel order successfully!");
        } catch (Exception e) {
            return new CancelOrder(false, "Cancel order fail!");

        }
    }

    @Transactional
    public CreatedOrder handleOrderDetailCreation(Orders order, Integer[] cartItemsId) {
        try {
            for (Integer cartItemId : cartItemsId) {
                Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
                if (cartItemOptional.isEmpty()) {
                    // Nếu cartItem không tồn tại, bạn có thể xử lý theo nhu cầu của mình, ví dụ: bỏ qua hoặc đưa ra thông báo lỗi
                    return new CreatedOrder(false, "CartItem with ID " + cartItemId + " not found!");
                }
                CartItem cartItem = cartItemOptional.get();
                Product product = cartItem.getProduct();
                Integer inStock = product.getQuantity();
                Integer quantityBuy = cartItem.getQuantity();
                // Kiểm tra số lượng tồn kho
                if (inStock - quantityBuy < 0)
                    return new CreatedOrder(false, "Not enough quantity!");
                // Xử lý số lượng sản phẩm
                product.setQuantity(inStock - quantityBuy);
                productRepository.save(product);
                Integer quantity = cartItem.getQuantity();
                // Xử lý tạo chi tiết đơn hàng
                OrderDetail orderDetail = new OrderDetail(order, product, quantity);
                orderDetailRepository.save(orderDetail);
                // Xử lý xóa mục giỏ hàng
                cartItemRepository.delete(cartItem);
            }
            // Lưu đơn hàng
            orderRepository.save(order);
            return new CreatedOrder(true, "Create Order successfully!");
        } catch (Exception e) {
            System.out.println("Loi:" + e.getMessage());
            return new CreatedOrder(false, "Create Order fail!");
        }
    }

    private Boolean isEnoughQuantity(Integer[] cartItemsId) {
        try {
            for (Integer cartItemId : cartItemsId) {
                Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
                if (cartItemOptional.isEmpty()) {
                    return false;
                }
                CartItem cartItem = cartItemOptional.get();
                Product product = cartItem.getProduct();
                Integer inStock = product.getQuantity();
                Integer quantityBuy = cartItem.getQuantity();
                if (inStock - quantityBuy < 0)
                    return false;
            }
            return true;
        } catch (Exception e) {
            System.out.println("Loi:" + e.getMessage());
            return false;
        }
    }


}
