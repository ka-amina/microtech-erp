package org.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.ClientRequestDTO;
import org.example.demo.dto.request.ClientRequestUpdateDTO;
import org.example.demo.dto.response.ClientResponseDTO;
import org.example.demo.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/clients")
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@RequestBody ClientRequestDTO req) {
        ClientResponseDTO clientResponseDTO = clientService.createClient(req);
        return ResponseEntity.ok(clientResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        List<ClientResponseDTO> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable Long id) {
        ClientResponseDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable Long id, @RequestBody ClientRequestUpdateDTO req) {
        ClientResponseDTO client = clientService.updateClient(id, req);
        return ResponseEntity.ok(client);
    }

}
