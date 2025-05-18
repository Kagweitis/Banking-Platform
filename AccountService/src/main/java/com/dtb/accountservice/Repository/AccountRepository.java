package com.dtb.accountservice.Repository;

import com.dtb.accountservice.Entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountIdAndDeletedFalse(UUID uuid);

    Boolean existsByIbanAndDeletedFalse(String iban);

    Boolean existsByAccountIdAndDeletedFalse(UUID accountId);

    @Query("SELECT a FROM Account a " +
            "WHERE (:iban IS NULL OR a.iban = :iban) " +
            "AND (:bicSwift IS NULL OR a.bicSwift = :bicSwift) " +
            "AND a.deleted = false")
    Page<Account> findByIbanAndBicSwift(
            @Param("iban") String iban,
            @Param("bicSwift") String bicSwift,
            Pageable pageable);
}
