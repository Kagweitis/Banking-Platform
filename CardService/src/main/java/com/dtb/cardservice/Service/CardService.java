package com.dtb.cardservice.Service;

import com.dtb.cardservice.DTOs.Requests.CreateCardRequest;
import com.dtb.cardservice.DTOs.Requests.UpdateCardRequest;
import com.dtb.cardservice.DTOs.Responses.GeneralResponse;
import com.dtb.cardservice.Enums.CardType;
import com.dtb.cardservice.Exceptions.AlreadyExistsException;
import com.dtb.cardservice.Exceptions.EntityNotFoundException;
import com.dtb.cardservice.Interfaces.AccountServiceClient;
import com.dtb.cardservice.Mappers.CardMapper;
import com.dtb.cardservice.Repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final AccountServiceClient accountServiceClient;

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

    private void validateAccountId(UUID id){
        ResponseEntity<Boolean> response = accountServiceClient.checkAccountById(id);
        if (!response.getStatusCode().is2xxSuccessful() ||
                response.getBody().equals(Boolean.FALSE)){
            throw new EntityNotFoundException("Account with ID " + id + " not found");
        }
    }


    public GeneralResponse updateCard(UpdateCardRequest request) {
        cardMapper.updateCard(request);
        return GeneralResponse.builder()
                .message("Card updated successfully")
                .status(HttpStatus.CREATED)
                .build();
    }
}
