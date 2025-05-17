package com.dtb.customerservice.Repository;

import com.dtb.customerservice.Entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByCustomerIdAndDeletedFalse(UUID customerId);

    @Query(value = "SELECT * FROM customers WHERE " +
            "to_tsvector('english', first_name || ' ' || last_name || ' ' || COALESCE(other_name, '')) " +
            "@@ plainto_tsquery('english', :searchTerm) " +
            "AND created_at BETWEEN :startDate AND :endDate",
            countQuery = "SELECT count(*) FROM customers WHERE " +
                    "to_tsvector('english', first_name || ' ' || last_name || ' ' || COALESCE(other_name, '')) " +
                    "@@ plainto_tsquery('english', :searchTerm) " +
                    "AND created_at BETWEEN :startDate AND :endDate",
            nativeQuery = true)
    Page<Customer> searchCustomers(
            @Param("searchTerm") String searchTerm,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT c FROM Customer c " +
            "WHERE (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(c.otherName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND c.createdAt BETWEEN :startDate AND :endDate")
    Page<Customer> fuzzySearchByNameAndDateRange(
            @Param("name") String name,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );


}
