package com.limpezaestofados.backend.quote;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.limpezaestofados.backend.catalog.Servico;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/quotes")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @PostMapping
    public ResponseEntity<Solicitacao> criarSolicitacao(@RequestBody SolicitacaoRequest request) {

        Solicitacao novaSolicitacao = new Solicitacao(); // Cria uma nova instância de Solicitacao
        novaSolicitacao.setNome(request.getNome()); // Define o nome da solicitação com base no valor recebido na
                                                    // requisição
        novaSolicitacao.setTelefone(request.getTelefone()); // Define o telefone da solicitação com base no valor
                                                            // recebido na requisição
        novaSolicitacao.setCidade(request.getCidade()); // Define a cidade da solicitação com base no valor recebido na
                                                        // requisição

        Servico buscaServico = new Servico(); // Cria uma nova instância de Servico para associar à solicitação
        buscaServico.setId(request.getServicoId()); // Define o ID do serviço com base no valor recebido na requisição

        novaSolicitacao.setServico(buscaServico); // Associa o serviço à solicitação

        Solicitacao solicitacaoSalva = solicitacaoService.criarSolicitacao(novaSolicitacao); // Chama o serviço para
                                                                                             // criar a solicitação e
                                                                                             // salva o resultado em
                                                                                             // solicitacaoSalva

        return ResponseEntity.status(HttpStatus.CREATED).body(solicitacaoSalva); // Retorna a resposta HTTP com status
                                                                                 // 201 (Created) e o objeto da
                                                                                 // solicitação salva no corpo da
                                                                                 // resposta
    }

}
