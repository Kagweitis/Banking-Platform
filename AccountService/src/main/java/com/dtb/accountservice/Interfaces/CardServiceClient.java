package com.dtb.accountservice.Interfaces;


import com.dtb.accountservice.Config.FeignConfig;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "CARDSERVICE", configuration = FeignConfig.class)
public interface CardServiceClient {

    @GetMapping("/card/api/v1/get/account/ids")
    ResponseEntity<Page<UUID>> getAccountIds(
            @RequestParam String alias,
            @RequestParam(defaultValue = "0") @NotNull Integer page,
            @RequestParam(defaultValue = "10") @NotNull Integer size
    );
}
