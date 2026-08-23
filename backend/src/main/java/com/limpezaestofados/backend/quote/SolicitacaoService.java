package com.limpezaestofados.backend.quote;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.limpezaestofados.backend.catalog.ServicoRepository;
import com.limpezaestofados.backend.catalog.Servico;
import org.springframework.mail.SimpleMailMessage;
import java.util.Optional;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final ServicoRepository servicoRepository;
    private final JavaMailSender javaMailSender;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository, ServicoRepository servicoRepository,
            JavaMailSender javaMailSender) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.servicoRepository = servicoRepository;
        this.javaMailSender = javaMailSender;
    }

    public Solicitacao criarSolicitacao(Solicitacao solicitacao) {

        Optional<Servico> servicoEncontrado = servicoRepository.findById(solicitacao.getServico().getId());

        // isempty() verifica se o serviço existe e isAtivo() verifica se o serviço está
        // ativo
        if (servicoEncontrado.isEmpty() || !servicoEncontrado.get().isAtivo()) {
            throw new IllegalArgumentException("Serviço não disponível");
        }
        // salva solicitação no banco de dados
        Solicitacao solicitacaoSalva = solicitacaoRepository.save(solicitacao); // objeto do tipo Solicitacao salvo no
                                                                                // banco de dados

        // tenta notificar o administrador por e-mail sobre a nova solicitação
        try {
            enviarEmailNotificacao(solicitacaoSalva);
        } catch (Exception e) {
            System.out.println("Erro ao enviar notificação por e-mail: " + e.getMessage());
        }

        return solicitacaoSalva;
    }

    private void enviarEmailNotificacao(Solicitacao solicitacao) {
        // Implementação do envio de e-mail usando javaMailSender
        // Aqui você pode criar o conteúdo do e-mail e enviá-lo para o administrador
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo("admin@example.com");
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
