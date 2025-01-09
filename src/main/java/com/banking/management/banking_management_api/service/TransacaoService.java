package com.banking.management.banking_management_api.service;


import com.banking.management.banking_management_api.model.Transacao;
import com.banking.management.banking_management_api.model.Usuario;
import com.banking.management.banking_management_api.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransacaoService {
    @Autowired
    private TransacaoRepository transacaoRepository;

    public List<Transacao> buscarPorUsuario(Usuario usuario) {
        return transacaoRepository.findByUsuario(usuario);
    }

    public List<Transacao> buscarPorTipo(String tipo) {
        return transacaoRepository.findByType(tipo);
    }

    public List<Transacao> buscarPorIntervaloDeDatas(LocalDateTime inicio, LocalDateTime fim) {
        return transacaoRepository.findByDataBetween(inicio, fim);
    }

}
