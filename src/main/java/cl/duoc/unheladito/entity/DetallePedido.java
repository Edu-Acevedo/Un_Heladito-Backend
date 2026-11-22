package cl.duoc.unheladito.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "detalles_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "codigo_producto", nullable = false, length = 20)
    private String codigoProducto;

    @Column(name = "nombre_producto", nullable = false, length = 120)
    private String nombreProducto;

    @Column(name = "formato_producto", nullable = false, length = 40)
    private String formatoProducto;

    @Column(name = "imagen_producto", nullable = false, length = 500)
    private String imagenProducto;

    @Column(name = "precio_unitario", nullable = false)
    private Integer precioUnitario;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Long subtotal;

    public DetallePedido() {
    }

    public DetallePedido(
            String codigoProducto,
            String nombreProducto,
            String formatoProducto,
            String imagenProducto,
            Integer precioUnitario,
            Integer cantidad,
            Long subtotal
    ) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.formatoProducto = formatoProducto;
        this.imagenProducto = imagenProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getFormatoProducto() {
        return formatoProducto;
    }

    public String getImagenProducto() {
        return imagenProducto;
    }

    public Integer getPrecioUnitario() {
        return precioUnitario;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public Long getSubtotal() {
        return subtotal;
    }
}