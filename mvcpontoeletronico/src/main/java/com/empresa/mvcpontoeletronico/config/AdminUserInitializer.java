package com.empresa.mvcpontoeletronico.config;

import com.empresa.mvcpontoeletronico.entities.Usuario;
import com.empresa.mvcpontoeletronico.entities.RoleType;
import com.empresa.mvcpontoeletronico.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Inicializador do usuário administrador padrão
 * Executa após a aplicação iniciar e cria o usuário admin se não existir
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.nome}")
    private String adminNome;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.senha}")
    private String adminSenha;

    @Value("${app.admin.cpf}")
    private String adminCpf;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        criarUsuarioAdminSeNaoExistir();
    }

    private void criarUsuarioAdminSeNaoExistir() {
        log.info("Verificando existência do usuário administrador...");

        Optional<Usuario> adminExistente = usuarioRepository.findByEmail(adminEmail);

        if (adminExistente.isPresent()) {
            log.info("Usuário administrador já existe: {}", adminEmail);
            // Atualiza dados se necessário
            Usuario admin = adminExistente.get();
            boolean precisaAtualizar = false;

            if (!adminNome.equals(admin.getNome())) {
                admin.setNome(adminNome);
                precisaAtualizar = true;
            }

            if (!adminCpf.equals(admin.getCpf())) {
                admin.setCpf(adminCpf);
                precisaAtualizar = true;
            }

            if (!admin.getAtivo()) {
                admin.setAtivo(true);
                precisaAtualizar = true;
            }

            if (!RoleType.ADMIN.equals(admin.getRole())) {
                admin.setRole(RoleType.ADMIN);
                precisaAtualizar = true;
            }

            if (precisaAtualizar) {
                usuarioRepository.save(admin);
                log.info("Dados do usuário administrador atualizados");
            }

            return;
        }

        // Cria novo usuário admin
        log.info("Criando usuário administrador: {}", adminEmail);

        try {
            Usuario novoAdmin = Usuario.builder()
                    .nome(adminNome)
                    .email(adminEmail)
                    .cpf(adminCpf)
                    .senha(passwordEncoder.encode(adminSenha)) // Usa o encoder configurado (MD5)
                    .role(RoleType.ADMIN)
                    .ativo(true)
                    .primeiroLogin(true) // Força alteração de senha no primeiro login
                    .build();

            Usuario adminSalvo = usuarioRepository.save(novoAdmin);

            log.info("Usuário administrador criado com sucesso - ID: {}, Email: {}", 
                    adminSalvo.getId(), adminSalvo.getEmail());

        } catch (Exception e) {
            log.error("Erro ao criar usuário administrador", e);
            throw new RuntimeException("Falha ao inicializar usuário administrador", e);
        }
    }
}