package br.com.finance.manager.api.payloads.responses;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class RoleResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
}
