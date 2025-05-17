package com.dtb.accountservice.Controller;


import com.dtb.accountservice.DTOs.Requests.CreateAccountRequest;
import com.dtb.accountservice.DTOs.Responses.GeneralResponse;
import com.dtb.accountservice.Service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}
