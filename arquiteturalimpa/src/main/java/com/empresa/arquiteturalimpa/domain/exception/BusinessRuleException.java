package com.empresa.arquiteturalimpa.domain.exception;

/**
 * Lançada quando uma regra de negócio do domínio é violada.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
