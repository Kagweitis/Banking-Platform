package com.dtb.accountservice.Service;

import com.dtb.accountservice.DTOs.Requests.CreateAccountRequest;
import com.dtb.accountservice.DTOs.Responses.GeneralResponse;
import com.dtb.accountservice.Exceptions.AlreadyExistsException;
import com.dtb.accountservice.Exceptions.EntityNotFoundException;
import com.dtb.accountservice.Interfaces.CustomerServiceClient;
import com.dtb.accountservice.Mappers.AccountMapper;
import com.dtb.accountservice.Repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerServiceClient customerServiceClient;
    private final AccountMapper accountMapper;


    public GeneralResponse createCustomerAccount(CreateAccountRequest request) {

        ResponseEntity<Boolean> response = customerServiceClient.checkCustomerById(request.customerId());

        if (!response.getStatusCode().is2xxSuccessful() ||
                !Boolean.TRUE.equals(response.getBody())){
            throw new EntityNotFoundException("Customer not found");
        }
        if (accountRepository.existsByIbanAndDeletedFalse(request.iban())){
            throw new AlreadyExistsException("An account with that iban already exists");
        }

        accountRepository.save(accountMapper.dtoToEntity(request));
        return GeneralResponse.builder()
                .message("Account created successfully")
                .status(HttpStatus.CREATED)
                .build();
    }
}
