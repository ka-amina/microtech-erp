package org.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.ClientRequestDTO;
import org.example.demo.dto.response.ClientResponseDTO;
import org.example.demo.exception.DuplicateResourceException;
import org.example.demo.exception.ResourceNotFoundException;
import org.example.demo.mappers.ClientMapper;
import org.example.demo.model.Client;
import org.example.demo.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<ClientResponseDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ClientResponseDTO getClientById(Long id) {
        return clientRepository.findById(id)
                .map(clientMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Client with id " + id + " not found"));
    }
}
