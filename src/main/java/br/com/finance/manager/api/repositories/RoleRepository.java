package br.com.finance.manager.api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.finance.manager.api.enums.RoleNameEnum;
import br.com.finance.manager.api.models.RoleModel;

public interface RoleRepository extends JpaRepository<RoleModel, UUID> {
    RoleModel findByName(RoleNameEnum name);
}
