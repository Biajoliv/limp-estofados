package com.limpezaestofados.backend.calculator;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class CalculadoraService {

    private final PrecoOrcamentoRepository precoOrcamentoRepository;

    public CalculadoraService(PrecoOrcamentoRepository precoOrcamentoRepository) {
        this.precoOrcamentoRepository = precoOrcamentoRepository;
    }

    public BigDecimal calcular(CalculoRequest request) {
        PrecoOrcamento preco = precoOrcamentoRepository
                .findByServicoIdAndModeloAndAtivoTrue(request.getServicoId(), request.getModelo())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Não há preço cadastrado para o serviço e modelo informados"));

        BigDecimal valor = preco.getPrecoBase();

        if ("por_metro".equals(preco.getUnidade())) {
            if (request.getMetrosLineares() == null || request.getMetrosLineares() <= 0) {
                throw new IllegalArgumentException("Informe os metros lineares para este serviço");
            }
            valor = valor.multiply(BigDecimal.valueOf(request.getMetrosLineares()));
        }

        if (request.isIncluirImpermeabilizacao()) {
            valor = valor.multiply(BigDecimal.valueOf(2));
        }

        return valor;
    }
}