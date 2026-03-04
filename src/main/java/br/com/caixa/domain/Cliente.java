package br.com.caixa.domain;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import java.util.Optional;

@Entity
@Table(name = "clientes")
public class Cliente extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String nome;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(name = "senha_hash", nullable = false)
    public String senhaHash;

    public static Cliente registrar(String nome, String email, String senha) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome nao pode ser nulo ou vazio");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email nao pode ser nulo ou vazio");
        }
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("senha nao pode ser nula ou vazia");
        }

        Cliente cliente = new Cliente();
        cliente.nome = nome.strip();
        cliente.email = email.strip().toLowerCase();
        cliente.senhaHash = BcryptUtil.bcryptHash(senha);
        cliente.persist();
        return cliente;
    }

    public static Optional<Cliente> autenticar(String email, String senha) {
        email = email.strip().toLowerCase();
        Optional<Cliente> cliente = findByEmail(email);

        if (cliente.isEmpty()) {
            return Optional.empty();
        }
        if (!cliente.get().verificarSenha(senha)) {
            return Optional.empty();
        }
        return cliente;
    }

    public boolean verificarSenha(String senhaPlana) {
        try {
            PasswordFactory factory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT);
            BCryptPassword bcrypt = (BCryptPassword) factory.translate(ModularCrypt.decode(senhaHash));
            return factory.verify(bcrypt, senhaPlana.toCharArray());
        } catch (Exception e) {
            return false;
        }
    }

    public static Optional<Cliente> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}
