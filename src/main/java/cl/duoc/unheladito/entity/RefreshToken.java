package cl.duoc.unheladito.entity;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_family", columnList = "family_id"),
                @Index(name = "idx_refresh_user", columnList = "usuario_id")
        }
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "family_id", nullable = false, length = 36)
    private String familyId;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "expira_en", nullable = false)
    private Instant expiraEn;

    // NULL significa que todavía está activo.
    @Column(name = "revocado_en")
    private Instant revocadoEn;

    public RefreshToken() {
    }

    public RefreshToken(
            Usuario usuario,
            String tokenHash,
            String familyId,
            Instant expiraEn
    ) {
        this.usuario = usuario;
        this.tokenHash = tokenHash;
        this.familyId = familyId;
        this.creadoEn = Instant.now();
        this.expiraEn = expiraEn;
    }

    @PrePersist
    protected void antesDeGuardar() {
        if (this.creadoEn == null) {
            this.creadoEn = Instant.now();
        }
    }

    public void revocar(Instant momento) {
        if (this.revocadoEn == null) {
            this.revocadoEn = momento;
        }
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public String getFamilyId() {
        return familyId;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public Instant getExpiraEn() {
        return expiraEn;
    }

    public Instant getRevocadoEn() {
        return revocadoEn;
    }
}