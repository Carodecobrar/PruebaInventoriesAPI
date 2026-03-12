package com.prueba.inventories.dto.response;

import com.prueba.inventories.dto.exception.InsufficientStockException;
import com.prueba.inventories.dto.exception.OptimisticLockException;
import com.prueba.inventories.dto.exception.ProductNotFoundException;
import com.prueba.inventories.dto.exception.ProductsServiceException;
import com.prueba.inventories.dto.jsonapi.JsonApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<JsonApiErrorResponse> handleProductNotFound(ProductNotFoundException ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado: {} en la ruta: {}", ex.getMessage(), request.getRequestURI());
        JsonApiErrorResponse response = JsonApiErrorResponse.builder()
                .errors(List.of(
                        JsonApiErrorResponse.Error.builder()
                                .status("404")
                                .title("Producto no encontrado")
                                .detail(ex.getMessage())
                                .build()
                ))
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductsServiceException.class)
    public ResponseEntity<JsonApiErrorResponse> handleProductsServiceException(ProductsServiceException ex, HttpServletRequest request) {
        log.error("Error en servicio externo de productos: {} al acceder a: {}", ex.getMessage(), request.getRequestURI());
        JsonApiErrorResponse response = JsonApiErrorResponse.builder()
                .errors(List.of(
                        JsonApiErrorResponse.Error.builder()
                                .status("503")
                                .title("Servicio de productos no disponible")
                                .detail(ex.getMessage())
                                .build()
                ))
                .build();
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<JsonApiErrorResponse> handleOptimisticLockException(OptimisticLockException ex, HttpServletRequest request) {
        log.error("Conflicto de concurrencia: {} en la ruta: {}", ex.getMessage(), request.getRequestURI());
        JsonApiErrorResponse response = JsonApiErrorResponse.builder()
            .errors(List.of(
                JsonApiErrorResponse.Error.builder()
                    .status("409")
                    .title("El stock disponible ha sido modificado por otro usuario")
                    .detail(ex.getMessage())
                    .source(JsonApiErrorResponse.Error.Source.builder()
                            .pointer("/data/" + request.getRequestURI())
                            .build())
                    .build()
            ))
            .build();

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<JsonApiErrorResponse> handleInsufficientStock(
            InsufficientStockException ex,
            HttpServletRequest request) {
        log.warn("Validación de stock fallida: {} en la ruta: {}", ex.getMessage(), request.getRequestURI());
        JsonApiErrorResponse response = JsonApiErrorResponse.builder()
                .errors(List.of(
                        JsonApiErrorResponse.Error.builder()
                                .status("422")  // Unprocessable Entity
                                .title("Stock insuficiente")
                                .detail(ex.getMessage())
                                .source(JsonApiErrorResponse.Error.Source.builder()
                                        .pointer("/data/attributes/quantity")
                                        .build())
                                .build()
                ))
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado capturado en GlobalExceptionHandler: ", ex);
        JsonApiErrorResponse response = JsonApiErrorResponse.builder()
                .errors(List.of(
                        JsonApiErrorResponse.Error.builder()
                                .status("500")
                                .title("Error interno del servidor")
                                .detail("Ha ocurrido un error inesperado")
                                .build()
                ))
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
