package com.dtb.cardservice.Entity;

import com.dtb.cardservice.Enums.CardType;
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
@Table(name = "cards",
        indexes = {
                @Index(name = "idx_card_alias", columnList = "card_alias"),
                @Index(name = "idx_pan", columnList = "pan"),
                @Index(name = "idx_card_type", columnList = "card_type")
        })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID cardId;

    @CreationTimestamp
    @Column(updatable = false, nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "card_alias", nullable = false)
    private String cardAlias;

    @Column(name = "account_id", nullable = false, updatable = false)
    private UUID accountId;

    @Column(name = "card_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Column(name = "pan", nullable = false, updatable = false)
    private String pan;

    @Column(name = "pan_suffix", nullable = false, updatable = false)
    private String panSuffix;

    @Column(name = "cvv", nullable = false, updatable = false)
    private String cvv;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


}
