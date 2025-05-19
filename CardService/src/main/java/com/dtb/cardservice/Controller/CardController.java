package com.dtb.cardservice.Controller;

import com.dtb.cardservice.DTOs.Requests.CreateCardRequest;
import com.dtb.cardservice.DTOs.Requests.UpdateCardRequest;
import com.dtb.cardservice.DTOs.Responses.GeneralResponse;
import com.dtb.cardservice.DTOs.Responses.GetCardResponse;
import com.dtb.cardservice.Service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/card/api/v1")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @Operation(

            summary = "Create a card",
            description = "Creates a card and ties it to an account."
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Created a new card successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing parameters in request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create/card")
    public GeneralResponse createCard(@Valid @RequestBody CreateCardRequest request) {
        return cardService.createCard(request);
    }

    @Operation(

            summary = "Update a card",
            description = "Updates a card based on params."
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Updated card successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing parameters in request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/update/card")
    public GeneralResponse updateCard(@Valid @RequestBody UpdateCardRequest request) {
        return cardService.updateCard(request);
    }

    @Operation(

            summary = "Delete a card",
            description = "Deletes a card based on id."
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "deleted card successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing parameters in request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/delete/card/{id}")
    public GeneralResponse deleteCard(@NotNull @PathVariable UUID id) {
        return cardService.deleteCard(id);
    }

    @Operation(

            summary = "Get a card by id",
            description = "Gets a card based on id."
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Retrieved card successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing parameters in request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/get/card/{id}")
    public GetCardResponse getCard(@NotNull @PathVariable UUID id) {
        return cardService.getCardByID(id);
    }

    @Operation(

            summary = "Search cards",
            description = "Search cards based on params."
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Retrieved cards successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing parameters in request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search/cards")
    public Page<GetCardResponse> searchCards(
            @RequestParam(required = false) String cardAlias,
            @RequestParam(required = false) String cardType,
            @RequestParam(required = false) String pan,
            @RequestParam(required = false) Boolean mask,
            @RequestParam(defaultValue = "0") @NotNull Integer page,
            @RequestParam(defaultValue = "10") @NotNull Integer size
    ) {
        return cardService.getCardsByParams(cardAlias, cardType, pan, mask, page, size);
    }

    @Operation(
            summary = "Get account ids",
            description = "Get account IDs based on card alias"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Account ids found successfully."),
                    @ApiResponse(responseCode = "400", description = "Invalid or missing parameters in the request"),
                    @ApiResponse(responseCode = "404", description = "Account not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/get/account/ids")
    @ResponseStatus(HttpStatus.OK)
    public Page<UUID> getAccountIds(
            @RequestParam String alias,
            @RequestParam(defaultValue = "0") @NotNull Integer page,
            @RequestParam(defaultValue = "10") @NotNull Integer size
    ) {
        return cardService.getAccountIds(alias, page, size);
    }


}
