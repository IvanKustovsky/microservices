package com.example.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(name = "Customer", description = "Schema to hold Customer and Account information")
public class CustomerDto {


    @Schema(description = "Name of the customer", example = "John Doe")
    @NotEmpty(message = "Name can not be a null or empty")
    @Size(min = 3, max = 30, message = "The length of the name should be between 3 and 30")
    private String name;

    @Schema(description = "Email address of the customer", example = "johndoe@gmail.com")
    @NotEmpty(message = "Email can not be a null or empty")
    @Email(message = "Email address should be a valid value")
    private String email;

    @Schema(description = "Mobile number of the customer", example = "3809865342")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @Schema(description = "Account details of the customer")
    private AccountsDto accountsDto;
}
