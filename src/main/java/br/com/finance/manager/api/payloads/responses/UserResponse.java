package br.com.finance.manager.api.payloads.responses;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;

import br.com.finance.manager.api.models.UserModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private String email;
    private Boolean disabled;
    private List<RoleResponse> roles;

    @SuppressWarnings("null")
    public UserResponse(UserModel userModel) {
        BeanUtils.copyProperties(userModel, this);
        this.roles = userModel.getAuthorities().stream().map(role -> new RoleResponse(role.getAuthority())).toList();
    }
}
