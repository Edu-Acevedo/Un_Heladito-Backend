package cl.duoc.unheladito.security;

import java.util.List;
import cl.duoc.unheladito.entity.Usuario;
import cl.duoc.unheladito.repository.UsuarioRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class UsuarioJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UsuarioRepository usuarioRepository;

    public UsuarioJwtAuthenticationConverter(
            UsuarioRepository usuarioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Object userIdClaim = jwt.getClaims().get("userId");

        if (!(userIdClaim instanceof Number userId)) {
            throw new InvalidBearerTokenException(
                    "El token no contiene un identificador de usuario válido."
            );
        }

        Usuario usuario = usuarioRepository.findById(userId.longValue())
                .orElseThrow(() ->
                        new InvalidBearerTokenException(
                                "El usuario del token ya no existe."
                        )
                );

        if (!usuario.isActivo()) {
            throw new InvalidBearerTokenException(
                    "La cuenta está desactivada."
            );
        }

        if (!usuario.getEmail().equalsIgnoreCase(jwt.getSubject())) {
            throw new InvalidBearerTokenException(
                    "La identidad del token no coincide con el usuario."
            );
        }

        var authorities = List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + usuario.getRol().name()
                )
        );

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                usuario.getEmail()
        );
    }
}