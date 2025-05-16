package com.dtb.customerservice.Mappers;

import com.dtb.customerservice.DTOs.Requests.CreateCustomerRequest;
import com.dtb.customerservice.Entities.Customer;
import com.dtb.customerservice.Repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerMapper {

    private final CustomerRepository customerRepository;

    public Customer dtoToEntity(CreateCustomerRequest request){
        return Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .otherName(request.otherName() != null ? request.otherName() : null)
                .build();
    }
}
