package com.limpezaestofados.backend.quote;

import com.limpezaestofados.backend.catalog.Servico;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "solicitacao_orcamento")
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY) // geração automática do id
    private Long id;

    @ManyToOne // relação muitos para um com a entidade Servico
    @JoinColumn(name = "servico_id", nullable = false) // coluna de junção para a chave estrangeira do serviço
    private Servico servico;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private java.time.LocalDateTime criadoEm = java.time.LocalDateTime.now(); // atributo para armazenar a data e hora
                                                                              // de criação da solicitação, inicializado
                                                                              // com a data e hora atual

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public java.time.LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(java.time.LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Solicitacao other = (Solicitacao) obj;
        if (id == null) {
            return other.id == null;
        }
        return id.equals(other.id);
    }

}
