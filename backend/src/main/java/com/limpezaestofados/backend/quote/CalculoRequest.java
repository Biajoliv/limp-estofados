package com.limpezaestofados.backend.quote;

public class CalculoRequest {
    private String servicoAlvo; // sofa, cadeira, poltrona, colchao, tapete
    private String tamanhoOuModelo; // "1,80", "solteiro", "assento e encosto"
    private Double metrosLineares; // Usado apenas para tapetes
    private Boolean incluirImpermeabilizacao; // true ou false
    private String tipoTecido; // Mantido para referência do formulário

    // Getters
    public String getServicoAlvo() {
        return servicoAlvo;
    }

    public String getTamanhoOuModelo() {
        return tamanhoOuModelo;
    }

    public Double getMetrosLineares() {
        return metrosLineares;
    }

    public Boolean getIncluirImpermeabilizacao() {
        return incluirImpermeabilizacao;
    }

    public String getTipoTecido() {
        return tipoTecido;
    }

    // Setters
    public void setServicoAlvo(String servicoAlvo) {
        this.servicoAlvo = servicoAlvo;
    }

    public void setTamanhoOuModelo(String tamanhoOuModelo) {
        this.tamanhoOuModelo = tamanhoOuModelo;
    }

    public void setMetrosLineares(Double metrosLineares) {
        this.metrosLineares = metrosLineares;
    }

    public void setIncluirImpermeabilizacao(Boolean incluirImpermeabilizacao) {
        this.incluirImpermeabilizacao = incluirImpermeabilizacao;
    }

    public void setTipoTecido(String tipoTecido) {
        this.tipoTecido = tipoTecido;
    }
}