package br.com.finance.manager.api.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.finance.manager.api.payloads.requests.AddEntryRequest;
import br.com.finance.manager.api.payloads.requests.ReverseEntryRequest;
import br.com.finance.manager.api.payloads.responses.EntryResponse;

public interface EntryService {
    EntryResponse add(AddEntryRequest request, String username);
    EntryResponse getById(UUID id);
    Page<EntryResponse> getAll(Pageable pageable);
    EntryResponse reverse(UUID id, ReverseEntryRequest request, String username);
    void deleteById(UUID id);
}
