package com.dtb.accountservice.Mappers;

import com.dtb.accountservice.DTOs.Requests.CreateAccountRequest;
import com.dtb.accountservice.DTOs.Requests.UpdateCustomerAccountRequest;
import com.dtb.accountservice.Entity.Account;
import com.dtb.accountservice.Exceptions.EntityNotFoundException;
import com.dtb.accountservice.Repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountMapper {

    private final AccountRepository accountRepository;

    public Account dtoToEntity(CreateAccountRequest request){
        return Account.builder()
                .iban(request.iban())
                .bicSwift(request.bicSwift())
                .customerId(request.customerId())
                .build();
    }

    public void updateCustomerAccount(UpdateCustomerAccountRequest request){
        Account account = accountRepository.findByAccountIdAndDeletedFalse(request.accountId())
                .orElseThrow(()-> new EntityNotFoundException("Account not found"));
        account.setIban(request.iban() != null ? request.iban() : account.getIban());
        account.setBicSwift(request.bicSwift() != null ? request.bicSwift() : account.getBicSwift());
        accountRepository.save(account);
    }
}
