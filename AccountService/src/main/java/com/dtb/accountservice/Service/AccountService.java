package com.dtb.accountservice.Service;

import com.dtb.accountservice.DTOs.Requests.CreateAccountRequest;
import com.dtb.accountservice.DTOs.Requests.UpdateCustomerAccountRequest;
import com.dtb.accountservice.DTOs.Responses.GeneralResponse;
import com.dtb.accountservice.DTOs.Responses.GetAccountResponse;
import com.dtb.accountservice.Entity.Account;
import com.dtb.accountservice.Exceptions.AlreadyExistsException;
import com.dtb.accountservice.Exceptions.EntityNotFoundException;
import com.dtb.accountservice.Interfaces.CustomerServiceClient;
import com.dtb.accountservice.Mappers.AccountMapper;
import com.dtb.accountservice.Repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerServiceClient customerServiceClient;
    private final AccountMapper accountMapper;


    @Transactional
    public GeneralResponse createCustomerAccount(CreateAccountRequest request) {
        validateCustomerExists(request);
        validateIbanUniqueness(request);
        accountRepository.save(accountMapper.dtoToEntity(request));
        return GeneralResponse.builder()
                .message("Account created successfully")
                .status(HttpStatus.CREATED)
                .build();
    }

    private void validateCustomerExists(CreateAccountRequest request){
        ResponseEntity<Boolean> response = customerServiceClient.checkCustomerById(request.customerId());
        if (!response.getStatusCode().is2xxSuccessful() ||
                !Boolean.TRUE.equals(response.getBody())){
            throw new EntityNotFoundException("Customer not found");
        }
    }

    private void validateIbanUniqueness(CreateAccountRequest request){
        if (accountRepository.existsByIbanAndDeletedFalse(request.iban())){
            throw new AlreadyExistsException("An account with that iban already exists");
        }
    }

    public GeneralResponse updateCustomerAccount(UpdateCustomerAccountRequest request) {
        accountMapper.updateCustomerAccount(request);
        return GeneralResponse.builder()
                .message("Account updated successfully")
                .status(HttpStatus.OK)
                .build();
    }

    public GeneralResponse deleteCustomerAccount(UUID id) {

        Account account = accountRepository.findByAccountIdAndDeletedFalse(id)
                .orElseThrow(()-> new EntityNotFoundException("Account not found"));
        account.setDeleted(true);
        account.setDeletedAt(LocalDateTime.now());
        accountRepository.save(account);
        return GeneralResponse.builder()
                .message("Account deleted successfully")
                .status(HttpStatus.OK)
                .build();
    }

    public GetAccountResponse getCustomerAccount(UUID id) {
        Account account = accountRepository.findByAccountIdAndDeletedFalse(id)
                .orElseThrow(()-> new EntityNotFoundException("Account not found"));
        return accountMapper.entityToDto(account);
    }

    public Page<GetAccountResponse> searchAccounts(String iban, String bicSwift, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return accountRepository.findByIbanAndBicSwift(iban, bicSwift, pageable)
                .map(accountMapper::entityToDto);
    }
}
