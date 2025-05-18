package com.dtb.cardservice.Interfaces;

import com.dtb.cardservice.Config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "ACCOUNTSERVICE", configuration = FeignConfig.class)
public interface AccountServiceClient {

    @GetMapping("/account/api/v1/check/account/{id}")
    ResponseEntity<Boolean> checkAccountById(@PathVariable UUID id);
}
