package com.limpezaestofados.backend.quote;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.limpezaestofados.backend.catalog.Servico;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/quotes")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;
    private final CalculadoraService calculadoraService;

    public SolicitacaoController(SolicitacaoService solicitacaoService, CalculadoraService calculadoraService) {
        this.solicitacaoService = solicitacaoService;
        this.calculadoraService = calculadoraService;
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

    @PostMapping("/calcular")
    public ResponseEntity<BigDecimal> calcularPreco(@RequestBody CalculoRequest request) {
        // Agora passamos o objeto Request inteiro, o Service lida com as propriedades
        BigDecimal valorEstimado = calculadoraService.calcularOrcamento(request);

        return ResponseEntity.ok(valorEstimado);
    }

    @GetMapping
    public ResponseEntity<List<Solicitacao>> listarTodasSolicitacoes() {
        List<Solicitacao> lista = solicitacaoService.listarTodas();
        return ResponseEntity.ok(lista);
    }
}