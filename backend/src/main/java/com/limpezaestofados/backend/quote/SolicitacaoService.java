package com.limpezaestofados.backend.quote;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.limpezaestofados.backend.catalog.Servico;
import com.limpezaestofados.backend.catalog.ServicoRepository;

import java.util.Optional;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final ServicoRepository servicoRepository;
    private final JavaMailSender javaMailSender;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository,
            ServicoRepository servicoRepository,
            JavaMailSender javaMailSender) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.servicoRepository = servicoRepository;
        this.javaMailSender = javaMailSender;
    }

    public Solicitacao criarSolicitacao(Solicitacao solicitacao) {

        // RN01: o serviço deve existir e estar ativo. Nunca cria serviço
        // automaticamente.
        Optional<Servico> servicoEncontrado = servicoRepository.findById(solicitacao.getServico().getId());

        if (servicoEncontrado.isEmpty() || !servicoEncontrado.get().isAtivo()) {
            throw new IllegalArgumentException("Serviço não disponível");
        }

        solicitacao.setServico(servicoEncontrado.get());

        Solicitacao solicitacaoSalva = solicitacaoRepository.save(solicitacao);

        // Envio síncrono, protegido: falha no e-mail não invalida a solicitação já
        // salva.
        try {
            enviarEmailNotificacao(solicitacaoSalva);
        } catch (Exception e) {
            System.out.println("Erro ao enviar notificação por e-mail: " + e.getMessage());
        }

        return solicitacaoSalva;
    }

    private void enviarEmailNotificacao(Solicitacao solicitacao) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo("admin@example.com"); // TODO: mover para variável de ambiente
        mensagem.setSubject("Nova Solicitação de Orçamento");
        mensagem.setText("Uma nova solicitação de orçamento foi criada:\n\n" +
                "Nome: " + solicitacao.getNome() + "\n" +
                "Telefone: " + solicitacao.getTelefone() + "\n" +
                "Cidade: " + solicitacao.getCidade() + "\n" +
                "Serviço: " + solicitacao.getServico().getNome() + "\n" +
                "Criado em: " + solicitacao.getCriadoEm());
        javaMailSender.send(mensagem);
    }
}
