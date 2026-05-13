package com.empresa.catalogo.service;

import com.empresa.catalogo.dto.ProductoRequestDTO;
import com.empresa.catalogo.dto.ProductoResponseDTO;
import com.empresa.catalogo.entity.Producto;
import com.empresa.catalogo.exception.EntityNotFoundException;
import com.empresa.catalogo.factory.ProductoFactory;
import com.empresa.catalogo.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de ProductoService.
 * Aplica SRP: solo contiene lógica de negocio, delega persistencia al Repository
 * y conversión de objetos al Factory.
 * Aplica DIP: depende de la interfaz ProductoRepository (no de una clase concreta de BD).
 */
@Service
public class ProductoServiceImpl implements ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoServiceImpl.class);

    private final ProductoRepository repo;
    private final ProductoFactory factory;

    /**
     * Inyección por constructor (buena práctica: permite pruebas unitarias fáciles).
     */
    public ProductoServiceImpl(ProductoRepository repo, ProductoFactory factory) {
        this.repo = repo;
        this.factory = factory;
    }

    @Override
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        log.info("Creando producto: nombre={}, categoria={}", dto.getNombre(), dto.getCategoria());
        Producto p = factory.toEntity(dto);
        Producto guardado = repo.save(p);
        ProductoResponseDTO resp = factory.toResponseDTO(guardado);
        log.info("Producto creado exitosamente con id={}", resp.getId());
        return resp;
    }

    @Override
    public ProductoResponseDTO buscarPorId(Long id) {
        log.debug("Buscando producto con id={}", id);
        Producto p = repo.findById(id).orElseThrow(() -> {
            log.warn("Producto con id={} no encontrado", id);
            return new EntityNotFoundException("Producto", id);
        });
        return factory.toResponseDTO(p);
    }

    @Override
    public List<ProductoResponseDTO> listarActivos() {
        return repo.findByActivoTrue().stream()
                .map(factory::toResponseDTO)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando producto con id={}", id);
        buscarPorId(id); // verifica existencia antes de eliminar
        repo.deleteById(id);
        log.info("Producto con id={} eliminado correctamente", id);
    }
}
