package com.dtb.accountservice.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts",
        indexes = {
                @Index(name = "idx_iban", columnList = "iban"),
                @Index(name = "idx_bic_swift", columnList = "bic_swift")
        }
        )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID accountId;

    @CreationTimestamp
    @Column(updatable = false, nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "iban", nullable = false, unique = true)
    private String iban;

    @Column(name = "bic_swift", nullable = false)
    private String bicSwift;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
