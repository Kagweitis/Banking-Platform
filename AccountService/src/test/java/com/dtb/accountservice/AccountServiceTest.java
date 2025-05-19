package com.dtb.accountservice;

import com.dtb.accountservice.DTOs.Requests.CreateAccountRequest;
import com.dtb.accountservice.DTOs.Requests.UpdateCustomerAccountRequest;
import com.dtb.accountservice.DTOs.Responses.GeneralResponse;
import com.dtb.accountservice.DTOs.Responses.GetAccountResponse;
import com.dtb.accountservice.Entity.Account;
import com.dtb.accountservice.Exceptions.AlreadyExistsException;
import com.dtb.accountservice.Exceptions.EntityNotFoundException;
import com.dtb.accountservice.Interfaces.CardServiceClient;
import com.dtb.accountservice.Interfaces.CustomerServiceClient;
import com.dtb.accountservice.Mappers.AccountMapper;
import com.dtb.accountservice.Repository.AccountRepository;
import com.dtb.accountservice.Service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @Mock
    private CardServiceClient cardServiceClient;

    @Test
    void createCustomerAccount_success() {
        CreateAccountRequest request = new CreateAccountRequest(UUID.randomUUID(), "IBAN123", "bicswift");

        when(customerServiceClient.checkCustomerById(request.customerId()))
                .thenReturn(ResponseEntity.ok(true));
        when(accountRepository.existsByIbanAndDeletedFalse(request.iban()))
                .thenReturn(false);
        when(accountMapper.dtoToEntity(request))
                .thenReturn(new Account());

        GeneralResponse response = accountService.createCustomerAccount(request);

        assertEquals("Account created successfully", response.message());
        assertEquals(HttpStatus.CREATED, response.status());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createCustomerAccount_customerNotFound_throwsException() {
        CreateAccountRequest request = new CreateAccountRequest(UUID.randomUUID(), "IBAN123", "bicswift");

        when(customerServiceClient.checkCustomerById(request.customerId()))
                .thenReturn(ResponseEntity.ok(false));

        assertThrows(EntityNotFoundException.class, () ->
                accountService.createCustomerAccount(request)
        );
    }

    @Test
    void createCustomerAccount_ibanExists_throwsException() {
        CreateAccountRequest request = new CreateAccountRequest(UUID.randomUUID(), "IBAN123", "bicswift");

        when(customerServiceClient.checkCustomerById(request.customerId()))
                .thenReturn(ResponseEntity.ok(true));
        when(accountRepository.existsByIbanAndDeletedFalse(request.iban()))
                .thenReturn(true);

        assertThrows(AlreadyExistsException.class, () ->
                accountService.createCustomerAccount(request)
        );
    }

    @Test
    void updateCustomerAccount_shouldUpdateSuccessfully() {
        UUID accountId = UUID.randomUUID();
        String iban = "IBAN123456789";
        String bicSwift = "BIC123";

        UpdateCustomerAccountRequest request = new UpdateCustomerAccountRequest(accountId, iban, bicSwift);

        // No repository interaction is assumed, only the mapper is called
        doNothing().when(accountMapper).updateCustomerAccount(request);

        GeneralResponse response = accountService.updateCustomerAccount(request);

        verify(accountMapper).updateCustomerAccount(request);
        assertEquals("Account updated successfully", response.message());
        assertEquals(HttpStatus.OK, response.status()  );
    }


    @Test
    void deleteCustomerAccount_success() {
        UUID id = UUID.randomUUID();
        Account account = new Account();
        account.setDeleted(false);

        when(accountRepository.findByAccountIdAndDeletedFalse(id))
                .thenReturn(Optional.of(account));

        GeneralResponse response = accountService.deleteCustomerAccount(id);

        assertTrue(account.getDeleted());
        assertNotNull(account.getDeletedAt());
        verify(accountRepository).save(account);
        assertEquals("Account deleted successfully", response.message());
    }

    @Test
    void deleteCustomerAccount_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findByAccountIdAndDeletedFalse(id))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                accountService.deleteCustomerAccount(id)
        );
    }

    @Test
    void getCustomerAccount_shouldReturnAccountResponse_whenAccountExists() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        String iban = "IBAN123456";
        String bicSwift = "BIC123";

        Account account = new Account();
        account.setAccountId(accountId);
        account.setCustomerId(customerId);
        account.setIban(iban);
        account.setBicSwift(bicSwift);
        account.setDeleted(false);

        when(accountRepository.findByAccountIdAndDeletedFalse(accountId)).thenReturn(Optional.of(account));
        GetAccountResponse expectedResponse = new GetAccountResponse(accountId, customerId, iban, bicSwift);
        when(accountMapper.entityToDto(account)).thenReturn(expectedResponse);

        // Act
        GetAccountResponse actualResponse = accountService.getCustomerAccount(accountId);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.accountId(), actualResponse.accountId());
        assertEquals(expectedResponse.customerId(), actualResponse.customerId());
        assertEquals(expectedResponse.iban(), actualResponse.iban());
        assertEquals(expectedResponse.bicSwift(), actualResponse.bicSwift());
    }

    @Test
    void getCustomerAccount_shouldThrowException_whenAccountNotFound() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findByAccountIdAndDeletedFalse(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            accountService.getCustomerAccount(accountId);
        });
    }


    @Test
    void checkAccountExists_returnsCorrectValue() {
        UUID id = UUID.randomUUID();
        when(accountRepository.existsByAccountIdAndDeletedFalse(id))
                .thenReturn(true);

        assertTrue(accountService.checkAccountExists(id));
    }


    @Test
    void getAccountIds_success() {
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        Page<UUID> page = new PageImpl<>(ids);

        when(cardServiceClient.getAccountIds("alias", 0, 10))
                .thenReturn(ResponseEntity.ok(page));

        List<UUID> result = accountService.getAccountIds("alias", 0, 10);
        assertEquals(ids, result);
    }

    @Test
    void getAccountIds_error_returnsNull() {
        when(cardServiceClient.getAccountIds("alias", 0, 10))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        List<UUID> result = accountService.getAccountIds("alias", 0, 10);
        assertNull(result);
    }

    @Test
    void searchAccounts_shouldReturnPageOfResponses_whenFiltersMatch() {
        // Arrange
        String iban = "IBAN123";
        String bicSwift = "BIC123";
        String cardAlias = "card-alias";
        int page = 0;
        int size = 10;
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        List<UUID> accountIds = List.of(accountId);

        // Mocking card service client
        Page<UUID> uuidPage = new PageImpl<>(accountIds);
        ResponseEntity<Page<UUID>> cardResponse = new ResponseEntity<>(uuidPage, HttpStatus.OK);
        when(cardServiceClient.getAccountIds(cardAlias, page, size)).thenReturn(cardResponse);

        Account account = new Account();
        account.setAccountId(accountId);
        account.setCustomerId(customerId);
        account.setIban(iban);
        account.setBicSwift(bicSwift);

        Page<Account> accountPage = new PageImpl<>(List.of(account));
        when(accountRepository.searchAccountsWithFilters(eq(iban), eq(bicSwift), eq(accountIds), any(Pageable.class)))
                .thenReturn(accountPage);

        GetAccountResponse expectedResponse = new GetAccountResponse(accountId, customerId, iban, bicSwift);
        when(accountMapper.entityToDto(account)).thenReturn(expectedResponse);

        // Act
        Page<GetAccountResponse> result = accountService.searchAccounts(iban, bicSwift, cardAlias, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(accountId, result.getContent().get(0).accountId());
        verify(accountRepository).searchAccountsWithFilters(eq(iban), eq(bicSwift), eq(accountIds), any(Pageable.class));
    }

    @Test
    void searchAccounts_shouldReturnResults_whenCardAliasIsNull() {
        String iban = "IBAN123";
        String bicSwift = "BIC123";
        String cardAlias = null;
        int page = 0;
        int size = 5;

        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Account account = new Account();
        account.setAccountId(accountId);
        account.setCustomerId(customerId);
        account.setIban(iban);
        account.setBicSwift(bicSwift);

        Page<Account> accountPage = new PageImpl<>(List.of(account));
        when(accountRepository.searchAccountsWithFilters(eq(iban), eq(bicSwift), isNull(), any(Pageable.class)))
                .thenReturn(accountPage);

        GetAccountResponse expectedResponse = new GetAccountResponse(accountId, customerId, iban, bicSwift);
        when(accountMapper.entityToDto(account)).thenReturn(expectedResponse);

        Page<GetAccountResponse> result = accountService.searchAccounts(iban, bicSwift, cardAlias, page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(accountId, result.getContent().get(0).accountId());
    }



}
