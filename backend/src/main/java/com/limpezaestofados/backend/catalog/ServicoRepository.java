package com.limpezaestofados.backend.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    List<Servico> findByAtivo(boolean ativo);
}
