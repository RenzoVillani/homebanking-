package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.model.Card;
import com.mindhub.homebanking.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByEmail(String email);
    Client findByCards(Card card);
}