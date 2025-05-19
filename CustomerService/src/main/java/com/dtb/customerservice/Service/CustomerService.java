package com.dtb.customerservice.Service;

import com.dtb.customerservice.DTOs.Requests.CreateCustomerRequest;
import com.dtb.customerservice.DTOs.Requests.UpdateCustomerRequest;
import com.dtb.customerservice.DTOs.Responses.GeneralResponse;
import com.dtb.customerservice.DTOs.Responses.GetCustomerResponse;
import com.dtb.customerservice.Entities.Customer;
import com.dtb.customerservice.Exceptions.EntityNotFoundException;
import com.dtb.customerservice.Mappers.CustomerMapper;
import com.dtb.customerservice.Repository.CustomerRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    /**
     * Creates a new customer from the provided request data.
     *
     * @param request the validated DTO containing customer details (name, email, etc.)
     * @return a {@link GeneralResponse} with success message and {@code 201 CREATED} status
     * @throws IllegalArgumentException if validation constraints are violated (handled by Spring)
     */
    @Transactional
    public GeneralResponse createCustomer(@Valid CreateCustomerRequest request) {
        customerRepository.save(customerMapper.dtoToEntity(request));
        return GeneralResponse.builder()
                .message("Customer created successfully")
                .status(HttpStatus.CREATED)
                .build();
    }

    /**
     * Retrieves a customer's information using their unique ID.
     *
     * @param id the UUID of the customer to retrieve
     * @return the customer's data wrapped in a {@link GetCustomerResponse} DTO
     * @throws EntityNotFoundException if the customer does not exist or is marked as deleted
     */
    public GetCustomerResponse getUserById(@NotNull UUID id) {
        Customer customer = customerRepository.findByCustomerIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        return customerMapper.getCustomerResponse(customer);
    }

    /**
     * Performs a paginated, fuzzy search of customers by name and optional date range.
     * This is useful for administrative or reporting purposes.
     *
     * @param name      partial or full name of the customer (case-insensitive)
     * @param startDate optional start of creation timestamp filter
     * @param endDate   optional end of creation timestamp filter
     * @param page      0-based page index
     * @param size      number of items per page
     * @return a {@link Page} of {@link GetCustomerResponse} records matching the criteria
     */
    public Page<GetCustomerResponse> getUsersByParams(String name, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.fuzzySearchByNameAndDateRange(name, startDate, endDate, pageable)
                .map(customerMapper::getCustomerResponse);
    }

    /**
     * Updates an existing customer's details.
     * Only updatable fields (e.g. name, email) are modified.
     *
     * @param request the DTO containing new customer details
     * @return a {@link GeneralResponse} indicating success and {@code 200 OK} status
     * @throws EntityNotFoundException if the customer to update does not exist or is deleted
     */
    @Transactional
    public GeneralResponse updateCustomer(UpdateCustomerRequest request) {
        customerMapper.updateCustomer(request);
        return GeneralResponse.builder()
                .message("Customer updated successfully")
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Performs a soft delete on a customer by setting the {@code deleted} flag
     * and recording the deletion timestamp.
     *
     * @param id the UUID of the customer to delete
     * @return a {@link GeneralResponse} with deletion success message
     * @throws EntityNotFoundException if the customer does not exist or is already deleted
     */
    @Transactional
    public GeneralResponse deleteCustomer(UUID id) {
        Customer customer = customerRepository.findByCustomerIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        customer.setDeleted(true);
        customer.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customer);
        return GeneralResponse.builder()
                .message("Customer deleted successfully")
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Checks if a customer exists and is not marked as deleted.
     * Primarily used for cross-service validations or business rules.
     *
     * @param id the UUID of the customer to check
     * @return {@code true} if the customer exists and is active
     * @throws EntityNotFoundException if the customer does not exist or is deleted
     */
    public Boolean checkCustomerExists(UUID id) {
        customerRepository.findByCustomerIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        return true;
    }
}
