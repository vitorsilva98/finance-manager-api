package br.com.finance.manager.api.payloads.requests;

import java.io.Serializable;
import java.util.List;

import br.com.finance.manager.api.enums.RoleNameEnum;

import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdateUserRequest implements Serializable {

   private static final long serialVersionUID = 1L;

   private Boolean disabled;

   @Size(max = 2)
   private List<RoleNameEnum> roles;
}
