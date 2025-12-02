package org.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.ClientRequestDTO;
import org.example.demo.dto.response.ClientResponseDTO;
import org.example.demo.exception.DuplicateResourceException;
import org.example.demo.mappers.ClientMapper;
import org.example.demo.model.Client;
import org.example.demo.repository.ClientRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientResponseDTO createClient(ClientRequestDTO req) {
        if (clientRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("email already exists");
        }
        Client client = clientMapper.toEntity(req);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponse(savedClient);
    }
}
