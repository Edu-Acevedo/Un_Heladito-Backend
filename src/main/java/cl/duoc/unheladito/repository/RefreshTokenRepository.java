package cl.duoc.unheladito.repository;

import java.time.Instant;
import java.util.Optional;
import cl.duoc.unheladito.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r
        FROM RefreshToken r
        JOIN FETCH r.usuario
        WHERE r.tokenHash = :hash
        """)
    Optional<RefreshToken> buscarParaRenovar(
            @Param("hash") String hash
    );

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
        UPDATE RefreshToken r
        SET r.revocadoEn = :momento
        WHERE r.familyId = :familyId
        AND r.revocadoEn IS NULL
        """)
    int revocarFamilia(
            @Param("familyId") String familyId,
            @Param("momento") Instant momento
    );
}