package com.banking.management.banking_management_api.repository;

import com.banking.management.banking_management_api.model.Transacao;
import com.banking.management.banking_management_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByUsuario(Usuario usuario); // Busca por usuário

    List<Transacao> findByType(String tipo); // Busca por tipo de transação

    List<Transacao> findByDataBetween(LocalDateTime startDate, LocalDateTime endDate); // Busca por intervalo de datas
}
