package com.dtb.cardservice.Repository;

import com.dtb.cardservice.Entity.Card;
import com.dtb.cardservice.Enums.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    Optional<Card> findByCardIdAndDeletedFalse(UUID uuid);

    Boolean existsByAccountIdAndCardTypeAndDeletedFalse(UUID accountId, CardType cardType);
}
