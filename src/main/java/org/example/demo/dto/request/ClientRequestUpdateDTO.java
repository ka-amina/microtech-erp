package org.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.demo.enums.CustomerTier;

import java.time.LocalDateTime;

@Data
public class ClientRequestUpdateDTO {
    
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
    
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;
    
    @Pattern(regexp = "^(\\+212|0)[5-7][0-9]{8}$", message = "Phone must be a valid Moroccan phone number")
    private String phone;
    
    @Size(min = 10, max = 255, message = "Address must be between 10 and 255 characters")
    private String address;
    
    private CustomerTier customerTier;
    
    @Min(value = 0, message = "Total orders cannot be negative")
    private Integer totalOrders;
    
    @Min(value = 0, message = "Total spent cannot be negative")
    private Double totalSpent;
    
    private LocalDateTime lastOrderDate;
}
