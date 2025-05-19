package com.dtb.cardservice;


import com.dtb.cardservice.DTOs.Requests.CreateCardRequest;
import com.dtb.cardservice.DTOs.Requests.UpdateCardRequest;
import com.dtb.cardservice.DTOs.Responses.GeneralResponse;
import com.dtb.cardservice.DTOs.Responses.GetCardResponse;
import com.dtb.cardservice.Entity.Card;
import com.dtb.cardservice.Exceptions.AlreadyExistsException;
import com.dtb.cardservice.Exceptions.EntityNotFoundException;
import com.dtb.cardservice.Interfaces.AccountServiceClient;
import com.dtb.cardservice.Mappers.CardMapper;
import com.dtb.cardservice.Repository.CardRepository;
import com.dtb.cardservice.Service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardMapper cardMapper;
    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCard_shouldCreateCard_whenValid() {
        UUID accountId = UUID.randomUUID();
        CreateCardRequest request = new CreateCardRequest("Test Alias", accountId, "123", "1234567890123456", "VIRTUAL");

        when(cardRepository.existsByAccountIdAndCardTypeAndDeletedFalse(any(), any())).thenReturn(false);
        when(accountServiceClient.checkAccountById(accountId))
                .thenReturn(ResponseEntity.ok(true));

        when(cardMapper.dtoToEntity(request)).thenReturn(new Card());

        GeneralResponse response = cardService.createCard(request);

        assertThat(response.message()).isEqualTo("Card created successfully");
        assertThat(response.status()).isEqualTo(HttpStatus.CREATED);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_shouldThrowAlreadyExists_whenDuplicate() {
        CreateCardRequest request = new CreateCardRequest("Test Alias", UUID.randomUUID(), "123", "1234567890123456", "VIRTUAL");

        when(cardRepository.existsByAccountIdAndCardTypeAndDeletedFalse(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> cardService.createCard(request))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("Card with that account id and card type exists");
    }

    @Test
    void updateCard_shouldUpdateCardAlias() {
        UpdateCardRequest request = new UpdateCardRequest(UUID.randomUUID(), "New Alias");

        GeneralResponse response = cardService.updateCard(request);

        assertThat(response.message()).isEqualTo("Card updated successfully");
        verify(cardMapper).updateCard(request);
    }

    @Test
    void deleteCard_shouldSoftDeleteCard_whenExists() {
        UUID cardId = UUID.randomUUID();
        Card card = new Card();
        card.setCardId(cardId);
        card.setDeleted(false);

        when(cardRepository.findByCardIdAndDeletedFalse(cardId)).thenReturn(Optional.of(card));

        GeneralResponse response = cardService.deleteCard(cardId);

        assertThat(card.getDeleted()).isTrue();
        assertThat(response.message()).isEqualTo("Card deleted successfully");
        verify(cardRepository).save(card);
    }

    @Test
    void deleteCard_shouldThrowException_whenCardNotFound() {
        UUID cardId = UUID.randomUUID();

        when(cardRepository.findByCardIdAndDeletedFalse(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.deleteCard(cardId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Card not found");
    }

    @Test
    void getCardByID_shouldReturnCard_whenExists() {
        UUID cardId = UUID.randomUUID();
        Card card = new Card();
        GetCardResponse dto = new GetCardResponse("Alias", "123", "123456", UUID.randomUUID(), "VIRTUAL");

        when(cardRepository.findByCardIdAndDeletedFalse(cardId)).thenReturn(Optional.of(card));
        when(cardMapper.entityToDTO(card, false)).thenReturn(dto);

        GetCardResponse response = cardService.getCardByID(cardId);

        assertThat(response.cardAlias()).isEqualTo("Alias");
        verify(cardMapper).entityToDTO(card, false);
    }

    @Test
    void getCardsByParams_shouldReturnMatchingCards() {
        Pageable pageable = PageRequest.of(0, 10);
        Card card = new Card();
        Page<Card> page = new PageImpl<>(List.of(card));
        GetCardResponse dto = new GetCardResponse("Alias", "123", "4567", UUID.randomUUID(), "VIRTUAL");

        when(cardRepository.searchCards(any(), any(), any(), eq(pageable))).thenReturn(page);
        when(cardMapper.entityToDTO(any(Card.class), eq(true))).thenReturn(dto);

        Page<GetCardResponse> result = cardService.getCardsByParams("Alias", "VIRTUAL", "4567", true, 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getAccountIds_shouldReturnAccountIds() {
        UUID accountId = UUID.randomUUID();
        Page<UUID> page = new PageImpl<>(List.of(accountId));

        when(cardRepository.getAccountIdsByCardAlias(eq("test"), any(Pageable.class)))
                .thenReturn(page);

        Page<UUID> result = cardService.getAccountIds("test", 0, 10);

        assertThat(result.getContent()).contains(accountId);
    }
}

