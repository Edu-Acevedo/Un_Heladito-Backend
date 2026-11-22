package cl.duoc.unheladito.config;

import java.util.List;
import cl.duoc.unheladito.entity.Categoria;
import cl.duoc.unheladito.entity.Producto;
import cl.duoc.unheladito.repository.CategoriaRepository;
import cl.duoc.unheladito.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CatalogoBootstrapRunner implements ApplicationRunner {

    private static final List<String> CATEGORIAS_INICIALES = List.of(
            "Helados tradicionales",
            "Sorbetes y veganos",
            "Paletas artesanales",
            "Postres fríos"
    );

    private static final List<ProductoInicial> PRODUCTOS_INICIALES = List.of(

            new ProductoInicial(
                    "HT001", "Chocolate Belga 70%",
                    "Helados tradicionales", 4500, "500 g", 150,
                    "Leche", true,
                    "Cacao belga intenso, textura cremosa y pequeñas hojuelas de chocolate semiamargo.",
                    "https://images.pexels.com/photos/8713078/pexels-photo-8713078.jpeg"
            ),

            new ProductoInicial(
                    "SV001", "Frutilla & Albahaca",
                    "Sorbetes y veganos", 3900, "500 g", 120,
                    "Puede contener trazas", false,
                    "Frutillas frescas combinadas con una nota aromática de albahaca.",
                    "https://images.pexels.com/photos/22484693/pexels-photo-22484693.jpeg"
            ),

            new ProductoInicial(
                    "PA001", "Maracuyá & Leche Condensada",
                    "Paletas artesanales", 2200, "Unidad", 250,
                    "Leche", true,
                    "Paleta de maracuyá natural con un centro suave de leche condensada.",
                    "https://images.pexels.com/photos/22484702/pexels-photo-22484702.jpeg"
            ),

            new ProductoInicial(
                    "PC001", "Sundae Caramelo Salado",
                    "Postres fríos", 5200, "400 g", 90,
                    "Leche, nuez", false,
                    "Crema americana con salsa de caramelo salado y un toque crocante.",
                    "https://images.pexels.com/photos/9500931/pexels-photo-9500931.jpeg"
            ),

            new ProductoInicial(
                    "HT002", "Mango Tropical",
                    "Helados tradicionales", 4100, "500 g", 140,
                    "Leche", true,
                    "Un sabor frutal y refrescante elaborado con mango.",
                    "https://images.pexels.com/photos/5060372/pexels-photo-5060372.jpeg"
            ),

            new ProductoInicial(
                    "HT003", "Chocolate & Avellana",
                    "Helados tradicionales", 4700, "500 g", 80,
                    "Leche, avellana", false,
                    "Chocolate cremoso acompañado de avellanas tostadas.",
                    "https://images.pexels.com/photos/5061032/pexels-photo-5061032.jpeg"
            ),

            new ProductoInicial(
                    "SV002", "Sorbete de Limón",
                    "Sorbetes y veganos", 3600, "500 g", 100,
                    "Sin lácteos declarados", false,
                    "Sorbete cítrico y refrescante, ideal para los días calurosos.",
                    "https://images.pexels.com/photos/32848535/pexels-photo-32848535.jpeg"
            ),

            new ProductoInicial(
                    "HT004", "Vainilla Artesanal",
                    "Helados tradicionales", 4000, "500 g", 200,
                    "Leche", false,
                    "Un clásico suave elaborado con inspiración en la vainilla de Madagascar.",
                    "https://images.pexels.com/photos/16630830/pexels-photo-16630830.jpeg"
            ),

            new ProductoInicial(
                    "PA002", "Frutos del Bosque",
                    "Paletas artesanales", 2000, "Unidad", 12,
                    "0% Azucar", true,
                    "Paleta frutal con una mezcla de berries y sabor fresco.",
                    "https://images.pexels.com/photos/39831399/pexels-photo-39831399.jpeg"
            )
    );

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final boolean enabled;

    public CatalogoBootstrapRunner(
            CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository,
            @Value("${app.bootstrap.catalogo.enabled:false}") boolean enabled
    ) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.enabled = enabled;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (!enabled) {
            return;
        }

        int categoriasCreadas = 0;

        for (String nombre : CATEGORIAS_INICIALES) {

            if (categoriaRepository.findByNombreIgnoreCase(nombre).isEmpty()) {

                categoriaRepository.save(new Categoria(nombre));
                categoriasCreadas++;
            }
        }

        int productosCreados = 0;
        int orden = 1;

        for (ProductoInicial inicial : PRODUCTOS_INICIALES) {

            if (!productoRepository.existsById(inicial.id())) {

                Categoria categoria = categoriaRepository
                        .findByNombreIgnoreCase(inicial.categoria())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No se encontró la categoría: "
                                                + inicial.categoria()
                                )
                        );

                Producto producto = new Producto(
                        inicial.id(),
                        inicial.nombre(),
                        categoria,
                        inicial.precio(),
                        inicial.formato(),
                        inicial.stock(),
                        inicial.alergenos(),
                        inicial.oferta(),
                        inicial.descripcion(),
                        inicial.imagen()
                );

                producto.setOrdenCatalogo(orden);

                productoRepository.save(producto);

                productosCreados++;
            }

            orden++;
        }

        System.out.println("------------------------------------");
        System.out.println("CARGA INICIAL DEL CATÁLOGO");
        System.out.println("Categorías nuevas: " + categoriasCreadas);
        System.out.println("Productos nuevos: " + productosCreados);
        System.out.println("------------------------------------");
    }

    private record ProductoInicial(
            String id,
            String nombre,
            String categoria,
            Integer precio,
            String formato,
            Integer stock,
            String alergenos,
            boolean oferta,
            String descripcion,
            String imagen
    ) {
    }
}