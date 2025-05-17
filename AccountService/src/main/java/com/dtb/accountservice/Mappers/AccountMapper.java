package com.dtb.accountservice.Mappers;

import com.dtb.accountservice.DTOs.Requests.CreateAccountRequest;
import com.dtb.accountservice.Entity.Account;
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
}
