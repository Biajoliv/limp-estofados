package com.limpezaestofados.backend.calculator;

public class CalculoRequest {

    private Long servicoId;
    private String modelo;
    private Double metrosLineares; // usado apenas quando unidade = "por_metro"
    private boolean incluirImpermeabilizacao;

    public Long getServicoId() {
        return servicoId;
    }

    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Double getMetrosLineares() {
        return metrosLineares;
    }

    public void setMetrosLineares(Double metrosLineares) {
        this.metrosLineares = metrosLineares;
    }

    public boolean isIncluirImpermeabilizacao() {
        return incluirImpermeabilizacao;
    }

    public void setIncluirImpermeabilizacao(boolean incluirImpermeabilizacao) {
        this.incluirImpermeabilizacao = incluirImpermeabilizacao;
    }
}