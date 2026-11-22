package cl.duoc.unheladito.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "pedidos",
        indexes = {
                @Index(
                        name = "idx_pedidos_usuario",
                        columnList = "usuario_id"
                ),
                @Index(
                        name = "idx_pedidos_fecha",
                        columnList = "fecha_creacion"
                )
        }
)
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "codigo_pedido",
            nullable = false,
            unique = true,
            length = 20
    )
    private String codigoPedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario cliente;
    @Column(name = "nombre_cliente", nullable = false, length = 100)
    private String nombreCliente;
    @Column(name = "email_cliente", nullable = false, length = 150)
    private String emailCliente;
    @Column(name = "direccion_entrega", nullable = false, length = 200)
    private String direccionEntrega;
    @Column(nullable = false, length = 100)
    private String region;
    @Column(nullable = false, length = 100)
    private String comuna;
    @Column(name = "opcion_entrega", nullable = false, length = 80)
    private String opcionEntrega;
    @Column(columnDefinition = "TEXT")
    private String indicaciones;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPedido estado;
    @Column(nullable = false)
    private Long total;
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("id ASC")
    private List<DetallePedido> detalles = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(
            String codigoPedido,
            Usuario cliente,
            String nombreCliente,
            String emailCliente,
            String direccionEntrega,
            String region,
            String comuna,
            String opcionEntrega,
            String indicaciones,
            EstadoPedido estado,
            Long total
    ) {
        this.codigoPedido = codigoPedido;
        this.cliente = cliente;
        this.nombreCliente = nombreCliente;
        this.emailCliente = emailCliente;
        this.direccionEntrega = direccionEntrega;
        this.region = region;
        this.comuna = comuna;
        this.opcionEntrega = opcionEntrega;
        this.indicaciones = indicaciones;
        this.estado = estado;
        this.total = total;
    }

    @PrePersist
    protected void antesDeGuardar() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        detalle.setPedido(this);
    }

    public Long getId() {
        return id;
    }

    public String getCodigoPedido() {
        return codigoPedido;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public String getRegion() {
        return region;
    }

    public String getComuna() {
        return comuna;
    }

    public String getOpcionEntrega() {
        return opcionEntrega;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public Long getTotal() {
        return total;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }
}