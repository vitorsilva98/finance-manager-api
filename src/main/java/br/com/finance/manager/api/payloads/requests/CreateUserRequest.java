package br.com.finance.manager.api.payloads.requests;

import java.io.Serializable;
import java.util.List;

import br.com.finance.manager.api.enums.RoleNameEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateUserRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Email
    @NotBlank
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @NotBlank
    @ToString.Exclude
    private String password;

    @NotNull
    @Size(min = 1, max = 2)
    private List<RoleNameEnum> roles;
}
