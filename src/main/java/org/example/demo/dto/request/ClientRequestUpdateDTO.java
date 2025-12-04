package org.example.demo.dto.request;

import lombok.Data;
import org.example.demo.enums.CustomerTier;

import java.time.LocalDateTime;

@Data
public class ClientRequestUpdateDTO {
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private CustomerTier customerTier;
    private Integer totalOrders;
    private Double totalSpent;
    private LocalDateTime lastOrderDate;
}
