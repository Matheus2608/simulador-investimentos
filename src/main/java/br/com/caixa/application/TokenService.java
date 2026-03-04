package br.com.caixa.application;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class TokenService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @ConfigProperty(name = "jwt.duration.seconds", defaultValue = "3600")
    long durationSeconds;

    public String gerarToken(Long clienteId, String email) {
        return Jwt.issuer(issuer)
                .subject(clienteId.toString())
                .upn(email)
                .groups(Set.of("user"))
                .expiresIn(Duration.ofSeconds(durationSeconds))
                .sign();
    }
}
