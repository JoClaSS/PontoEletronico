package com.empresa.mvcpontoeletronico.services;

import com.empresa.mvcpontoeletronico.dto.LoginRequest;
import com.empresa.mvcpontoeletronico.dto.LoginResponse;
import com.empresa.mvcpontoeletronico.entities.Usuario;
import com.empresa.mvcpontoeletronico.repositories.UsuarioRepository;
import com.empresa.mvcpontoeletronico.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço de autenticação customizada
 */
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        try {
            System.out.println("AuthService.login - Email: " + request.getEmail() + ", Senha fornecida: " + request.getSenha());
            
            // Primeiro, verificar se o usuário existe
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    System.out.println("Usuário não encontrado: " + request.getEmail());
                    return new UsernameNotFoundException("Usuário não encontrado");
                });
            
            System.out.println("Usuário encontrado: " + usuario.getNome() + ", Senha no BD: " + usuario.getSenha() + ", Ativo: " + usuario.getAtivo());
            
            // Testar o hash da senha fornecida
            String senhaHasheada = passwordEncoder.encode(request.getSenha());
            System.out.println("Hash da senha fornecida: " + senhaHasheada);
            System.out.println("Senhas coincidem? " + passwordEncoder.matches(request.getSenha(), usuario.getSenha()));
            
            // Autenticar credenciais
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
            );

            // Gerar token JWT
            String token = jwtUtil.generateToken(
                usuario.getEmail(), 
                usuario.getId(), 
                usuario.getRole().getValue()
            );

            return LoginResponse.builder()
                .token(token)
                .usuario(usuario)
                .primeiroLogin(usuario.getPrimeiroLogin())
                .build();

        } catch (BadCredentialsException e) {
            System.out.println("Credenciais inválidas para: " + request.getEmail());
            throw new BadCredentialsException("Credenciais inválidas");
        } catch (Exception e) {
            System.out.println("Erro no login: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw e;
        }
    }

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}