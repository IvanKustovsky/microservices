package com.example.message.functions;

import com.example.message.dto.AccountsMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Slf4j
@Configuration
public class MessageFunctions {

    @Bean
    public Function<AccountsMessageDto, AccountsMessageDto> email() {
        return accountsMessageDto -> {
            log.info("Sending email with the details: {}", accountsMessageDto.toString());
            return accountsMessageDto;
        };
    }

    @Bean
    public Function<AccountsMessageDto, Long> sms() {
        return accountsMessageDto -> {
            log.info("Sending sms with the details: {}", accountsMessageDto.toString());
            return accountsMessageDto.accountNumber();
        };
    }

}
