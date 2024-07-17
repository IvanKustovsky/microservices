package com.example.loans.repository;

import com.example.loans.audit.AuditConfig;
import com.example.loans.entity.Loans;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.yml")
@Import(AuditConfig.class)
class LoansRepositoryTest {

    private static final Long LOAN_ID = 123L;
    private static final String MOBILE_NUMBER = "3809745312";
    private static final String LOAN_NUMBER = "LN123456";
    private static final String LOAN_TYPE = "Personal Loan";
    private static final int TOTAL_LOAN = 50000;
    private static final int AMOUNT_PAID = 10000;
    private static final int OUTSTANDING_AMOUNT = 40000;

    @Autowired
    private AuditorAware<String> auditorAware;
    @Autowired
    private LoansRepository loansRepositoryTest;

    @AfterEach
    void tearDown() {
        loansRepositoryTest.deleteAll();
    }

    @Test
    public void testCurrentAuditor() {
        String currentAuditor = auditorAware.getCurrentAuditor().orElse("");
        assertThat(currentAuditor).isEqualTo("LOANS_MS");
    }

    @Test
    void findByMobileNumber() {
        // given
        Loans loan = new Loans(LOAN_ID, MOBILE_NUMBER, LOAN_NUMBER, LOAN_TYPE, TOTAL_LOAN,
                AMOUNT_PAID, OUTSTANDING_AMOUNT);
        loansRepositoryTest.save(loan);

        // when
        Optional<Loans> optionalLoans = loansRepositoryTest.findByMobileNumber(MOBILE_NUMBER);

        // then
        assertThat(optionalLoans.isPresent()).isTrue();
    }

    @Test
    void findByCardNumber() {
        // given
        Loans loan = new Loans(LOAN_ID, MOBILE_NUMBER, LOAN_NUMBER, LOAN_TYPE, TOTAL_LOAN,
                AMOUNT_PAID, OUTSTANDING_AMOUNT);
        loansRepositoryTest.save(loan);

        // when
        Optional<Loans> optionalLoans = loansRepositoryTest.findByLoanNumber(LOAN_NUMBER);

        // then
        assertThat(optionalLoans.isPresent()).isTrue();
    }
}