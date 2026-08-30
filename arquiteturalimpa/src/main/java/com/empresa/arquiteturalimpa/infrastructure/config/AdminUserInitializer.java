package com.empresa.arquiteturalimpa.infrastructure.config;

import com.empresa.arquiteturalimpa.domain.enums.RoleType;
import com.empresa.arquiteturalimpa.domain.model.Usuario;
import com.empresa.arquiteturalimpa.domain.repository.UsuarioRepository;
import com.empresa.arquiteturalimpa.domain.security.PasswordHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Cria (ou atualiza) o usuário administrador padrão ao iniciar a aplicação.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;

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

        log.info("Criando usuário administrador: {}", adminEmail);
        try {
            Usuario novoAdmin = Usuario.builder()
                    .nome(adminNome)
                    .email(adminEmail)
                    .cpf(adminCpf)
                    .senha(passwordHasher.hash(adminSenha))
                    .role(RoleType.ADMIN)
                    .ativo(true)
                    .primeiroLogin(true)
                    .build();

            Usuario adminSalvo = usuarioRepository.save(novoAdmin);
            log.info("Usuário administrador criado com sucesso - ID: {}, Email: {}", adminSalvo.getId(), adminSalvo.getEmail());
        } catch (Exception e) {
            log.error("Erro ao criar usuário administrador", e);
            throw new RuntimeException("Falha ao inicializar usuário administrador", e);
        }
    }
}
