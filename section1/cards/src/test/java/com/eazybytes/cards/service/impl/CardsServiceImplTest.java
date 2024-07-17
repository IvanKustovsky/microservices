package com.eazybytes.cards.service.impl;

import com.eazybytes.cards.constants.CardsConstants;
import com.eazybytes.cards.dto.CardsDto;
import com.eazybytes.cards.entity.Cards;
import com.eazybytes.cards.exception.CardAlreadyExistsException;
import com.eazybytes.cards.exception.ResourceNotFoundException;
import com.eazybytes.cards.repository.CardsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Cards Service Test Class")
class CardsServiceImplTest {

    private static final String MOBILE_NUMBER = "3809678512";
    private static final String CARD_NUMBER = "1234567890";
    private static final Long CARD_ID = 1L;

    @Mock
    private CardsRepository cardsRepositoryMock;

    @InjectMocks
    private CardsServiceImpl cardsServiceImplTest;


    @Test
    @DisplayName("Should throw CardAlreadyExistsException when trying to create card for the given mobile number that already exists")
    void createCardWhenExists() {
        // given
        Cards newCard = new Cards();
        newCard.setMobileNumber(MOBILE_NUMBER);

        when(cardsRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.of(newCard));

        // when, then
        assertThatThrownBy(() -> cardsServiceImplTest.createCard(MOBILE_NUMBER))
                .isInstanceOf(CardAlreadyExistsException.class)
                .hasMessage("Card already registered with given mobileNumber " + MOBILE_NUMBER);

        verify(cardsRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
        verify(cardsRepositoryMock, never()).save(any(Cards.class));
    }

    @Test
    @DisplayName("Should create card when no card exists for the given mobile number")
    void createCardWhenNotExists() {
        // given
        Cards newCard = new Cards();
        newCard.setMobileNumber(MOBILE_NUMBER);

        when(cardsRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.empty());
        when(cardsRepositoryMock.save(any(Cards.class))).thenReturn(newCard);

        // when
        cardsServiceImplTest.createCard(MOBILE_NUMBER);

        // then
        verify(cardsRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
        verify(cardsRepositoryMock, times(1)).save(any(Cards.class));

        ArgumentCaptor<Cards> cardCaptor = ArgumentCaptor.forClass(Cards.class);
        verify(cardsRepositoryMock).save(cardCaptor.capture());
        Cards savedCard = cardCaptor.getValue();

        assertEquals(MOBILE_NUMBER, savedCard.getMobileNumber());
        assertEquals(CardsConstants.CREDIT_CARD, savedCard.getCardType());
        assertEquals(CardsConstants.NEW_CARD_LIMIT, savedCard.getTotalLimit());
        assertEquals(0, savedCard.getAmountUsed());
        assertEquals(CardsConstants.NEW_CARD_LIMIT, savedCard.getAvailableAmount());
    }

    @Test
    @DisplayName("Should fetch card when it exists for the given mobile number")
    void fetchCardWhenExists() {
        // given
        Cards newCard = new Cards();
        newCard.setMobileNumber(MOBILE_NUMBER);

        when(cardsRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.of(newCard));

        // when
        CardsDto result = cardsServiceImplTest.fetchCard(MOBILE_NUMBER);

        // then
        assertNotNull(result);
        assertEquals(newCard.getMobileNumber(), result.getMobileNumber());

        verify(cardsRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to fetch card for the given mobile number that does not exist")
    void fetchCardWhenNotExists() {
        // given
        when(cardsRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> cardsServiceImplTest.fetchCard(MOBILE_NUMBER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("%s not found with the given input data %s : '%s'",
                        "Card", "mobileNumber", MOBILE_NUMBER);

        verify(cardsRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
    }

    @Test
    @DisplayName("Should update card when it exists for the given card number")
    void updateCardWhenExists() {
        // given
        CardsDto cardsDto = new CardsDto();
        cardsDto.setCardNumber(CARD_NUMBER);
        Cards newCard = new Cards();

        when(cardsRepositoryMock.findByCardNumber(CARD_NUMBER)).thenReturn(Optional.of(newCard));
        when(cardsRepositoryMock.save(any(Cards.class))).thenReturn(newCard);

        // when
        boolean isUpdated = cardsServiceImplTest.updateCard(cardsDto);

        // then
        assertTrue(isUpdated);

        verify(cardsRepositoryMock, times(1)).findByCardNumber(CARD_NUMBER);
        verify(cardsRepositoryMock, times(1)).save(any(Cards.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to update card for the given card number that does not exist")
    void updateCardWhenNotExists() {
        // given
        CardsDto cardsDto = new CardsDto();
        cardsDto.setCardNumber(CARD_NUMBER);

        when(cardsRepositoryMock.findByCardNumber(CARD_NUMBER)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> cardsServiceImplTest.updateCard(cardsDto))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("%s not found with the given input data %s : '%s'",
                                "Card", "CardNumber", CARD_NUMBER);

        verify(cardsRepositoryMock, times(1)).findByCardNumber(CARD_NUMBER);
        verify(cardsRepositoryMock, never()).save(any(Cards.class));
    }


    @Test
    @DisplayName("Should delete card when it exists for the given mobile number")
    void deleteCardWhenExists() {
        // given
        Cards foundCard = new Cards();
        foundCard.setCardId(CARD_ID);

        when(cardsRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.of(foundCard));

        // when
        boolean isDeleted = cardsServiceImplTest.deleteCard(MOBILE_NUMBER);

        // then
        assertTrue(isDeleted);

        verify(cardsRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
        verify(cardsRepositoryMock, times(1)).deleteById(CARD_ID);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to delete card for the given mobile number that does not exist")
    void deleteCardWhenNotExists() {
        // given
        Cards foundCard = new Cards();
        foundCard.setCardId(CARD_ID);

        when(cardsRepositoryMock.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> cardsServiceImplTest.fetchCard(MOBILE_NUMBER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("%s not found with the given input data %s : '%s'",
                        "Card", "mobileNumber", MOBILE_NUMBER);

        verify(cardsRepositoryMock, times(1)).findByMobileNumber(MOBILE_NUMBER);
        verify(cardsRepositoryMock, never()).deleteById(any());
    }
}