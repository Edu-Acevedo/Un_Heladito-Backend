package cl.duoc.unheladito.repository;

import cl.duoc.unheladito.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    boolean existsByCodigoPedido(String codigoPedido);

    Optional<Pedido> findByCodigoPedido(String codigoPedido);

    List<Pedido> findAllByOrderByFechaCreacionDesc();

    List<Pedido> findByCliente_IdOrderByFechaCreacionDesc(Long clienteId);

    Optional<Pedido> findByCodigoPedidoAndCliente_Id(
            String codigoPedido,
            Long clienteId
    );
}
