package cl.duoc.unheladito.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.duoc.unheladito.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, String> {

    boolean existsByCategoria_Id(Long categoriaId);
    Optional<Producto> findTopByOrderByOrdenCatalogoDesc();
    @Query("""
        SELECT p
        FROM Producto p
        JOIN FETCH p.categoria c
        WHERE (:categoria IS NULL OR LOWER(c.nombre) = LOWER(:categoria))
        AND (
            :busqueda IS NULL
            OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))
            OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%'))
            OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))
        )
        """)
    List<Producto> buscarCatalogo(
            @Param("categoria") String categoria,
            @Param("busqueda") String busqueda,
            Sort sort
    );

    @Query("""
        SELECT p
        FROM Producto p
        JOIN FETCH p.categoria
        WHERE p.id = :id
        """)
    Optional<Producto> buscarPorIdConCategoria(@Param("id") String id);
}