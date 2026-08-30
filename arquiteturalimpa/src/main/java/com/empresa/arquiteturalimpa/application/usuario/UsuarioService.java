package com.empresa.arquiteturalimpa.application.usuario;

import com.empresa.arquiteturalimpa.application.usuario.dto.CriarUsuarioRequest;
import com.empresa.arquiteturalimpa.application.usuario.dto.UsuarioResponse;
import com.empresa.arquiteturalimpa.domain.enums.RoleType;
import com.empresa.arquiteturalimpa.domain.exception.BusinessRuleException;
import com.empresa.arquiteturalimpa.domain.exception.EntityNotFoundException;
import com.empresa.arquiteturalimpa.domain.model.Usuario;
import com.empresa.arquiteturalimpa.domain.repository.UsuarioRepository;
import com.empresa.arquiteturalimpa.domain.security.PasswordHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Casos de uso relacionados a Usuario.
 * Camada de aplicação: orquestra o domínio através das portas (interfaces), sem depender de frameworks de infraestrutura.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAllByOrderByNomeAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UsuarioResponse> listarFuncionariosAtivos() {
        return usuarioRepository.findFuncionariosAtivos(RoleType.FUNCIONARIO).stream()
                .map(this::toResponse)
                .toList();
    }

    public UsuarioResponse buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    public UsuarioResponse buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    public List<UsuarioResponse> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UsuarioResponse criarUsuario(CriarUsuarioRequest request) {
        log.debug("Criando usuário: {}", request.getNome());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Já existe um usuário com este email");
        }
        if (usuarioRepository.existsByCpf(request.getCpf())) {
            throw new BusinessRuleException("Já existe um usuário com este CPF");
        }

        // Define senha como CPF (apenas números) se não fornecida
        String senhaFinal = request.getSenha();
        if (senhaFinal == null || senhaFinal.trim().isEmpty()) {
            senhaFinal = request.getCpf().replaceAll("\\D", "");
        }
        if (senhaFinal.trim().isEmpty()) {
            throw new BusinessRuleException("Não foi possível gerar senha a partir do CPF");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordHasher.hash(senhaFinal))
                .cpf(request.getCpf())
                .role(request.getRole())
                .ativo(true)
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("Usuário criado com sucesso: {} (ID: {})", usuarioSalvo.getNome(), usuarioSalvo.getId());
        return toResponse(usuarioSalvo);
    }

    @Transactional
    public UsuarioResponse atualizar(UUID id, CriarUsuarioRequest request) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (!usuarioExistente.getEmail().equals(request.getEmail())
                && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Já existe um usuário com este email");
        }
        if (!usuarioExistente.getCpf().equals(request.getCpf())
                && usuarioRepository.existsByCpf(request.getCpf())) {
            throw new BusinessRuleException("Já existe um usuário com este CPF");
        }

        usuarioExistente.setNome(request.getNome());
        usuarioExistente.setEmail(request.getEmail());
        usuarioExistente.setCpf(request.getCpf());
        usuarioExistente.setRole(request.getRole());

        if (request.getSenha() != null && !request.getSenha().trim().isEmpty()) {
            usuarioExistente.setSenha(passwordHasher.hash(request.getSenha()));
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
        log.info("Usuário atualizado com sucesso: {}", usuarioAtualizado.getNome());
        return toResponse(usuarioAtualizado);
    }

    @Transactional
    public void trocarSenha(UUID userId, String senhaAtual, String novaSenha, String confirmarSenha) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (!passwordHasher.matches(senhaAtual, usuario.getSenha())) {
            throw new BusinessRuleException("Senha atual incorreta");
        }
        if (!novaSenha.equals(confirmarSenha)) {
            throw new BusinessRuleException("Nova senha e confirmação não coincidem");
        }
        if (novaSenha.length() < 8) {
            throw new BusinessRuleException("Nova senha deve ter no mínimo 8 caracteres");
        }
        if (!novaSenha.matches("^(?=.*[a-zA-Z])(?=.*\\d).+$")) {
            throw new BusinessRuleException("Nova senha deve conter pelo menos uma letra e um número");
        }

        usuario.setSenha(passwordHasher.hash(novaSenha));
        usuario.setPrimeiroLogin(false);
        usuarioRepository.save(usuario);
        log.info("Senha alterada com sucesso para usuário: {}", usuario.getNome());
    }

    @Transactional
    public void desativarUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        if (!usuario.getAtivo()) {
            throw new BusinessRuleException("Usuário já está desativado");
        }
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuário desativado com sucesso: {} (ID: {})", usuario.getNome(), id);
    }

    @Transactional
    public void reativarUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        if (usuario.getAtivo()) {
            throw new BusinessRuleException("Usuário já está ativo");
        }
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
        log.info("Usuário reativado com sucesso: {} (ID: {})", usuario.getNome(), id);
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .cpf(usuario.getCpf())
                .role(usuario.getRole())
                .ativo(usuario.getAtivo())
                .primeiroLogin(usuario.getPrimeiroLogin())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .build();
    }
}
