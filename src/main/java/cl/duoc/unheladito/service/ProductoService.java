package cl.duoc.unheladito.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.unheladito.entity.Categoria;
import cl.duoc.unheladito.entity.Producto;
import cl.duoc.unheladito.exception.DatosDuplicadosException;
import cl.duoc.unheladito.exception.DatosInvalidosException;
import cl.duoc.unheladito.exception.RecursoNoEncontradoException;
import cl.duoc.unheladito.repository.CategoriaRepository;
import cl.duoc.unheladito.repository.ProductoRepository;

@Service
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository
    ) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Producto> listar(
            String categoria,
            String busqueda,
            String orden
    ) {
        String categoriaFiltro = normalizarFiltro(categoria);
        String busquedaFiltro = normalizarFiltro(busqueda);

        Sort ordenamiento = determinarOrden(orden);

        return productoRepository.buscarCatalogo(
                categoriaFiltro,
                busquedaFiltro,
                ordenamiento
        );
    }

    public Producto buscarPorId(String id) {

        if (id == null || id.isBlank()) {
            throw new DatosInvalidosException(
                    "El código del producto es obligatorio."
            );
        }

        return productoRepository.buscarPorIdConCategoria(id.trim())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe el producto " + id
                        )
                );
    }

    @Transactional
    public Producto crear(Producto producto, String nombreCategoria) {

        validarDatosProducto(producto);

        String codigo = producto.getId();

        if (codigo == null || codigo.isBlank()) {
            codigo = generarCodigo();
        } else {
            codigo = codigo.trim();
        }

        if (codigo.length() > 20) {
            throw new DatosInvalidosException(
                    "El código del producto admite hasta 20 caracteres."
            );
        }

        if (productoRepository.existsById(codigo)) {
            throw new DatosDuplicadosException(
                    "Ya existe un producto con el código " + codigo
            );
        }

        Categoria categoria = buscarCategoria(nombreCategoria);

        producto.setId(codigo);
        producto.setCategoria(categoria);

        normalizarCamposOpcionales(producto);

        producto.setOrdenCatalogo(siguienteOrden());

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(
            String id,
            Producto datos,
            String nombreCategoria
    ) {

        validarDatosProducto(datos);

        Producto productoActual = buscarPorId(id);
        Categoria categoria = buscarCategoria(nombreCategoria);

        productoActual.setNombre(datos.getNombre().trim());
        productoActual.setCategoria(categoria);
        productoActual.setPrecio(datos.getPrecio());
        productoActual.setFormato(datos.getFormato().trim());
        productoActual.setStock(datos.getStock());
        productoActual.setAlergenos(
                datos.getAlergenos() == null ? "" : datos.getAlergenos().trim()
        );
        productoActual.setOferta(datos.isOferta());
        productoActual.setDescripcion(datos.getDescripcion().trim());
        productoActual.setImagen(
                datos.getImagen() == null ? "" : datos.getImagen().trim()
        );

        return productoRepository.save(productoActual);
    }

    @Transactional
    public void eliminar(String id) {

        Producto producto = buscarPorId(id);

        productoRepository.delete(producto);
    }

    private Categoria buscarCategoria(String nombreCategoria) {

        if (nombreCategoria == null || nombreCategoria.isBlank()) {
            throw new DatosInvalidosException(
                    "La categoría del producto es obligatoria."
            );
        }

        return categoriaRepository.findByNombreIgnoreCase(
                        nombreCategoria.trim()
                )
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la categoría " + nombreCategoria
                        )
                );
    }

    private void validarDatosProducto(Producto producto) {

        if (producto == null) {
            throw new DatosInvalidosException(
                    "Los datos del producto son obligatorios."
            );
        }

        if (producto.getNombre() == null
                || producto.getNombre().isBlank()) {
            throw new DatosInvalidosException(
                    "El nombre del producto es obligatorio."
            );
        }

        if (producto.getNombre().trim().length() > 120) {
            throw new DatosInvalidosException(
                    "El nombre admite hasta 120 caracteres."
            );
        }

        if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
            throw new DatosInvalidosException(
                    "El precio debe ser mayor que cero."
            );
        }

        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new DatosInvalidosException(
                    "El stock no puede ser negativo."
            );
        }

        if (producto.getFormato() == null
                || producto.getFormato().isBlank()) {
            throw new DatosInvalidosException(
                    "El formato del producto es obligatorio."
            );
        }

        if (producto.getDescripcion() == null
                || producto.getDescripcion().isBlank()) {
            throw new DatosInvalidosException(
                    "La descripción del producto es obligatoria."
            );
        }
    }

    private String generarCodigo() {

        String codigo;

        do {
            String aleatorio = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase(Locale.ROOT);

            codigo = "UH" + aleatorio;

        } while (productoRepository.existsById(codigo));

        return codigo;
    }

    private Integer siguienteOrden() {

        return productoRepository.findTopByOrderByOrdenCatalogoDesc()
                .map(producto -> producto.getOrdenCatalogo() + 1)
                .orElse(1);
    }

    private String normalizarFiltro(String valor) {

        if (valor == null || valor.isBlank()
                || valor.equalsIgnoreCase("Todas")) {
            return null;
        }

        return valor.trim();
    }

    private Sort determinarOrden(String orden) {

        String criterio = (orden == null || orden.isBlank())
                ? "recommended"
                : orden.trim();

        return switch (criterio) {

            case "price-asc" ->
                    Sort.by(Sort.Direction.ASC, "precio")
                            .and(Sort.by(Sort.Direction.ASC, "id"));

            case "price-desc" ->
                    Sort.by(Sort.Direction.DESC, "precio")
                            .and(Sort.by(Sort.Direction.ASC, "id"));

            case "name" ->
                    Sort.by(Sort.Direction.ASC, "nombre")
                            .and(Sort.by(Sort.Direction.ASC, "id"));

            case "recommended" ->
                    Sort.by(Sort.Direction.ASC, "ordenCatalogo")
                            .and(Sort.by(Sort.Direction.ASC, "id"));

            default -> throw new DatosInvalidosException(
                    "El criterio de ordenamiento no es válido: " + criterio
            );
        };
    }

    private void normalizarCamposOpcionales(Producto producto) {

        if (producto.getAlergenos() == null) {
            producto.setAlergenos("");
        }

        if (producto.getImagen() == null) {
            producto.setImagen("");
        }
    }
}