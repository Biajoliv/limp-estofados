package com.limpezaestofados.backend.calculator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/quotes/calcular")
public class CalculadoraController {

    private final CalculadoraService calculadoraService;

    public CalculadoraController(CalculadoraService calculadoraService) {
        this.calculadoraService = calculadoraService;
    }

    @PostMapping
    public ResponseEntity<BigDecimal> calcular(@RequestBody CalculoRequest request) {
        BigDecimal valorEstimado = calculadoraService.calcular(request);
        return ResponseEntity.ok(valorEstimado);
    }
}