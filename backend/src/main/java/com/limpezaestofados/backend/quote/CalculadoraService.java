package com.limpezaestofados.backend.quote;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class CalculadoraService {

    public BigDecimal calcularOrcamento(CalculoRequest request) {
        BigDecimal precoBase = BigDecimal.ZERO;

        // Evita NullPointerException e padroniza para letras minúsculas
        String servico = request.getServicoAlvo() != null ? request.getServicoAlvo().toLowerCase() : "";
        String tamanho = request.getTamanhoOuModelo() != null ? request.getTamanhoOuModelo().toLowerCase() : "";

        switch (servico) {
            case "sofa":
                if (tamanho.contains("1,80") || tamanho.contains("2,00")) {
                    precoBase = BigDecimal.valueOf(150.00);
                } else if (tamanho.contains("2,20") || tamanho.contains("2,50")) {
                    precoBase = BigDecimal.valueOf(180.00);
                } else if (tamanho.contains("3,00") || tamanho.contains("3,30")) {
                    precoBase = BigDecimal.valueOf(220.00);
                } else {
                    precoBase = BigDecimal.valueOf(150.00); // Valor padrão de segurança
                }
                break;

            case "cadeira":
                if (tamanho.contains("encosto")) {
                    precoBase = BigDecimal.valueOf(20.00);
                } else {
                    precoBase = BigDecimal.valueOf(15.00); // Só assento
                }
                break;

            case "poltrona":
                precoBase = BigDecimal.valueOf(60.00);
                break;

            case "colchao":
                if (tamanho.contains("solteiro")) {
                    precoBase = BigDecimal.valueOf(90.00);
                } else if (tamanho.contains("queen")) {
                    precoBase = BigDecimal.valueOf(160.00);
                } else if (tamanho.contains("king")) {
                    precoBase = BigDecimal.valueOf(180.00);
                } else {
                    precoBase = BigDecimal.valueOf(150.00); // Padrão casal
                }
                break;

            case "tapete":
                BigDecimal metros = request.getMetrosLineares() != null ? BigDecimal.valueOf(request.getMetrosLineares()) : BigDecimal.ZERO;
                precoBase = metros.multiply(BigDecimal.valueOf(20.00));
                break;

            default:
                precoBase = BigDecimal.ZERO;
        }

        // Se houver impermeabilização, o valor dobra
        if (Boolean.TRUE.equals(request.getIncluirImpermeabilizacao())) {
            precoBase = precoBase.multiply(BigDecimal.valueOf(2));
        }

        return precoBase;
    }
}