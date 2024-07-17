package com.example.accounts.repository;

import com.example.accounts.audit.AuditConfig;
import com.example.accounts.entity.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.yml")
@Import(AuditConfig.class)
class CustomerRepositoryTest {

    @Autowired
    private AuditorAware<String> auditorAware;
    @Autowired
    private CustomerRepository customerRepositoryTest;

    @AfterEach
    void tearDown() {
        customerRepositoryTest.deleteAll();
    }

    @Test
    public void testCurrentAuditor() {
        String currentAuditor = auditorAware.getCurrentAuditor().orElse("");
        assertThat(currentAuditor).isEqualTo("ACCOUNTS_MS");
    }
    @Test
    void findByMobileNumber() {
        // given
        String mobileNumber = "9831256782";

        Customer found = new Customer(123L, "Oleg", "olegmongol@gmail.com", mobileNumber);
        customerRepositoryTest.save(found);

        // when
        Optional<Customer> customerOptional = customerRepositoryTest.findByMobileNumber(mobileNumber);

        // then
        assertThat(customerOptional.isPresent()).isTrue();
    }

    @Test
    void findByWrongMobileNumber() {
        // given
        String wrongMobileNumber = "1113334448";

        Customer found = new Customer(123L, "Oleg", "olegmongol@gmail.com", "9831256782");
        customerRepositoryTest.save(found);

        // when
        Optional<Customer> customerOptional = customerRepositoryTest.findByMobileNumber(wrongMobileNumber);

        // then
        assertThat(customerOptional.isPresent()).isFalse();
    }


}