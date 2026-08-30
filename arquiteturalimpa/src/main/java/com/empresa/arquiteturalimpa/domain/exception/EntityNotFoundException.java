package com.empresa.arquiteturalimpa.domain.exception;

/**
 * Lançada quando uma entidade de domínio não é encontrada.
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
