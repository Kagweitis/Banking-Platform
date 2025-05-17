package com.dtb.customerservice.Mappers;

import com.dtb.customerservice.DTOs.Requests.CreateCustomerRequest;
import com.dtb.customerservice.DTOs.Requests.UpdateCustomerRequest;
import com.dtb.customerservice.DTOs.Responses.GetCustomerResponse;
import com.dtb.customerservice.Entities.Customer;
import com.dtb.customerservice.Exceptions.EntityNotFoundException;
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

    public GetCustomerResponse getCustomerResponse(Customer customer){
        return GetCustomerResponse.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .otherName(customer.getOtherName() != null ? customer.getOtherName() : null)
                .build();
    }

    public void updateCustomer(UpdateCustomerRequest request){
        Customer customer = customerRepository.findByCustomerIdAndDeletedFalse(request.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        customer.setFirstName(request.firstName() != null ? request.firstName() : customer.getFirstName());
        customer.setLastName(request.lastName() != null ? request.lastName() : customer.getLastName());
        customer.setOtherName(request.otherName() != null ? request.otherName() : customer.getOtherName());
        customerRepository.save(customer);
    }
}
