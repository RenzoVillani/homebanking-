package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.model.Account;
import com.mindhub.homebanking.model.Card;
import com.mindhub.homebanking.model.CardType;
import com.mindhub.homebanking.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;

@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Long> {
    Set<Card>findByClientAndType(Client client, CardType cardType);
    Card findByNumber(String number);
}
