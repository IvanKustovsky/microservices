package com.example.accounts.service.impl;

import com.example.accounts.dto.AccountsDto;
import com.example.accounts.dto.CustomerDto;
import com.example.accounts.entity.Accounts;
import com.example.accounts.entity.Customer;
import com.example.accounts.exception.CustomerAlreadyExistsException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.mapper.CustomerMapper;
import com.example.accounts.repository.AccountRepository;
import com.example.accounts.repository.CustomerRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    private static final Long CUSTOMER_ID = 1L;
    private static final String CUSTOMER_NAME = "John Doe";
    private static final String MOBILE_NUMBER = "1234567890";
    private static final Long ACCOUNT_NUMBER = 123456L;

    @Mock
    private AccountRepository accountRepositoryMock;

    @Mock
    private CustomerRepository customerRepositoryMock;

    @InjectMocks
    private AccountServiceImpl accountServiceTest;

    @Test
    void createAccount() {
        // given
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName(CUSTOMER_NAME);
        customerDto.setMobileNumber(MOBILE_NUMBER);

        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());

        when(customerRepositoryMock.findByMobileNumber(customer.getMobileNumber())).thenReturn(Optional.empty());
        when(customerRepositoryMock.save(any(Customer.class))).thenReturn(customer);
        when(accountRepositoryMock.save(any(Accounts.class))).thenReturn(new Accounts());

        // when
        assertDoesNotThrow(() -> accountServiceTest.createAccount(customerDto));

        // then
        verify(customerRepositoryMock, times(1)).findByMobileNumber(customer.getMobileNumber());
        verify(customerRepositoryMock, times(1)).save(any(Customer.class));
        verify(accountRepositoryMock, times(1)).save(any(Accounts.class));
    }

    @Test
    void createAccountThrowsExceptionWhenCustomerExists() {
        // given
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName(CUSTOMER_NAME);
        customerDto.setMobileNumber(MOBILE_NUMBER);

        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());

        when(customerRepositoryMock.findByMobileNumber(customer.getMobileNumber())).thenReturn(Optional.of(customer));

        // when, then
        assertThrows(CustomerAlreadyExistsException.class, () -> accountServiceTest.createAccount(customerDto));

        verify(customerRepositoryMock, times(1)).findByMobileNumber(customer.getMobileNumber());
        verify(customerRepositoryMock, never()).save(any(Customer.class));
        verify(accountRepositoryMock, never()).save(any(Accounts.class));
    }

    @Test
    void fetchAccount() {
        // given
        Customer customer = new Customer();
        customer.setCustomerId(CUSTOMER_ID);
        customer.setName(CUSTOMER_NAME);
        customer.setMobileNumber(MOBILE_NUMBER);

        Accounts accounts = new Accounts();
        accounts.setAccountNumber(ACCOUNT_NUMBER);
        accounts.setCustomerId(customer.getCustomerId());

        when(customerRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.of(customer));
        when(accountRepositoryMock.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.of(accounts));

        // when
        CustomerDto result = accountServiceTest.fetchAccount(MOBILE_NUMBER);

        // then
        assertNotNull(result);
        assertEquals(customer.getName(), result.getName());
        assertNotNull(result.getAccountsDto());
        assertEquals(accounts.getAccountNumber(), result.getAccountsDto().getAccountNumber());

        verify(customerRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
        verify(accountRepositoryMock, times(1)).findByCustomerId(customer.getCustomerId());
    }

    @Test
    void fetchAccountThrowsResourceNotFoundExceptionForCustomer() {
        // given
        when(customerRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> accountServiceTest.fetchAccount(MOBILE_NUMBER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("%s not found with the given input data %s: '%s'",
                        "Customer", "mobileNumber", MOBILE_NUMBER);

        verify(customerRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
        verify(accountRepositoryMock, never()).findByCustomerId(any());
    }

    @Test
    void fetchAccountThrowsResourceNotFoundExceptionForAccounts() {
        // given
        Customer customer = new Customer();
        customer.setCustomerId(CUSTOMER_ID);

        when(customerRepositoryMock.findByMobileNumber(any())).thenReturn(Optional.of(customer));

        // when, then
        assertThatThrownBy(() -> accountServiceTest.fetchAccount(any()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("%s not found with the given input data %s: '%s'",
                        "Account", "customerId", customer.getCustomerId().toString());

        verify(customerRepositoryMock, times(1)).findByMobileNumber(any());
        verify(accountRepositoryMock, times(1)).findByCustomerId(customer.getCustomerId());
    }

    @Test
    void updateAccount() {
        // given
        AccountsDto accountsDto = new AccountsDto();
        accountsDto.setAccountNumber(ACCOUNT_NUMBER);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setMobileNumber(MOBILE_NUMBER);
        customerDto.setAccountsDto(accountsDto);

        Accounts accounts = new Accounts();
        accounts.setAccountNumber(ACCOUNT_NUMBER);
        accounts.setCustomerId(CUSTOMER_ID);

        when(accountRepositoryMock.findById(accountsDto.getAccountNumber())).thenReturn(Optional.of(accounts));
        when(accountRepositoryMock.save(any(Accounts.class))).thenReturn(accounts);

        Customer customer = new Customer();
        customer.setCustomerId(accounts.getCustomerId());

        when(customerRepositoryMock.findById(accounts.getCustomerId())).thenReturn(Optional.of(customer));

        // when
        boolean result = accountServiceTest.updateAccount(customerDto);

        // then
        assertTrue(result);

        verify(accountRepositoryMock, times(1)).findById(accountsDto.getAccountNumber());
        verify(accountRepositoryMock, times(1)).save(any(Accounts.class));
        verify(customerRepositoryMock, times(1)).findById(accounts.getCustomerId());
    }

    @Test
    void updateAccountThrowsResourceNotFoundExceptionForAccount() {
        // given
        AccountsDto accountsDto = new AccountsDto();
        accountsDto.setAccountNumber(ACCOUNT_NUMBER);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setMobileNumber(MOBILE_NUMBER);
        customerDto.setAccountsDto(accountsDto);

        when(accountRepositoryMock.findById(accountsDto.getAccountNumber())).thenReturn(Optional.empty());

        // when, then
        assertThrows(ResourceNotFoundException.class, () -> accountServiceTest.updateAccount(customerDto));

        verify(accountRepositoryMock, times(1)).findById(accountsDto.getAccountNumber());
        verify(accountRepositoryMock, never()).save(any(Accounts.class));
    }

    @Test
    void updateAccountThrowsResourceNotFoundExceptionForCustomer() {
        // given
        AccountsDto accountsDto = new AccountsDto();
        accountsDto.setAccountNumber(ACCOUNT_NUMBER);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setMobileNumber(MOBILE_NUMBER);
        customerDto.setAccountsDto(accountsDto);

        Accounts accounts = new Accounts();
        accounts.setAccountNumber(accountsDto.getAccountNumber());
        accounts.setCustomerId(CUSTOMER_ID);

        given(accountRepositoryMock.save(any(Accounts.class))).willReturn(accounts);

        when(accountRepositoryMock.findById(accountsDto.getAccountNumber())).thenReturn(Optional.of(accounts));
        when(customerRepositoryMock.findById(accounts.getCustomerId())).thenReturn(Optional.empty());

        // when, then
        assertThrows(ResourceNotFoundException.class, () -> accountServiceTest.updateAccount(customerDto));

        verify(accountRepositoryMock, times(1)).findById(accountsDto.getAccountNumber());
        verify(customerRepositoryMock, times(1)).findById(accounts.getCustomerId());
        verify(customerRepositoryMock, never()).save(any(Customer.class));
    }

    @Test
    void updateAccountReturnsFalseWhenAccountsDTOIsNull() {
        // given
        CustomerDto customerDto = new CustomerDto();

        // when, then
        assertFalse(accountServiceTest.updateAccount(customerDto));

        verify(customerRepositoryMock, never()).save(any(Customer.class));
        verify(accountRepositoryMock, never()).save(any(Accounts.class));
    }

    @Test
    void deleteAccount() {
        // given
        Customer customer = new Customer();
        customer.setCustomerId(CUSTOMER_ID);
        customer.setMobileNumber(MOBILE_NUMBER);

        when(customerRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.of(customer));

        // when
        boolean result = accountServiceTest.deleteAccount(MOBILE_NUMBER);

        // then
        assertTrue(result);

        verify(customerRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
        verify(accountRepositoryMock, times(1)).deleteByCustomerId(customer.getCustomerId());
        verify(customerRepositoryMock, times(1)).deleteById(customer.getCustomerId());
    }

    @Test
    void deleteAccountThrowsResourceNotFoundException() {
        // given
        when(customerRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.empty());

        // when, then
        assertThrows(ResourceNotFoundException.class, () -> accountServiceTest.deleteAccount(MOBILE_NUMBER));

        verify(customerRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
        verify(accountRepositoryMock, never()).deleteByCustomerId(any());
        verify(customerRepositoryMock, never()).deleteById(any());
    }
}