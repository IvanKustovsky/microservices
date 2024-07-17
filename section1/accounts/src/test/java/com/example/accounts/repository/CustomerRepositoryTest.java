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

    private static final Long CUSTOMER_ID = 123L;
    private static final String CUSTOMER_NAME = "Oleg";
    private static final String CUSTOMER_EMAIL = "olegmongol@gmail.com";
    private static final String VALID_MOBILE_NUMBER = "9831256782";
    private static final String WRONG_MOBILE_NUMBER = "1113334448";

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
        Customer found = new Customer(CUSTOMER_ID, CUSTOMER_NAME, CUSTOMER_EMAIL, VALID_MOBILE_NUMBER);
        customerRepositoryTest.save(found);

        // when
        Optional<Customer> customerOptional = customerRepositoryTest.findByMobileNumber(VALID_MOBILE_NUMBER);

        // then
        assertThat(customerOptional.isPresent()).isTrue();
    }

    @Test
    void findByWrongMobileNumber() {
        // given
        Customer found = new Customer(CUSTOMER_ID, CUSTOMER_NAME, CUSTOMER_EMAIL, VALID_MOBILE_NUMBER);
        customerRepositoryTest.save(found);

        // when
        Optional<Customer> customerOptional = customerRepositoryTest.findByMobileNumber(WRONG_MOBILE_NUMBER);

        // then
        assertThat(customerOptional.isPresent()).isFalse();
    }
}