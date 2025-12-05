package org.example.demo.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    private Long clientId;

    private List<OrderItemRequestDTO> orderItems;

    @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Promo code must match format PROMO-XXXX where X is alphanumeric")
    private String promoCode;
}
