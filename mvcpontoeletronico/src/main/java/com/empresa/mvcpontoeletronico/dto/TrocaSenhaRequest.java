package com.empresa.mvcpontoeletronico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrocaSenhaRequest {
    
    @NotBlank(message = "Senha atual é obrigatória")
    private String senhaAtual;
    
    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 8, message = "Nova senha deve ter no mínimo 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "Nova senha deve conter pelo menos uma letra e um número")
    private String novaSenha;
    
    @NotBlank(message = "Confirmação de senha é obrigatória")
    private String confirmarSenha;
}