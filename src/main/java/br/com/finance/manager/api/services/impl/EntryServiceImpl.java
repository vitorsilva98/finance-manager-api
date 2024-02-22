package br.com.finance.manager.api.services.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.finance.manager.api.configs.exception.BusinessRuleException;
import br.com.finance.manager.api.configs.exception.EntityNotFoundException;
import br.com.finance.manager.api.configs.exception.InvalidEntityDataException;
import br.com.finance.manager.api.enums.RoleNameEnum;
import br.com.finance.manager.api.models.CategoryModel;
import br.com.finance.manager.api.models.EntryModel;
import br.com.finance.manager.api.models.UserModel;
import br.com.finance.manager.api.payloads.requests.AddEntryRequest;
import br.com.finance.manager.api.payloads.requests.ReverseEntryRequest;
import br.com.finance.manager.api.payloads.responses.EntryResponse;
import br.com.finance.manager.api.repositories.CategoryRepository;
import br.com.finance.manager.api.repositories.EntryRepository;
import br.com.finance.manager.api.repositories.UserRepository;
import br.com.finance.manager.api.services.EntryService;

@Service
public class EntryServiceImpl implements EntryService {

    private final UserRepository userRepository;
    private final EntryRepository entryRepository;
    private final CategoryRepository categoryRepository;

    public EntryServiceImpl(UserRepository userRepository, EntryRepository entryRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.entryRepository = entryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public EntryResponse add(AddEntryRequest request, String username) {
        Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(request.getCategoryId());
        if (!categoryModelOptional.isPresent()) {
            throw new InvalidEntityDataException("Category doesn't exists");
        }

        EntryModel entryModel = new EntryModel();
        entryModel.setAmount(request.getAmount());
        entryModel.setCategory(categoryModelOptional.get());
        entryModel.setDateTime(request.getDateTime() != null ? request.getDateTime() : LocalDateTime.now());
        entryModel.setDescription(request.getDescription());
        entryModel.setPaymentMethod(request.getPaymentMethod());
        entryModel.setType(request.getType());
        /* It is not necessary to validate the Optional because this call is already made when validating the token */
        entryModel.setUser(userRepository.findByEmail(username).get());
        return new EntryResponse(entryRepository.save(entryModel));
    }

    @Override
    public EntryResponse getById(UUID id) {
        return new EntryResponse(findById(id));
    }

    @Override
    public Page<EntryResponse> getAll(Pageable pageable) {
        return entryRepository.findAll(pageable).map(EntryResponse::new);
    }

    @Override
    @Transactional
    public EntryResponse reverse(UUID id, ReverseEntryRequest request, String username) {
        EntryModel entryModel = findById(id);
        if (!entryModel.getUser().getUsername().equals(username)) {
            /* It is not necessary to validate the Optional because this call is already made when validating the token */
            Optional<UserModel> userModelOption = userRepository.findByEmail(username);
            if (userModelOption.get().getRoles().stream()
                .noneMatch(role -> role.getName().equals(RoleNameEnum.ROLE_ADMIN))) {
                    throw new BusinessRuleException("Users are only allowed to reverse their own entries or you must have ADMIN permission");
            }
        }

        entryModel.setReversed(true);
        entryModel.setReversalDateTime(request.getReversalDateTime() != null ? request.getReversalDateTime() : LocalDateTime.now());
        return new EntryResponse(entryModel);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        EntryModel entryModel = findById(id);
        entryRepository.delete(entryModel);
    }

    private EntryModel findById(UUID id) {
        return entryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Entry doesn't exists"));
    }
}
