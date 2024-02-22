package br.com.finance.manager.api.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.finance.manager.api.configs.exception.EntityNotFoundException;
import br.com.finance.manager.api.configs.exception.InvalidEntityDataException;
import br.com.finance.manager.api.enums.RoleNameEnum;
import br.com.finance.manager.api.models.RoleModel;
import br.com.finance.manager.api.models.UserModel;
import br.com.finance.manager.api.payloads.requests.ChangeUserNameRequest;
import br.com.finance.manager.api.payloads.requests.CreateUserRequest;
import br.com.finance.manager.api.payloads.requests.UpdateUserRequest;
import br.com.finance.manager.api.payloads.responses.UserResponse;
import br.com.finance.manager.api.repositories.RoleRepository;
import br.com.finance.manager.api.repositories.UserRepository;
import br.com.finance.manager.api.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    private static final String USER_CACHE_KEY = "users";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = USER_CACHE_KEY, allEntries = true)
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidEntityDataException("User already exists");
        }

        UserModel userModel = new UserModel();
        userModel.setDisabled(false);
        userModel.setRoles(getRoles(request.getRoles()));
        userModel.setName(request.getName());
        userModel.setEmail(request.getEmail());
        userModel.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
        return new UserResponse(userRepository.save(userModel));
    }

    @Override
    public UserResponse getById(UUID id) {
        return new UserResponse(findById(id));
    }

    @Override
    @Cacheable(cacheNames = USER_CACHE_KEY)
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::new);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = USER_CACHE_KEY, allEntries = true)
    public UserResponse update(UUID id, UpdateUserRequest request) {
        UserModel userModel = findById(id);

        if (request.getDisabled() != null) {
            userModel.setDisabled(request.getDisabled());
        }

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            userModel.setRoles(getRoles(request.getRoles()));
        }

        return new UserResponse(userModel);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = USER_CACHE_KEY, allEntries = true)
    public UserResponse changeName(ChangeUserNameRequest request, String username) {
        /* It is not necessary to validate the Optional because this call is already made when validating the token */
        Optional<UserModel> userModelOptional = userRepository.findByEmail(username);
        UserModel userModel = userModelOptional.get();
        userModel.setName(request.getName());
        return new UserResponse(userModel);
    }

    private List<RoleModel> getRoles(List<RoleNameEnum> roles) {
        List<RoleModel> userRoles = new ArrayList<>();

        for (RoleNameEnum roleName : roles) {
            /* Optional is not used because the roles are pre-registered in the database and the enum is already validated in request */
            RoleModel roleModel = roleRepository.findByName(roleName);
            userRoles.add(roleModel);
        }

        return userRoles;
    }

    private UserModel findById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User doesn't exists"));
    }
}
