package com.example.accounts.repository;

import com.example.accounts.audit.AuditConfig;
import com.example.accounts.entity.Accounts;
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
class AccountRepositoryTest {

    private static final Long CUSTOMER_ID = 12L;
    private static final Long ACCOUNT_ID = 123456L;
    private static final String ACCOUNT_TYPE = "Savings";
    private static final String ACCOUNT_ADDRESS = "123 Main St";

    @Autowired
    private AuditorAware<String> auditorAware;
    @Autowired
    private AccountRepository accountRepositoryTest;

    @AfterEach
    void tearDown() {
        accountRepositoryTest.deleteAll();
    }

    @Test
    public void testCurrentAuditor() {
        String currentAuditor = auditorAware.getCurrentAuditor().orElse("");
        assertThat(currentAuditor).isEqualTo("ACCOUNTS_MS");
    }

    @Test
    void findByCustomerId() {
        // given
        Accounts found = new Accounts(ACCOUNT_ID, CUSTOMER_ID, ACCOUNT_TYPE, ACCOUNT_ADDRESS);
        accountRepositoryTest.save(found);

        // when
        Optional<Accounts> accountOptional = accountRepositoryTest.findByCustomerId(CUSTOMER_ID);

        // then
        assertThat(accountOptional.isPresent()).isTrue();
    }

    @Test
    void deleteByCustomerId() {
        // given
        Accounts accountToDelete = new Accounts(ACCOUNT_ID, CUSTOMER_ID, ACCOUNT_TYPE, ACCOUNT_ADDRESS);
        accountRepositoryTest.save(accountToDelete);

        // Ensure the account is present before deletion
        Optional<Accounts> accountOptionalBeforeDelete = accountRepositoryTest.findByCustomerId(CUSTOMER_ID);
        assertThat(accountOptionalBeforeDelete.isPresent()).isTrue();

        // when
        accountRepositoryTest.deleteByCustomerId(CUSTOMER_ID);

        // then
        Optional<Accounts> accountOptionalAfterDelete = accountRepositoryTest.findByCustomerId(CUSTOMER_ID);
        assertThat(accountOptionalAfterDelete.isEmpty()).isTrue();
    }
}