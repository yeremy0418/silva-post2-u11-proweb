package com.empresa.catalogo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para toda la API.
 * Aplica SRP: única responsabilidad es interceptar y formatear errores.
 * @RestControllerAdvice combina @ControllerAdvice + @ResponseBody.
 */
@RestControllerAdvice(basePackages = "com.empresa.catalogo.controller")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(EntityNotFoundException ex,
                                   HttpServletRequest req) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return new ApiError(404, "Not Found", ex.getMessage(),
                req.getRequestURI());
    }

    /**
     * Maneja errores de validación en los DTOs de entrada → 400 Bad Request.
     * Concatena todos los errores de campo en un solo mensaje.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException ex,
                                     HttpServletRequest req) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validación fallida: {}", errores);
        return new ApiError(400, "Bad Request", errores, req.getRequestURI());
    }

    /**
     * Maneja cualquier otra excepción no controlada → 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGeneral(Exception ex, HttpServletRequest req) {
        log.error("Error interno en {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return new ApiError(500, "Internal Server Error",
                "Error inesperado. Contactar soporte.", req.getRequestURI());
    }
}
