package com.dtb.accountservice.Interfaces;

import com.dtb.accountservice.Config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "CUSTOMERSERVICE", configuration = FeignConfig.class)
public interface CustomerServiceClient {

    @GetMapping("customers/api/v1/check/customer/{id}")
    ResponseEntity<Boolean> checkCustomerById(@PathVariable UUID id);
}
