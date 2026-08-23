package com.limpezaestofados.backend.catalog;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services")
public class ServicoController {

    private final ServicoService servicoService; // Injeção para criação de objeto bean gerenciado pelo Spring

    public ServicoController(ServicoService servicoService) { // contrtutor que recebe o serviço como parâmetro
        this.servicoService = servicoService;
    }

    @GetMapping
    public List<Servico> listarAtivos() { // endpoint para listar todos os serviços ativos
        return servicoService.listarAtivos();
    }

}
