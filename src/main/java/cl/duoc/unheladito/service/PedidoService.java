package cl.duoc.unheladito.service;

import cl.duoc.unheladito.dto.pedido.*;
import cl.duoc.unheladito.entity.*;
import cl.duoc.unheladito.exception.*;
import cl.duoc.unheladito.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    private static final Set<String> OPCIONES_ENTREGA = Set.of(
            "Domicilio estándar",
            "Retiro en tienda",
            "Entrega prioritaria (simulada)"
    );

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PedidoResponse crear(
            CrearPedidoRequest request,
            String emailAutenticado
    ) {

        Usuario cliente = buscarUsuario(emailAutenticado);

        if (cliente.getRol() != RolUsuario.CLIENTE) {
            throw new OperacionNoPermitidaException(
                    "Solo los clientes pueden realizar compras."
            );
        }

        if (request == null || request.customer() == null
                || request.items() == null || request.payment() == null) {
            throw new DatosInvalidosException(
                    "Los datos del pedido están incompletos."
            );
        }

        validarEntrega(request.customer().delivery());

        Set<String> codigosUtilizados = new HashSet<>();

        List<LineaPreparada> lineas = new ArrayList<>();

        long total = 0;

        for (ItemPedidoRequest item : request.items()) {

            String codigo = item.id()
                    .trim()
                    .toUpperCase(Locale.ROOT);

            if (!codigosUtilizados.add(codigo)) {
                throw new DatosInvalidosException(
                        "El producto " + codigo
                                + " está repetido en el pedido."
                );
            }

            Producto producto = productoRepository
                    .buscarPorIdConBloqueo(codigo)
                    .orElseThrow(() ->
                            new RecursoNoEncontradoException(
                                    "No existe el producto " + codigo
                            )
                    );

            int cantidad = item.qty();

            if (producto.getStock() < cantidad) {
                throw new OperacionNoPermitidaException(
                        "Stock insuficiente para "
                                + producto.getNombre()
                                + ". Disponible: "
                                + producto.getStock()
                );
            }

            long subtotal;

            try {
                subtotal = Math.multiplyExact(
                        producto.getPrecio().longValue(),
                        cantidad
                );

                total = Math.addExact(total, subtotal);

            } catch (ArithmeticException ex) {
                throw new DatosInvalidosException(
                        "El total del pedido supera el límite permitido."
                );
            }

            lineas.add(new LineaPreparada(
                    producto,
                    cantidad,
                    subtotal
            ));
        }

        DatosClientePedidoRequest datos = request.customer();

        Pedido pedido = new Pedido(
                generarCodigoPedido(),
                cliente,
                datos.name().trim(),
                datos.email().trim(),
                datos.street().trim(),
                datos.region().trim(),
                datos.commune().trim(),
                datos.delivery().trim(),
                datos.notes() == null || datos.notes().isBlank()
                        ? null
                        : datos.notes().trim(),
                request.payment(),
                total
        );

        for (LineaPreparada linea : lineas) {

            Producto producto = linea.producto();

            DetallePedido detalle = new DetallePedido(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getFormato(),
                    producto.getImagen(),
                    producto.getPrecio(),
                    linea.cantidad(),
                    linea.subtotal()
            );

            pedido.agregarDetalle(detalle);

            if (request.payment() == EstadoPedido.APROBADO_SIMULADO) {

                producto.setStock(
                        producto.getStock() - linea.cantidad()
                );
            }
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        return convertirRespuesta(pedidoGuardado);
    }

    public List<PedidoResponse> listarMisPedidos(String emailAutenticado) {

        Usuario usuario = buscarUsuario(emailAutenticado);

        if (usuario.getRol() != RolUsuario.CLIENTE) {
            throw new OperacionNoPermitidaException(
                    "Esta operación está disponible para clientes."
            );
        }

        return pedidoRepository
                .findByCliente_IdOrderByFechaCreacionDesc(usuario.getId())
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    public List<PedidoResponse> listarTodos(String emailAutenticado) {

        Usuario usuario = buscarUsuario(emailAutenticado);

        validarPersonal(usuario);

        return pedidoRepository
                .findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    public PedidoResponse buscarPorCodigo(
            String codigo,
            String emailAutenticado
    ) {

        Usuario usuario = buscarUsuario(emailAutenticado);

        Pedido pedido = pedidoRepository
                .findByCodigoPedido(codigo)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró el pedido solicitado."
                        )
                );

        if (usuario.getRol() == RolUsuario.CLIENTE
                && !pedido.getCliente().getId().equals(usuario.getId())) {

            throw new RecursoNoEncontradoException(
                    "No se encontró el pedido solicitado."
            );
        }

        if (usuario.getRol() != RolUsuario.CLIENTE) {
            validarPersonal(usuario);
        }

        return convertirRespuesta(pedido);
    }

    private Usuario buscarUsuario(String email) {

        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró el usuario autenticado."
                        )
                );
    }

    private void validarPersonal(Usuario usuario) {

        if (usuario.getRol() != RolUsuario.VENDEDOR
                && usuario.getRol() != RolUsuario.ADMINISTRADOR) {

            throw new OperacionNoPermitidaException(
                    "No tienes permisos para consultar todos los pedidos."
            );
        }
    }

    private void validarEntrega(String entrega) {

        if (entrega == null
                || !OPCIONES_ENTREGA.contains(entrega.trim())) {

            throw new DatosInvalidosException(
                    "La opción de entrega seleccionada no es válida."
            );
        }
    }

    private String generarCodigoPedido() {

        String codigo;

        do {
            codigo = "PED" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 12)
                    .toUpperCase(Locale.ROOT);

        } while (pedidoRepository.existsByCodigoPedido(codigo));

        return codigo;
    }

    private PedidoResponse convertirRespuesta(Pedido pedido) {

        ClientePedidoResponse cliente = new ClientePedidoResponse(
                pedido.getNombreCliente(),
                pedido.getEmailCliente(),
                pedido.getDireccionEntrega(),
                pedido.getRegion(),
                pedido.getComuna(),
                pedido.getOpcionEntrega(),
                pedido.getIndicaciones()
        );

        List<ItemPedidoResponse> items = pedido.getDetalles()
                .stream()
                .map(detalle -> new ItemPedidoResponse(
                        detalle.getCodigoProducto(),
                        detalle.getNombreProducto(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getFormatoProducto(),
                        detalle.getImagenProducto(),
                        detalle.getSubtotal()
                ))
                .toList();

        return new PedidoResponse(
                pedido.getCodigoPedido(),
                cliente,
                items,
                pedido.getTotal(),
                pedido.getEstado().getEtiqueta(),
                pedido.getFechaCreacion()
        );
    }

    private record LineaPreparada(
            Producto producto,
            Integer cantidad,
            Long subtotal
    ) {
    }
}