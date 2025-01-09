package com.banking.management.banking_management_api.controller;

import com.banking.management.banking_management_api.model.Transacao;
import com.banking.management.banking_management_api.model.Usuario;
import com.banking.management.banking_management_api.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {
    @Autowired
    private TransacaoService transacaoService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Transacao>> buscarPorUsuario(@PathVariable Long usuarioId) {
        Usuario usuario = new Usuario(); // Simulação: precisará buscar o usuário real no banco
        usuario.setId(usuarioId);
        List<Transacao> transacoes = transacaoService.buscarPorUsuario(usuario);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<Transacao>> buscarPorTipo(@RequestParam String tipo) {
        List<Transacao> transacoes = transacaoService.buscarPorTipo(tipo);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/datas")
    public ResponseEntity<List<Transacao>> buscarPorDatas(@RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fim) {
        List<Transacao> transacoes = transacaoService.buscarPorIntervaloDeDatas(inicio, fim);
        return ResponseEntity.ok(transacoes);
    }
}
