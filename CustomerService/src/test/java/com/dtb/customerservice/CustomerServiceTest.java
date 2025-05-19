package com.dtb.customerservice;


import com.dtb.customerservice.DTOs.Requests.CreateCustomerRequest;
import com.dtb.customerservice.DTOs.Requests.UpdateCustomerRequest;
import com.dtb.customerservice.DTOs.Responses.GeneralResponse;
import com.dtb.customerservice.DTOs.Responses.GetCustomerResponse;
import com.dtb.customerservice.Entities.Customer;
import com.dtb.customerservice.Exceptions.EntityNotFoundException;
import com.dtb.customerservice.Mappers.CustomerMapper;
import com.dtb.customerservice.Repository.CustomerRepository;
import com.dtb.customerservice.Service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateCustomer() {
        CreateCustomerRequest request = new CreateCustomerRequest("John", "Doe", "A.");
        Customer customer = new Customer(); // Assume a constructor or builder

        when(customerMapper.dtoToEntity(request)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);

        GeneralResponse response = customerService.createCustomer(request);

        assertEquals("Customer created successfully", response.message());
        assertEquals(HttpStatus.CREATED, response.status());
        verify(customerRepository).save(customer);
    }

    @Test
    void shouldReturnCustomerById() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer();
        GetCustomerResponse expectedResponse = new GetCustomerResponse("John", "Doe", "A.");

        when(customerRepository.findByCustomerIdAndDeletedFalse(id)).thenReturn(Optional.of(customer));
        when(customerMapper.getCustomerResponse(customer)).thenReturn(expectedResponse);

        GetCustomerResponse actual = customerService.getUserById(id);

        assertEquals(expectedResponse, actual);
    }

    @Test
    void shouldThrowWhenCustomerNotFoundById() {
        UUID id = UUID.randomUUID();
        when(customerRepository.findByCustomerIdAndDeletedFalse(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.getUserById(id));
    }

    @Test
    void shouldSearchCustomersByNameAndDateRange() {
        Pageable pageable = PageRequest.of(0, 10);
        Customer customer = new Customer();
        GetCustomerResponse response = new GetCustomerResponse("John", "Doe", "A.");

        when(customerRepository.fuzzySearchByNameAndDateRange(anyString(), any(), any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(customer)));
        when(customerMapper.getCustomerResponse(customer)).thenReturn(response);

        Page<GetCustomerResponse> page = customerService.getUsersByParams("John", null, null, 0, 10);

        assertEquals(1, page.getTotalElements());
        assertEquals("John", page.getContent().getFirst().firstName());
    }

    @Test
    void shouldUpdateCustomer() {
        UpdateCustomerRequest request = new UpdateCustomerRequest(UUID.randomUUID(), "John", "Doe", "B.");

        doNothing().when(customerMapper).updateCustomer(request);

        GeneralResponse response = customerService.updateCustomer(request);

        assertEquals("Customer updated successfully", response.message());
        assertEquals(HttpStatus.OK, response.status());
        verify(customerMapper).updateCustomer(request);
    }

    @Test
    void shouldDeleteCustomer() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setDeleted(false);

        when(customerRepository.findByCustomerIdAndDeletedFalse(id)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        GeneralResponse response = customerService.deleteCustomer(id);

        assertTrue(customer.getDeleted());
        assertNotNull(customer.getDeletedAt());
        assertEquals("Customer deleted successfully", response.message());
    }

    @Test
    void shouldThrowWhenDeletingNonexistentCustomer() {
        UUID id = UUID.randomUUID();

        when(customerRepository.findByCustomerIdAndDeletedFalse(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.deleteCustomer(id));
    }

    @Test
    void shouldReturnTrueIfCustomerExists() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer();

        when(customerRepository.findByCustomerIdAndDeletedFalse(id)).thenReturn(Optional.of(customer));

        assertTrue(customerService.checkCustomerExists(id));
    }

    @Test
    void shouldThrowIfCustomerDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(customerRepository.findByCustomerIdAndDeletedFalse(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.checkCustomerExists(id));
    }


}

