package com.example.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Schema(name = "Loans", description = "Schema to hold loans information")
public class LoansDto {

    @Schema(description = "Mobile Number of Customer", example = "3809756429")
    @NotEmpty(message = "MobileNumber can not be a null or empty")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "MobileNumber must be 10 digits")
    private String mobileNumber;

    @Schema(description = "Loan Number of Customer", example = "543198562113")
    @NotEmpty(message = "LoanNumber can not be a null or empty")
    @Pattern(regexp = "(^$|[0-9]{12})", message = "LoanNumber must be 12 digits")
    private String loanNumber;

    @Schema(description = "Type of the loan",  example = "Home Loan")
    @NotEmpty(message = "LoanType can not be a null or empty")
    private String loanType;

    @Schema(description = "Total loan amount",  example = "95000")
    @Positive(message = "Total loan amount should be greater than zero")
    private int totalLoan;

    @Schema(description = "Total loan amount paid",  example = "55000")
    @PositiveOrZero(message = "Total loan amount paid should be equal or greater than zero")
    private int amountPaid;

    @Schema(description = "Total outstanding amount against a loan", example = "40000")
    @PositiveOrZero(message = "Total outstanding amount should be equal or greater than zero")
    private int outstandingAmount;
}
