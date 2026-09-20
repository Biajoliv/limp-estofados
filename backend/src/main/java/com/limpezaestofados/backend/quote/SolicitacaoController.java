package com.limpezaestofados.backend.quote;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.limpezaestofados.backend.catalog.Servico;

@RestController
@RequestMapping("/api/v1/quotes")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @PostMapping
    public ResponseEntity<Solicitacao> criarSolicitacao(@RequestBody SolicitacaoRequest request) {
        Solicitacao novaSolicitacao = new Solicitacao();
        novaSolicitacao.setNome(request.getNome());
        novaSolicitacao.setTelefone(request.getTelefone());
        novaSolicitacao.setCidade(request.getCidade());

        Servico buscaServico = new Servico();
        buscaServico.setId(request.getServicoId());
        novaSolicitacao.setServico(buscaServico);

        Solicitacao solicitacaoSalva = solicitacaoService.criarSolicitacao(novaSolicitacao);

        return ResponseEntity.status(HttpStatus.CREATED).body(solicitacaoSalva);
    }
}