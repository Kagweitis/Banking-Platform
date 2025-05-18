package com.dtb.accountservice.Controller;


import com.dtb.accountservice.DTOs.Requests.CreateAccountRequest;
import com.dtb.accountservice.DTOs.Requests.UpdateCustomerAccountRequest;
import com.dtb.accountservice.DTOs.Responses.GeneralResponse;
import com.dtb.accountservice.DTOs.Responses.GetAccountResponse;
import com.dtb.accountservice.Service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("account/api/v1")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @Operation(

            summary = "Create an account",
            description = "Creates a new customer account on the data in the request."
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Creates a new customer account successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing parameters in request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create/account")
    public GeneralResponse createCustomerAccount(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.createCustomerAccount(request);
    }

    @Operation(

            summary = "Update an account",
            description = "Update customer account based on the data in the request."
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Account updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing parameters in request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/update/account")
    public GeneralResponse updateCustomerAccount(@Valid @RequestBody UpdateCustomerAccountRequest request) {
        return accountService.updateCustomerAccount(request);
    }


    @Operation(

            summary = "Delete an account",
            description = "Delete a customer account based on the ID."
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing parameters in request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/delete/account/{id}")
    public GeneralResponse deleteCustomerAccount(@NotNull @PathVariable UUID id) {
        return accountService.deleteCustomerAccount(id);
    }

    @Operation(

            summary = "Get an account",
            description = "Get a customer account based on the ID."
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Account retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing parameters in request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/get/account/{id}")
    public GetAccountResponse getCustomerAccount(@NotNull @PathVariable UUID id) {
        return accountService.getCustomerAccount(id);
    }

    @Operation(

            summary = "Get accounts by filtering",
            description = "Get customer accounts based on filtering params."
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing parameters in request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/filter/accounts")
    public Page<GetAccountResponse> filterAccounts(
            @RequestParam(required = false) String iban,
            @RequestParam(required = false) String bicSwift,
            @RequestParam(defaultValue = "0") @NotNull Integer page,
            @RequestParam(defaultValue = "10") @NotNull Integer size) {

        return accountService.searchAccounts(iban, bicSwift, page, size);
    }
}
