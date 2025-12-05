package org.example.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestUpdateDTO {

    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
    
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
}
