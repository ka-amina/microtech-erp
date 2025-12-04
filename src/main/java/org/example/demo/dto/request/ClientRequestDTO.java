package org.example.demo.dto.request;

import lombok.Data;

@Data
public class ClientRequestDTO {
    private String fullName;
    private String email;
    private String phone;
    private String address;
}
