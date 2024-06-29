package com.example.loans.mapper;

import com.example.loans.dto.LoansDto;
import com.example.loans.entity.Loans;

public class LoansMapper {

    public static LoansDto mapToLoansDto(Loans loans, LoansDto loansDto) {
        loansDto.setLoanNumber(loans.getLoanNumber());
        loansDto.setLoanType(loans.getLoanType());
        loansDto.setTotalLoan(loans.getTotalLoan());
        loansDto.setMobileNumber(loans.getMobileNumber());
        loansDto.setOutstandingAmount(loans.getOutstandingAmount());
        loansDto.setAmountPaid(loans.getAmountPaid());
        return loansDto;
    }

    public static Loans mapToLoans(LoansDto loansDto, Loans loans) {
        loans.setLoanNumber(loansDto.getLoanNumber());
        loans.setLoanType(loansDto.getLoanType());
        loans.setTotalLoan(loansDto.getTotalLoan());
        loans.setMobileNumber(loansDto.getMobileNumber());
        loans.setOutstandingAmount(loansDto.getOutstandingAmount());
        loans.setAmountPaid(loansDto.getAmountPaid());
        return loans;
    }
}
