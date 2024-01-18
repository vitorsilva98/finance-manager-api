package br.com.finance.manager.api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.finance.manager.api.models.EntryModel;

public interface EntryRepository extends JpaRepository<EntryModel, UUID> {
}
