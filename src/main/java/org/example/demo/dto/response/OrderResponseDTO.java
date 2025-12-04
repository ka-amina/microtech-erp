package org.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long id;
    private Long clientId;
    private String clientName;
    private List<OrderItemResponseDTO> orderItems;
    private LocalDateTime orderDate;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal vat;
    private BigDecimal total;
    private String promoCode;
    private OrderStatus status;
    private BigDecimal remainingAmount;
}
