package cl.duoc.unheladito.repository;

import java.util.List;
import java.util.Optional;
import cl.duoc.unheladito.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.unheladito.entity.RolUsuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findAllByOrderByFechaCreacionDesc();

    long countByRolAndActivoTrue(RolUsuario rol);
}
