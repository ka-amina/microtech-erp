package org.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.ClientRequestDTO;
import org.example.demo.dto.request.ClientRequestUpdateDTO;
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

    public ClientResponseDTO updateClient(Long id, ClientRequestUpdateDTO req) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client with id " + id + " not found"));
        if (req.getEmail() != null && !req.getEmail().equals(client.getEmail())) {
            if (clientRepository.existsByEmail(req.getEmail())) {
                throw new DuplicateResourceException("email already exists");
            }
        }
        clientMapper.updateEntity(client, req);
        Client updatedClient = clientRepository.save(client);
        return clientMapper.toResponse(updatedClient);
    }

    public ClientResponseDTO deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client Not found with id" + id));
        client.setIsActive(false);
        clientRepository.save(client);
        return clientMapper.toResponse(client);
    }
}
