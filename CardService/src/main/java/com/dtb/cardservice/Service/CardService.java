package com.dtb.cardservice.Service;

import com.dtb.cardservice.DTOs.Requests.CreateCardRequest;
import com.dtb.cardservice.DTOs.Requests.UpdateCardRequest;
import com.dtb.cardservice.DTOs.Responses.GeneralResponse;
import com.dtb.cardservice.DTOs.Responses.GetCardResponse;
import com.dtb.cardservice.Entity.Card;
import com.dtb.cardservice.Enums.CardType;
import com.dtb.cardservice.Exceptions.AlreadyExistsException;
import com.dtb.cardservice.Exceptions.EntityNotFoundException;
import com.dtb.cardservice.Interfaces.AccountServiceClient;
import com.dtb.cardservice.Mappers.CardMapper;
import com.dtb.cardservice.Repository.CardRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final AccountServiceClient accountServiceClient;

    /**
     * Creates a new card for the given account.
     * Validates account existence and checks for duplicate card type for the same account.
     *
     * @param request the card creation request payload
     * @return a general response with creation status
     * @throws AlreadyExistsException if a card of the same type already exists for the account
     * @throws EntityNotFoundException if the account is not found
     */
    @Transactional
    public GeneralResponse createCard(CreateCardRequest request) {
        if (cardRepository.existsByAccountIdAndCardTypeAndDeletedFalse(request.accountId(), CardType.valueOf(request.cardType()))){
            throw new AlreadyExistsException("Card with that account id and card type exists");
        }
        validateAccountId(request.accountId());
        cardRepository.save(cardMapper.dtoToEntity(request));
        return GeneralResponse.builder()
                .message("Card created successfully")
                .status(HttpStatus.CREATED)
                .build();

    }

    /**
     * Validates whether an account exists by invoking the AccountServiceClient.
     *
     * @param id the UUID of the account to validate
     * @throws EntityNotFoundException if the account does not exist or response is invalid
     */
    private void validateAccountId(UUID id){
        ResponseEntity<Boolean> response = accountServiceClient.checkAccountById(id);
        if (!response.getStatusCode().is2xxSuccessful() ||
                response.getBody().equals(Boolean.FALSE)){
            throw new EntityNotFoundException("Account with ID " + id + " not found");
        }
    }

    /**
     * Updates the alias of an existing card.
     *
     * @param request the update card request payload
     * @return a general response with update status
     */
    public GeneralResponse updateCard(UpdateCardRequest request) {
        cardMapper.updateCard(request);
        return GeneralResponse.builder()
                .message("Card updated successfully")
                .status(HttpStatus.CREATED)
                .build();
    }

    /**
     * Soft deletes a card by marking it as deleted and setting the deletion timestamp.
     *
     * @param id the UUID of the card to delete
     * @return a general response with deletion status
     * @throws EntityNotFoundException if the card is not found
     */
    public GeneralResponse deleteCard(UUID id) {
        Card card = cardRepository.findByCardIdAndDeletedFalse(id)
                .orElseThrow(()-> new EntityNotFoundException("Card not found"));
        card.setDeleted(true);
        card.setDeletedAt(LocalDateTime.now());
        cardRepository.save(card);
        return GeneralResponse.builder()
                .message("Card deleted successfully")
                .status(HttpStatus.CREATED)
                .build();
    }


    /**
     * Retrieves card details for the given card ID.
     *
     * @param id the UUID of the card
     * @return the response object containing card details
     * @throws EntityNotFoundException if the card is not found
     */
    public GetCardResponse getCardByID(UUID id) {
        Card card = cardRepository.findByCardIdAndDeletedFalse(id)
                .orElseThrow(()-> new EntityNotFoundException("Card not found"));
        return cardMapper.entityToDTO(card, false);
    }

    /**
     * Retrieves a paginated list of cards filtered by alias, card type, and partial PAN match.
     *
     * @param alias optional card alias filter (e.g., nickname)
     * @param type  optional card type filter (e.g., "DEBIT", "CREDIT")
     * @param pan   optional partial PAN match (e.g., last 4 digits)
     * @param overideMasking determines whether cvv and pan should be masked
     * @param page  0-based page index
     * @param size  number of records per page
     * @return a page of {@link GetCardResponse} matching the criteria
     */
    public Page<GetCardResponse> getCardsByParams(String alias, String type, String pan, Boolean overideMasking, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        CardType cardType = null;
        if (type != null && !type.isEmpty()){
            cardType = CardType.valueOf(type);
        }
        return cardRepository.searchCards(alias, cardType, pan, pageable)
                .map(card ->  cardMapper.entityToDTO(card, overideMasking));
    }

    public Page<UUID> getAccountIds(String alias, @NotNull Integer page, @NotNull Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.getAccountIdsByCardAlias(alias, pageable);
    }
}
