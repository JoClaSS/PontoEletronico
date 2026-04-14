package com.empresa.mvcpontoeletronico.dtos;

import com.empresa.mvcpontoeletronico.entities.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriarUsuarioRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;
    
    @Email(message = "Email deve ter formato válido")
    @NotBlank(message = "Email é obrigatório")
    @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
    private String email;
    
    // Senha é opcional no DTO pois é gerada automaticamente a partir do CPF
    private String senha;
    
    @NotBlank(message = "CPF é obrigatório")
    @Size(max = 14, message = "CPF deve ter formato XXX.XXX.XXX-XX")
    private String cpf;
    
    @NotNull(message = "Role é obrigatória")
    private RoleType role;
}