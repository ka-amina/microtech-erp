package org.example.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.OrderRequestDTO;
import org.example.demo.dto.response.OrderResponseDTO;
import org.example.demo.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO req) {
        OrderResponseDTO orderResponseDTO = orderService.createOrder(req);
        return ResponseEntity.ok(orderResponseDTO);
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long orderId) {
        OrderResponseDTO orderResponseDTO = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(orderResponseDTO);
    }

    @PatchMapping("/{orderId}/reject")
    public ResponseEntity<OrderResponseDTO> rejectOrder(@PathVariable Long orderId) {
        OrderResponseDTO orderResponseDTO = orderService.rejectOrder(orderId);
        return ResponseEntity.ok(orderResponseDTO);
    }

    @PatchMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponseDTO> confirmOrder(@PathVariable Long orderId) {
        OrderResponseDTO orderResponseDTO = orderService.confirmOrder(orderId);
        return ResponseEntity.ok(orderResponseDTO);
    }
}
