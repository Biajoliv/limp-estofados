package com.limpezaestofados.backend.quote;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.limpezaestofados.backend.catalog.ServicoRepository;
import com.limpezaestofados.backend.catalog.Servico;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;
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
        // Busca o serviço. Se não existir, cria um "Serviço Teste" automaticamente!
        Servico servico = servicoRepository.findById(solicitacao.getServico().getId())
                .orElseGet(() -> {
                    Servico s = new Servico();
                    // Assumindo que você tem os setters setId e setNome na classe Servico
                    s.setId(solicitacao.getServico().getId());
                    s.setNome("Serviço Teste Automático");
                    return servicoRepository.save(s);
                });

        // Associa o serviço completo e salva no banco
        solicitacao.setServico(servico);
        Solicitacao solicitacaoSalva = solicitacaoRepository.save(solicitacao);

        // Dispara o e-mail de notificação para o responsável
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setTo("email-do-dono@limpservice.com");
            mensagem.setSubject("Novo Pedido de Orçamento: " + solicitacao.getNome());
            mensagem.setText("Olá, um cliente simulou um orçamento no site e aguarda contato!\n\n" +
                    "Nome: " + solicitacao.getNome() + "\n" +
                    "Telefone: " + solicitacao.getTelefone() + "\n" +
                    "Cidade: " + solicitacao.getCidade() + "\n" +
                    "Serviço de interesse: " + servico.getNome()); // Usamos a variável 'servico' aqui

            javaMailSender.send(mensagem);
        } catch (Exception e) {
            System.out.println("Falha ao enviar e-mail de notificação: " + e.getMessage());
        }

        return solicitacaoSalva;
    }

    public List<Solicitacao> listarTodas() {
        return solicitacaoRepository.findAll();
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
