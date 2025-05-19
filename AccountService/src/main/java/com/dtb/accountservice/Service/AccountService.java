package com.dtb.accountservice.Service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerServiceClient customerServiceClient;
    private final AccountMapper accountMapper;
    private final CardServiceClient cardServiceClient;


    /**
     * Creates a new customer account after validating customer existence and IBAN uniqueness.
     *
     * @param request the account creation request containing necessary customer and IBAN details
     * @return a {@link GeneralResponse} indicating successful creation
     * @throws EntityNotFoundException if the specified customer does not exist
     * @throws AlreadyExistsException if an account with the given IBAN already exists
     */
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

    /**
     * Validates that the customer specified in the account request exists via remote call.
     *
     * @param request the account creation request
     * @throws EntityNotFoundException if the customer is not found
     */
    private void validateCustomerExists(CreateAccountRequest request){
        ResponseEntity<Boolean> response = customerServiceClient.checkCustomerById(request.customerId());
        if (!response.getStatusCode().is2xxSuccessful() ||
                !Boolean.TRUE.equals(response.getBody())){
            throw new EntityNotFoundException("Customer not found");
        }
    }

    /**
     * Validates that the IBAN in the request is not already used by an active account.
     *
     * @param request the account creation request
     * @throws AlreadyExistsException if the IBAN is already in use
     */
    private void validateIbanUniqueness(CreateAccountRequest request){
        if (accountRepository.existsByIbanAndDeletedFalse(request.iban())){
            throw new AlreadyExistsException("An account with that iban already exists");
        }
    }

    /**
     * Updates customer account details.
     *
     * @param request the request containing fields to update
     * @return a {@link GeneralResponse} indicating successful update
     */
    public GeneralResponse updateCustomerAccount(UpdateCustomerAccountRequest request) {
        accountMapper.updateCustomerAccount(request);
        return GeneralResponse.builder()
                .message("Account updated successfully")
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Soft deletes a customer account by setting its deleted flag and timestamp.
     *
     * @param id the unique identifier of the account
     * @return a {@link GeneralResponse} indicating successful deletion
     * @throws EntityNotFoundException if the account is not found or already deleted
     */
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

    /**
     * Retrieves a single customer account by its ID if it exists and is not deleted.
     *
     * @param id the UUID of the account
     * @return the account information as a {@link GetAccountResponse}
     * @throws EntityNotFoundException if the account is not found or is deleted
     */
    public GetAccountResponse getCustomerAccount(UUID id) {
        Account account = accountRepository.findByAccountIdAndDeletedFalse(id)
                .orElseThrow(()-> new EntityNotFoundException("Account not found"));
        return accountMapper.entityToDto(account);
    }

    /**
     * Searches for accounts using optional filters (IBAN, BIC Swift, Card Alias).
     * If a card alias is provided, it queries the card service for matching account IDs.
     *
     * @param iban the IBAN to filter accounts
     * @param bicSwift the BIC/SWIFT code to filter accounts
     * @param cardAlias the card alias used to retrieve related account IDs from card service
     * @param page the page index for pagination
     * @param size the page size for pagination
     * @return a paginated list of matching account DTOs
     */
    public Page<GetAccountResponse> searchAccounts(String iban, String bicSwift, String cardAlias, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        List<UUID> accountIds = null;
        if (cardAlias != null && !cardAlias.isEmpty()){
           accountIds = getAccountIds(cardAlias, page, size);
        }
        return accountRepository.searchAccountsWithFilters(iban, bicSwift, accountIds, pageable)
                .map(accountMapper::entityToDto);

    }

    /**
     * Checks whether an account exists and is not marked as deleted.
     *
     * @param id the UUID of the account
     * @return {@code true} if the account exists and is not deleted; otherwise {@code false}
     */
    public Boolean checkAccountExists(UUID id) {
        return accountRepository.existsByAccountIdAndDeletedFalse(id);
    }

    /**
     * Retrieves account IDs from the card service for cards that match the given alias.
     *
     * @param alias the card alias to filter by
     * @param page the page number for pagination
     * @param size the number of records per page
     * @return a list of account UUIDs associated with the given card alias, or {@code null} on failure
     */
    public List<UUID> getAccountIds(String alias, Integer page, Integer size){
        ResponseEntity<Page<UUID>> response = cardServiceClient.getAccountIds(alias, page, size);
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody().stream().toList();
        }
        return null;
    }
}
