package com.eazybytes.cards.repository;

import com.eazybytes.cards.audit.AuditConfig;
import com.eazybytes.cards.entity.Cards;
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
class CardsRepositoryTest {

    private static final String MOBILE_NUMBER = "3809745312";
    private static final String CARD_NUMBER = "7889312312";
    private static final String CARD_TYPE = "Savings";
    private static final long CARD_ID = 12L;
    private static final int TOTAL_LIMIT = 50000;
    private static final int AMOUNT_USED = 10000;
    private static final int AVAILABLE_AMOUNT = 40000;


    @Autowired
    private AuditorAware<String> auditorAware;
    @Autowired
    private CardsRepository cardsRepositoryTest;

    @AfterEach
    void tearDown() {
        cardsRepositoryTest.deleteAll();
    }

    @Test
    public void testCurrentAuditor() {
        String currentAuditor = auditorAware.getCurrentAuditor().orElse("");
        assertThat(currentAuditor).isEqualTo("CARDS_MS");
    }

    @Test
    void findByMobileNumber() {
        // given
        Cards cards = new Cards(CARD_ID, MOBILE_NUMBER, CARD_NUMBER,
                CARD_TYPE, TOTAL_LIMIT, AMOUNT_USED, AVAILABLE_AMOUNT);
        cardsRepositoryTest.save(cards);

        // when
        Optional<Cards> optionalCards = cardsRepositoryTest.findByMobileNumber(MOBILE_NUMBER);

        // then
        assertThat(optionalCards.isPresent()).isTrue();
    }

    @Test
    void findByCardNumber() {
        // given
        Cards cards = new Cards(CARD_ID, MOBILE_NUMBER, CARD_NUMBER,
                CARD_TYPE, TOTAL_LIMIT, AMOUNT_USED, AVAILABLE_AMOUNT);
        cardsRepositoryTest.save(cards);

        // when
        Optional<Cards> optionalCards = cardsRepositoryTest.findByCardNumber(CARD_NUMBER);

        // then
        assertThat(optionalCards.isPresent()).isTrue();
    }
}