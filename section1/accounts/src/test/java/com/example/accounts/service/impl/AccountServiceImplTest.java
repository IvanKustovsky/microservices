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
        customerDto.setName("John Doe");
        customerDto.setMobileNumber("1234567890");

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
        customerDto.setName("John Doe");
        customerDto.setMobileNumber("1234567890");

        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());

        when(customerRepositoryMock.findByMobileNumber(customer.getMobileNumber())).thenReturn(Optional.of(customer));

        // when, then
        assertThrows(CustomerAlreadyExistsException.class, () -> accountServiceTest.createAccount(customerDto));

        verify(customerRepositoryMock, times(1)).findByMobileNumber(customer.getMobileNumber());
        verify(customerRepositoryMock, times(0)).save(any(Customer.class));
        verify(accountRepositoryMock, times(0)).save(any(Accounts.class));
    }

    @Test
    void fetchAccount() {
        // given
        String mobileNumber = "1234567890";
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setName("John Doe");
        customer.setMobileNumber(mobileNumber);

        Accounts accounts = new Accounts();
        accounts.setAccountNumber(123456L);
        accounts.setCustomerId(customer.getCustomerId());

        when(customerRepositoryMock.findByMobileNumber(mobileNumber)).thenReturn(Optional.of(customer));
        when(accountRepositoryMock.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.of(accounts));

        // when
        CustomerDto result = accountServiceTest.fetchAccount(mobileNumber);

        // then
        assertNotNull(result);
        assertEquals(customer.getName(), result.getName());
        assertNotNull(result.getAccountsDto());
        assertEquals(accounts.getAccountNumber(), result.getAccountsDto().getAccountNumber());

        verify(customerRepositoryMock, times(1)).findByMobileNumber(mobileNumber);
        verify(accountRepositoryMock, times(1)).findByCustomerId(customer.getCustomerId());
    }

    @Test
    void fetchAccountThrowsResourceNotFoundExceptionForCustomer() {
        // given
        String mobileNumber = "1234567890";

        when(customerRepositoryMock.findByMobileNumber(mobileNumber)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> accountServiceTest.fetchAccount(mobileNumber))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("%s not found with the given input data %s: '%s'",
                        "Customer", "mobileNumber", mobileNumber);

        verify(customerRepositoryMock, times(1)).findByMobileNumber(mobileNumber);
        verify(accountRepositoryMock, never()).findByCustomerId(any());
    }

    @Test
    void fetchAccountThrowsResourceNotFoundExceptionForAccounts() {
        // given
        Long customerId = 123L;
        Customer customer = new Customer();
        customer.setCustomerId(customerId);

        when(customerRepositoryMock.findByMobileNumber(any())).thenReturn(Optional.of(customer));

        // when, then
        assertThatThrownBy(() -> accountServiceTest.fetchAccount(any()))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("%s not found with the given input data %s: '%s'",
                                "Account", "customerId", customer.getCustomerId().toString());

        verify(customerRepositoryMock, times(1)).findByMobileNumber(any());
        verify(accountRepositoryMock, times(1)).findByCustomerId(customerId);
    }



    @Test
    void updateAccount() {
        // given
        AccountsDto accountsDto = new AccountsDto();
        accountsDto.setAccountNumber(123456L);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setMobileNumber("1234567890");
        customerDto.setAccountsDto(accountsDto);

        Accounts accounts = new Accounts();
        accounts.setAccountNumber(123456L);
        accounts.setCustomerId(1L);

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
        accountsDto.setAccountNumber(123456L);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setMobileNumber("1234567890");
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
        accountsDto.setAccountNumber(123456L);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setMobileNumber("1234567890");
        customerDto.setAccountsDto(accountsDto);

        Accounts accounts = new Accounts();
        accounts.setAccountNumber(accountsDto.getAccountNumber());
        accounts.setCustomerId(1L);

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
        String mobileNumber = "1234567890";
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setMobileNumber(mobileNumber);

        when(customerRepositoryMock.findByMobileNumber(mobileNumber)).thenReturn(Optional.of(customer));

        // when
        boolean result = accountServiceTest.deleteAccount(mobileNumber);

        // then
        assertTrue(result);

        verify(customerRepositoryMock, times(1)).findByMobileNumber(mobileNumber);
        verify(accountRepositoryMock, times(1)).deleteByCustomerId(customer.getCustomerId());
        verify(customerRepositoryMock, times(1)).deleteById(customer.getCustomerId());
    }

    @Test
    void deleteAccountThrowsResourceNotFoundException() {
        // given
        String mobileNumber = "1234567890";

        when(customerRepositoryMock.findByMobileNumber(mobileNumber)).thenReturn(Optional.empty());

        // when, then
        assertThrows(ResourceNotFoundException.class, () -> accountServiceTest.deleteAccount(mobileNumber));

        verify(customerRepositoryMock, times(1)).findByMobileNumber(mobileNumber);
        verify(accountRepositoryMock, never()).deleteByCustomerId(any());
        verify(customerRepositoryMock, never()).deleteById(any());
    }
}