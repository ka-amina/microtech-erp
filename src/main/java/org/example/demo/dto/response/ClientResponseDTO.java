package org.example.demo.dto.response;

import lombok.Data;
import org.example.demo.enums.CustomerTier;

import java.time.LocalDateTime;

@Data
public class ClientResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private CustomerTier customerTier;
    private Integer totalOrders;
    private Double totalSpent;
    private LocalDateTime fistOrderDate;
    private LocalDateTime lastOrderDate;

}
