package com.limpezaestofados.backend.catalog;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository; // Injeção de dependência do repositório de serviços

    public ServicoService(ServicoRepository servicoRepository) { // Construtor que recebe o repositório de serviços como
                                                                 // parâmetro
        this.servicoRepository = servicoRepository;
    }

    public List<Servico> listarAtivos() { // Método para listar todos os serviços ativos
        return servicoRepository.findByAtivo(true);
    }

}
