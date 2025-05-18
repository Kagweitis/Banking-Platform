package com.dtb.cardservice.Mappers;

import com.dtb.cardservice.DTOs.Requests.CreateCardRequest;
import com.dtb.cardservice.DTOs.Requests.UpdateCardRequest;
import com.dtb.cardservice.DTOs.Responses.GetCardResponse;
import com.dtb.cardservice.Entity.Card;
import com.dtb.cardservice.Enums.CardType;
import com.dtb.cardservice.Exceptions.EntityNotFoundException;
import com.dtb.cardservice.Repository.CardRepository;
import com.dtb.cardservice.Util.EncryptionService;
import com.dtb.cardservice.Util.MaskUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardMapper {

    private final CardRepository cardRepository;
    private final EncryptionService encryptionService;
    private final MaskUtil maskUtil;

    public Card dtoToEntity(CreateCardRequest request){
        return Card.builder()
                .cvv(encryptionService.encrypt(request.cvv()))
                .pan(encryptionService.encrypt(request.pan()))
                .accountId(request.accountId())
                .cardAlias(request.cardAlias())
                .cardType(CardType.valueOf(request.cardType()))
                .build();
    }

    @Transactional
    public void updateCard(UpdateCardRequest request){
        Card card = cardRepository.findByCardIdAndDeletedFalse(request.cardId())
                .orElseThrow(()-> new EntityNotFoundException("Card not found"));
        card.setCardAlias(request.cardAlias());
        cardRepository.save(card);
    }

    public GetCardResponse entityToDTO(Card card){
        return GetCardResponse.builder()
                .accountId(card.getAccountId())
                .cardAlias(card.getCardAlias())
                .cardType(card.getCardType().name())
                .cvv(maskUtil.maskCvv(encryptionService.decrypt(card.getCvv())))
                .pan(maskUtil.maskPan(encryptionService.decrypt(card.getPan())))
                .build();
    }
}
