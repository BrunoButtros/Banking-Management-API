package com.banking.management.banking_management_api.service;


import com.banking.management.banking_management_api.model.Usuario;
import com.banking.management.banking_management_api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    private UsuarioRepository usuarioRepository;

    public Optional<Usuario> buscaPorId(Long Id) {
        return usuarioRepository.findById(Id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}
