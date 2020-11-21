package com.westland.secretsanta.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.westland.secretsanta.api.model.SantaRequest;
import com.westland.secretsanta.api.model.SantaResponse;
import com.westland.secretsanta.client.SantaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SantaController {
    private SantaService service;

    public SantaController(SantaService service){
        this.service = service;
    }

    @PostMapping("/assignsantas")
    ResponseEntity<SantaResponse> assign(@RequestBody SantaRequest request) throws JsonProcessingException {

        this.service.assignSantas(request);
        SantaResponse response = new SantaResponse();
        ResponseEntity<SantaResponse> responseEntity = new ResponseEntity<SantaResponse>(response, HttpStatus.OK);
        return responseEntity;
    }

}
