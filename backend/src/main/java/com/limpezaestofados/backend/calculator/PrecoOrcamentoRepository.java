package com.limpezaestofados.backend.calculator;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PrecoOrcamentoRepository extends JpaRepository<PrecoOrcamento, Long> {

    Optional<PrecoOrcamento> findByServicoIdAndModeloAndAtivoTrue(Long servicoId, String modelo);
}
