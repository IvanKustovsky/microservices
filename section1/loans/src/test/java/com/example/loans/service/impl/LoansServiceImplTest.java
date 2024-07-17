package com.example.loans.service.impl;

import com.example.loans.constants.LoansConstants;
import com.example.loans.dto.LoansDto;
import com.example.loans.entity.Loans;
import com.example.loans.exception.LoanAlreadyExistsException;
import com.example.loans.exception.ResourceNotFoundException;
import com.example.loans.repository.LoansRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Loans Service Test Class")
class LoansServiceImplTest {

    private static final Long LOAN_ID = 123L;
    private static final String MOBILE_NUMBER = "3809745312";
    private static final String LOAN_NUMBER = "LN123456";
    private static final String LOAN_TYPE = LoansConstants.HOME_LOAN;
    private static final int TOTAL_LOAN = LoansConstants.NEW_LOAN_LIMIT;
    private static final int AMOUNT_PAID = 0;
    private static final int OUTSTANDING_AMOUNT = LoansConstants.NEW_LOAN_LIMIT;

    @Mock
    private LoansRepository loansRepositoryMock;

    @InjectMocks
    private LoansServiceImpl loansServiceImplTest;


    @Test
    @DisplayName("Should throw LoanAlreadyExistsException when trying to create loan for the given mobile number that already exists")
    void createLoanWhenExists() {
        // given
        Loans newLoan = new Loans();
        newLoan.setMobileNumber(MOBILE_NUMBER);

        when(loansRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.of(newLoan));

        // when, then
        assertThatThrownBy(() -> loansServiceImplTest.createLoan(MOBILE_NUMBER))
                .isInstanceOf(LoanAlreadyExistsException.class)
                .hasMessage("Loan already registered with given mobileNumber " + MOBILE_NUMBER);

        verify(loansRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
        verify(loansRepositoryMock, never()).save(any(Loans.class));
    }

    @Test
    @DisplayName("Should create loan when no card exists for the given mobile number")
    void createLoanWhenNotExists() {
        // given
        Loans newLoan = new Loans();
        newLoan.setMobileNumber(MOBILE_NUMBER);

        when(loansRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.empty());
        when(loansRepositoryMock.save(any(Loans.class))).thenReturn(newLoan);

        // when
        loansServiceImplTest.createLoan(MOBILE_NUMBER);

        // then
        verify(loansRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
        verify(loansRepositoryMock, times(1)).save(any(Loans.class));

        ArgumentCaptor<Loans> loanCaptor = ArgumentCaptor.forClass(Loans.class);
        verify(loansRepositoryMock).save(loanCaptor.capture());
        Loans savedLoan = loanCaptor.getValue();

        assertEquals(MOBILE_NUMBER, savedLoan.getMobileNumber());
        assertEquals(LOAN_TYPE, savedLoan.getLoanType());
        assertEquals(TOTAL_LOAN, savedLoan.getTotalLoan());
        assertEquals(AMOUNT_PAID, savedLoan.getAmountPaid());
        assertEquals(OUTSTANDING_AMOUNT, savedLoan.getOutstandingAmount());
    }

    @Test
    @DisplayName("Should fetch loan when it exists for the given mobile number")
    void fetchLoanWhenExists() {
        // given
        Loans newLoan = new Loans();
        newLoan.setMobileNumber(MOBILE_NUMBER);

        when(loansRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.of(newLoan));

        // when
        LoansDto result = loansServiceImplTest.fetchLoan(MOBILE_NUMBER);

        // then
        assertNotNull(result);
        assertEquals(newLoan.getMobileNumber(), result.getMobileNumber());

        verify(loansRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to fetch loan for the given mobile number that does not exist")
    void fetchLoanWhenNotExists() {
        // given
        when(loansRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> loansServiceImplTest.fetchLoan(MOBILE_NUMBER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("%s not found with the given input data %s: '%s'",
                        "Loan", "mobileNumber", MOBILE_NUMBER);

        verify(loansRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
    }

    @Test
    @DisplayName("Should update loan when it exists for the given card number")
    void updateLoanWhenExists() {
        // given
        LoansDto loansDto = new LoansDto();
        loansDto.setLoanNumber(LOAN_NUMBER);
        Loans newCard = new Loans();

        when(loansRepositoryMock.findByLoanNumber(LOAN_NUMBER)).thenReturn(Optional.of(newCard));
        when(loansRepositoryMock.save(any(Loans.class))).thenReturn(newCard);

        // when
        boolean isUpdated = loansServiceImplTest.updateLoan(loansDto);

        // then
        assertTrue(isUpdated);

        verify(loansRepositoryMock, times(1)).findByLoanNumber(LOAN_NUMBER);
        verify(loansRepositoryMock, times(1)).save(any(Loans.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to update loan for the given card number that does not exist")
    void updateLoanWhenNotExists() {
        // given
        LoansDto loansDto = new LoansDto();
        loansDto.setLoanNumber(LOAN_NUMBER);

        when(loansRepositoryMock.findByLoanNumber(LOAN_NUMBER)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> loansServiceImplTest.updateLoan(loansDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("%s not found with the given input data %s: '%s'",
                        "Loan", "loanNumber", LOAN_NUMBER);

        verify(loansRepositoryMock, times(1)).findByLoanNumber(LOAN_NUMBER);
        verify(loansRepositoryMock, never()).save(any(Loans.class));
    }


    @Test
    @DisplayName("Should delete loan when it exists for the given mobile number")
    void deleteLoanWhenExists() {
        // given
        Loans foundLoan = new Loans();
        foundLoan.setLoanId(LOAN_ID);

        when(loansRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.of(foundLoan));

        // when
        boolean isDeleted = loansServiceImplTest.deleteLoan(MOBILE_NUMBER);

        // then
        assertTrue(isDeleted);

        verify(loansRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
        verify(loansRepositoryMock, times(1)).deleteById(LOAN_ID);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to delete loan for the given mobile number that does not exist")
    void deleteLoanWhenNotExists() {
        // given
        Loans foundLoan = new Loans();
        foundLoan.setLoanId(LOAN_ID);

        when(loansRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> loansServiceImplTest.fetchLoan(MOBILE_NUMBER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("%s not found with the given input data %s: '%s'",
                        "Loan", "mobileNumber", MOBILE_NUMBER);

        verify(loansRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
        verify(loansRepositoryMock, never()).deleteById(any());
    }
}