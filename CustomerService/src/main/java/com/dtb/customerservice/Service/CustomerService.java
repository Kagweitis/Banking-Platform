package com.dtb.customerservice.Service;

import com.dtb.customerservice.DTOs.Requests.CreateCustomerRequest;
import com.dtb.customerservice.DTOs.Responses.GeneralResponse;
import com.dtb.customerservice.DTOs.Responses.GetCustomerResponse;
import com.dtb.customerservice.Entities.Customer;
import com.dtb.customerservice.Exceptions.EntityNotFoundException;
import com.dtb.customerservice.Mappers.CustomerMapper;
import com.dtb.customerservice.Repository.CustomerRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public GeneralResponse createCustomer(@Valid CreateCustomerRequest request) {
        customerRepository.save(customerMapper.dtoToEntity(request));
        return GeneralResponse.builder()
                .message("Customer created successfully")
                .status(HttpStatus.CREATED)
                .build();
    }

    public GetCustomerResponse getUserById(@NotBlank UUID id) {
        Customer customer = customerRepository.findByCustomerIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        return customerMapper.getCustomerResponse(customer);
    }
}
