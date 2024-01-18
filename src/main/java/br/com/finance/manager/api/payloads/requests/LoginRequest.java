package br.com.finance.manager.api.payloads.requests;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Email
    @NotBlank
    @Size(max = 100)
    private String email;

    @NotBlank
    @ToString.Exclude
    private String password;
}
