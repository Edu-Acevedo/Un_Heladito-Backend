package cl.duoc.unheladito.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.unheladito.entity.Categoria;
import cl.duoc.unheladito.exception.CategoriaEnUsoException;
import cl.duoc.unheladito.exception.DatosDuplicadosException;
import cl.duoc.unheladito.exception.DatosInvalidosException;
import cl.duoc.unheladito.exception.RecursoNoEncontradoException;
import cl.duoc.unheladito.repository.CategoriaRepository;
import cl.duoc.unheladito.repository.ProductoRepository;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository
    ) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    public List<Categoria> listar() {
        return categoriaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la categoría con ID " + id
                        )
                );
    }

    @Transactional
    public Categoria crear(String nombre) {

        String nombreLimpio = validarNombre(nombre);

        if (categoriaRepository.existsByNombreIgnoreCase(nombreLimpio)) {
            throw new DatosDuplicadosException(
                    "Ya existe una categoría con el nombre: " + nombreLimpio
            );
        }

        Categoria categoria = new Categoria(nombreLimpio);

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizar(Long id, String nombre) {

        Categoria categoria = buscarPorId(id);
        String nombreLimpio = validarNombre(nombre);

        boolean nombreOcupado =
                categoriaRepository.existsByNombreIgnoreCase(nombreLimpio);

        boolean esOtraCategoria =
                !categoria.getNombre().equalsIgnoreCase(nombreLimpio);

        if (nombreOcupado && esOtraCategoria) {
            throw new DatosDuplicadosException(
                    "Ya existe otra categoría con el nombre: " + nombreLimpio
            );
        }

        categoria.setNombre(nombreLimpio);

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void eliminar(Long id) {

        Categoria categoria = buscarPorId(id);

        if (productoRepository.existsByCategoria_Id(id)) {
            throw new CategoriaEnUsoException(
                    "No se puede eliminar la categoría porque tiene productos asociados."
            );
        }

        categoriaRepository.delete(categoria);
    }

    private String validarNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {
            throw new DatosInvalidosException(
                    "El nombre de la categoría es obligatorio."
            );
        }

        String nombreLimpio = nombre.trim();

        if (nombreLimpio.length() > 80) {
            throw new DatosInvalidosException(
                    "El nombre de la categoría admite hasta 80 caracteres."
            );
        }

        return nombreLimpio;
    }
}