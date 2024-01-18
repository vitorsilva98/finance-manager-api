package br.com.finance.manager.api.controllers;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.finance.manager.api.constants.LogMessagesConstants;
import br.com.finance.manager.api.constants.MethodNamesConstants;
import br.com.finance.manager.api.payloads.requests.AddEntryRequest;
import br.com.finance.manager.api.payloads.requests.ReverseEntryRequest;
import br.com.finance.manager.api.payloads.responses.EntryResponse;
import br.com.finance.manager.api.services.EntryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/finance/entries")
public class EntryController {

    private final EntryService entryService;

    public EntryController(EntryService entryService) {
        this.entryService = entryService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping
    public ResponseEntity<EntryResponse> add(@RequestBody @Valid AddEntryRequest request, HttpServletRequest httpServletRequest, UriComponentsBuilder uriBuilder) {
        log.info(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.ADD_ENTRY, request);
        EntryResponse response = entryService.add(request, httpServletRequest.getAttribute("subject").toString());
        URI getByIdUri = uriBuilder.path("/api/v1/finance/entries/{id}").buildAndExpand(response.getId()).toUri();

        log.info(LogMessagesConstants.OUTPUT_ENDPOINT, MethodNamesConstants.ADD_ENTRY, response);
        return ResponseEntity.created(getByIdUri).body(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<EntryResponse> getById(@PathVariable UUID id) {
        log.info(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.GET_ENTRY_BY_ID, id);
        EntryResponse response = entryService.getById(id);

        log.info(LogMessagesConstants.OUTPUT_ENDPOINT, MethodNamesConstants.GET_ENTRY_BY_ID, response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<Page<EntryResponse>> getAll(@PageableDefault(sort = {"dateTime"}) Pageable pageable) {
        log.info(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.GET_ALL_ENTRIES, pageable);
        Page<EntryResponse> response = entryService.getAll(pageable);

        log.info(LogMessagesConstants.OUTPUT_ENDPOINT, MethodNamesConstants.GET_ALL_ENTRIES, response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PatchMapping("/{id}")
    public ResponseEntity<EntryResponse> reverse(@PathVariable UUID id, @RequestBody @Valid ReverseEntryRequest request, HttpServletRequest httpServletRequest) {
        log.info(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.REVERSE_ENTRY, id);
        EntryResponse response = entryService.reverse(id, request, httpServletRequest.getAttribute("subject").toString());

        log.info(LogMessagesConstants.OUTPUT_ENDPOINT, MethodNamesConstants.REVERSE_ENTRY, response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        log.info(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.DELETE_ENTRY_BY_ID, id);
        entryService.deleteById(id);

        log.info(LogMessagesConstants.OUTPUT_ENDPOINT_NO_CONTENT, MethodNamesConstants.DELETE_ENTRY_BY_ID);
        return ResponseEntity.noContent().build();
    }
}
