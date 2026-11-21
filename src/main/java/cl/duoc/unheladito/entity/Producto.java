package cl.duoc.unheladito.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @Column(nullable = false, length = 20, updatable = false)
    private String id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false, length = 40)
    private String formato;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false, length = 255)
    private String alergenos;

    @Column(nullable = false)
    private boolean oferta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 500)
    private String imagen;

    @Column(nullable = false)
    private Integer ordenCatalogo = 0;


    public Producto() {
    }


    public Producto(
            String id,
            String nombre,
            Categoria categoria,
            Integer precio,
            String formato,
            Integer stock,
            String alergenos,
            boolean oferta,
            String descripcion,
            String imagen
    ) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.formato = formato;
        this.stock = stock;
        this.alergenos = alergenos;
        this.oferta = oferta;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Integer getPrecio() {
        return precio;
    }

    public void setPrecio(Integer precio) {
        this.precio = precio;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getAlergenos() {
        return alergenos;
    }

    public void setAlergenos(String alergenos) {
        this.alergenos = alergenos;
    }

    public boolean isOferta() {
        return oferta;
    }

    public void setOferta(boolean oferta) {
        this.oferta = oferta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public Integer getOrdenCatalogo() {
        return ordenCatalogo;
    }

    public void setOrdenCatalogo(Integer ordenCatalogo) {
        this.ordenCatalogo = ordenCatalogo;
    }
}
