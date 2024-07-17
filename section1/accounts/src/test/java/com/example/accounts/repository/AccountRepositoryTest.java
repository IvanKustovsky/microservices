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
        Long customerId = 12L;

        Accounts found = new Accounts(123456L, customerId, "Savings",
                "123 Main St");
        accountRepositoryTest.save(found);

        // when
        Optional<Accounts> accountOptional = accountRepositoryTest.findByCustomerId(customerId);

        // then
        assertThat(accountOptional.isPresent()).isTrue();
    }

    @Test
    void deleteByCustomerId() {
        // given
        Long customerId = 12L;

        Accounts accountToDelete = new Accounts(123456L, customerId,
                "Savings", "123 Main St");
        accountRepositoryTest.save(accountToDelete);

        // Ensure the account is present before deletion
        Optional<Accounts> accountOptionalBeforeDelete = accountRepositoryTest.findByCustomerId(customerId);
        assertThat(accountOptionalBeforeDelete.isPresent()).isTrue();

        // when
        accountRepositoryTest.deleteByCustomerId(customerId);

        // then
        Optional<Accounts> accountOptionalAfterDelete = accountRepositoryTest.findByCustomerId(customerId);
        assertThat(accountOptionalAfterDelete.isEmpty()).isTrue();
    }
}