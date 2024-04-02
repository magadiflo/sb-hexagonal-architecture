package dev.magadiflo.hexagonal.app.utils;

import lombok.Getter;

@Getter
public enum ErrorCatalog {

    STUDENT_NOT_FOUND("ERR_STUDENT_001", "Estudiante no encontrado."),
    INVALID_STUDENT("ERR_STUDENT_002", "Parámetros inválidos del estudiante."),
    GENERIC_ERROR("ERR_GEN_001", "Ocurrió un error inesperado");

    private final String code;
    private final String message;

    ErrorCatalog(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
