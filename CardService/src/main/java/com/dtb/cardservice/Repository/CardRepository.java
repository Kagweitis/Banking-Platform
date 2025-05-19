package com.dtb.cardservice.Repository;

import com.dtb.cardservice.Entity.Card;
import com.dtb.cardservice.Enums.CardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    Optional<Card> findByCardIdAndDeletedFalse(UUID uuid);

    Boolean existsByAccountIdAndCardTypeAndDeletedFalse(UUID accountId, CardType cardType);

    @Query("""
    SELECT c FROM Card c
    WHERE (:alias IS NULL OR LOWER(c.cardAlias) LIKE LOWER(CONCAT('%', :alias, '%')))
      AND (:cardType IS NULL OR c.cardType = :cardType)
      AND (:panSuffix IS NULL OR c.panSuffix = :panSuffix)
      AND c.deleted = false
""")
    Page<Card> searchCards(
            @Param("alias") String alias,
            @Param("cardType") CardType cardType,
            @Param("panSuffix") String panSuffix,
            Pageable pageable);

    @Query("SELECT DISTINCT c.accountId FROM Card c WHERE LOWER(c.cardAlias) LIKE LOWER(CONCAT('%', :cardAlias, '%'))")
    Page<UUID> getAccountIdsByCardAlias(String cardAlias, Pageable pageable);

}
