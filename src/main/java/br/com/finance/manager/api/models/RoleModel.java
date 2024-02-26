package br.com.finance.manager.api.models;

import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;

import br.com.finance.manager.api.enums.RoleNameEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class RoleModel implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false, unique = true)
    private RoleNameEnum name;

    @Override
    public String getAuthority() {
        return this.name.toString();
    }
}
