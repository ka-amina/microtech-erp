package org.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.ClientRequestDTO;
import org.example.demo.dto.response.ClientResponseDTO;
import org.example.demo.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
