package com.empresa.arquiteturalimpa.infrastructure.security;

import com.empresa.arquiteturalimpa.domain.model.Usuario;
import com.empresa.arquiteturalimpa.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carrega usuários para autenticação, delegando ao output port UsuarioRepository do domínio.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        if (!usuario.getAtivo()) {
            throw new UsernameNotFoundException("Usuário inativo: " + email);
        }

        return new CustomUserPrincipal(usuario);
    }
}
