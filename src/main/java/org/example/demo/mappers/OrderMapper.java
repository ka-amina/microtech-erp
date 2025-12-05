package org.example.demo.mappers;

import org.example.demo.dto.response.OrderItemResponseDTO;
import org.example.demo.dto.response.OrderResponseDTO;
import org.example.demo.model.Order;
import org.example.demo.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponseDTO toResponse(Order order) {
        OrderResponseDTO res = new OrderResponseDTO();
        res.setId(order.getId());
        res.setClientId(order.getClient().getId());
        res.setClientName(order.getClient().getFullName());
        res.setOrderItems(order.getOrderItems().stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList()));
        res.setOrderDate(order.getOrderDate());
        res.setSubtotal(order.getSubtotal());
        res.setDiscount(order.getDiscount());
        res.setVat(order.getVat());
        res.setTotal(order.getTotal());
        res.setPromoCode(order.getPromoCode());
        res.setStatus(order.getStatus());
        res.setRemainingAmount(order.getRemainingAmount());
        return res;
    }

    public OrderItemResponseDTO toOrderItemResponse(OrderItem orderItem) {
        OrderItemResponseDTO res = new OrderItemResponseDTO();
        res.setId(orderItem.getId());
        res.setProductId(orderItem.getProduct().getId());
        res.setProductName(orderItem.getProduct().getName());
        res.setQuantity(orderItem.getQuantity());
        res.setUnitPrice(orderItem.getUnitPrice());
        res.setTotalPrice(orderItem.getTotalPrice());
        return res;
    }
}
