package org.example.demo.mappers;

import org.example.demo.dto.request.ClientRequestDTO;
import org.example.demo.dto.request.ClientRequestUpdateDTO;
import org.example.demo.dto.response.ClientResponseDTO;
import org.example.demo.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequestDTO req) {
        Client client = new Client();
        client.setFullName(req.getFullName());
        client.setEmail(req.getEmail());
        client.setPhone(req.getPhone());
        client.setAddress(req.getAddress());
        return client;
    }

    public ClientResponseDTO toResponse(Client client) {
        ClientResponseDTO res = new ClientResponseDTO();
        res.setId(client.getId());
        res.setFullName(client.getFullName());
        res.setEmail(client.getEmail());
        res.setPhone(client.getPhone());
        res.setAddress(client.getAddress());
        res.setCustomerTier(client.getFidelityLevel());
        res.setTotalOrders(client.getTotalOrders());
        res.setTotalSpent(client.getTotalSpent());
        res.setFistOrderDate(client.getFirstOrderDate());
        res.setLastOrderDate(client.getLastOrderDate());
        return res;
    }

    public void updateEntity(Client client, ClientRequestUpdateDTO req) {
        if (req.getFullName() != null) client.setFullName(req.getFullName());
        if (req.getEmail() != null) client.setEmail(req.getEmail());
        if (req.getPhone() != null) client.setPhone(req.getPhone());
        if (req.getAddress() != null) client.setAddress(req.getAddress());
        if (req.getCustomerTier() != null) client.setFidelityLevel(req.getCustomerTier());
        if (req.getTotalOrders() != null) client.setTotalOrders(req.getTotalOrders());
        if (req.getTotalSpent() != null) client.setTotalSpent(req.getTotalSpent());
        if (req.getLastOrderDate() != null) client.setLastOrderDate(req.getLastOrderDate());
    }

}
