package com.dtb.customerservice.Controller;


import com.dtb.customerservice.DTOs.Requests.CreateCustomerRequest;
import com.dtb.customerservice.DTOs.Responses.GeneralResponse;
import com.dtb.customerservice.Service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customers/api/v1")
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
    @PostMapping("/create/customer")
    public GeneralResponse createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return customerService.createCustomer(request);
    }
}
