package com.dtb.customerservice.Controller;


import com.dtb.customerservice.DTOs.Requests.CreateCustomerRequest;
import com.dtb.customerservice.DTOs.Responses.GeneralResponse;
import com.dtb.customerservice.DTOs.Responses.GetCustomerResponse;
import com.dtb.customerservice.Service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers/api/v1")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @Operation(

            summary = "Create a customer",
            description = "Creates a new customer based on the data in the request."
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Creates a new customer successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing parameters in request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create/customer")
    public GeneralResponse createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return customerService.createCustomer(request);
    }

    @Operation(
            summary = "Get Customer",
            description = "Get a customer based on their id."
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Customer found successfully."),
                    @ApiResponse(responseCode = "400", description = "Invalid or missing parameters in the request"),
                    @ApiResponse(responseCode = "404", description = "Customer not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/get/customer/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GetCustomerResponse getCustomerById(@PathVariable @NotNull UUID id) {
        return customerService.getUserById(id);
    }


}
