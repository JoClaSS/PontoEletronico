package com.empresa.mvcpontoeletronico.services;

import com.empresa.mvcpontoeletronico.entities.Usuario;
import com.empresa.mvcpontoeletronico.repositories.UsuarioRepository;
import com.empresa.mvcpontoeletronico.security.CustomUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Serviço customizado para carregar usuários para autenticação
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("CustomUserDetailsService.loadUserByUsername - Carregando usuário: " + email);
        
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> {
                System.out.println("Usuário não encontrado no loadUserByUsername: " + email);
                return new UsernameNotFoundException("Usuário não encontrado: " + email);
            });

        System.out.println("Usuário carregado: " + usuario.getNome() + ", Ativo: " + usuario.getAtivo() + ", Senha: " + usuario.getSenha());

        if (!usuario.getAtivo()) {
            System.out.println("Usuário inativo: " + email);
            throw new UsernameNotFoundException("Usuário inativo: " + email);
        }

        return new CustomUserPrincipal(usuario);
    }
}