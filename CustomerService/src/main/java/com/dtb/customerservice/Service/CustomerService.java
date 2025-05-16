package com.dtb.customerservice.Service;

import com.dtb.customerservice.DTOs.Requests.CreateCustomerRequest;
import com.dtb.customerservice.DTOs.Responses.GeneralResponse;
import com.dtb.customerservice.Mappers.CustomerMapper;
import com.dtb.customerservice.Repository.CustomerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
}
