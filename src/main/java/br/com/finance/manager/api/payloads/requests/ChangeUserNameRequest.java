package br.com.finance.manager.api.payloads.requests;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserNameRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;
}
