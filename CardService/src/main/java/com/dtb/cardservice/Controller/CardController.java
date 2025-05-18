package com.dtb.cardservice.Controller;

import com.dtb.cardservice.DTOs.Requests.CreateCardRequest;
import com.dtb.cardservice.DTOs.Requests.UpdateCardRequest;
import com.dtb.cardservice.DTOs.Responses.GeneralResponse;
import com.dtb.cardservice.Service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/update/card")
    public GeneralResponse updateCard(@Valid @RequestBody UpdateCardRequest request) {
        return cardService.updateCard(request);
    }

}
