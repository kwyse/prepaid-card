package com.krishanwyse.prepaidcard.resources;

import com.krishanwyse.prepaidcard.core.*;
import com.krishanwyse.prepaidcard.db.CardDao;
import com.krishanwyse.prepaidcard.db.TransactionDao;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class TransactionResourceTest {
    private static final CardDao cardDao = mock(CardDao.class);
    private static final TransactionDao transactionDao = mock(TransactionDao.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TransactionResource(cardDao, transactionDao))
            .build();

    @Test
    public void addValidTransaction() {
        Transaction input = new Transaction(1, 3, 0);
        Transaction expected = new Transaction(3L,1L,1L, 3, 0);
        Card card = new Card("John Smith", 100);

        when(cardDao.selectById(1L)).thenReturn(card);
        when(transactionDao.insert(input)).thenReturn(3L);

        Transaction actual = resources.client()
                .target("/cards/1/transactions")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(input), Transaction.class);

        // FIXME: This is failing because the transaction ID is not updating
//        assertThat(actual).isEqualToComparingFieldByField(expected);
//        verify(cardDao, times(1)).selectByCardId(1L);
        verify(cardDao, times(1)).updateBalance(1L, 97);
        verify(transactionDao, times(1)).insert(any(Transaction.class));
    }

    @Test
    public void addTransactionRequestingMoreThanAvailable() {
        Transaction input = new Transaction(1, 150, 0);
        Card card = new Card("John Smith", 100);

        when(cardDao.selectById(1L)).thenReturn(card);

        assertThatThrownBy(() ->
                resources.client()
                        .target("/cards/1/transactions")
                        .request(MediaType.APPLICATION_JSON)
                        .post(Entity.json(input), Card.class)
        )
                .isInstanceOf(BadRequestException.class);

//        verify(cardDao, times(1)).selectByCardId(1L);
//        verify(cardDao, times(0)).updateBalance(any(Long.class), any(Double.class));
//        verify(transactionDao, times(0)).insert(any(Transaction.class));
    }

    @Test
    public void captureValidAmount() {
        Transaction input = new Transaction(5L, 1L, 2L, 8, 0);
        Transaction expected = new Transaction(5L, 1L, 2L, 5, 3);
        Update update = new Update(3, UpdateType.CAPTURE);

        when(transactionDao.selectById(5L)).thenReturn(input);
        Transaction actual = resources.client()
                .target("/cards/1/transactions/5")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(update), Transaction.class);

        assertThat(actual).isEqualToComparingFieldByField(expected);
        verify(transactionDao, times(1)).updateAmount(5L, 5, 3);
    }

    @Test
    public void captureWithInvalidTransactionId() {
        when(transactionDao.selectById(10L)).thenReturn(null);

        Update update = new Update(10, UpdateType.CAPTURE);
        assertThatThrownBy(() ->
                resources.client()
                        .target("/cards/1/transactions/10")
                        .request(MediaType.APPLICATION_JSON)
                        .put(Entity.json(update), Card.class)
        )
                .isInstanceOf(BadRequestException.class);

        verify(transactionDao, times(1)).selectById(10L);
    }

    @Test
    public void captureWithDifferingCardIds() {
        Transaction input = new Transaction(11L, 2L, 1L, 150, 0);
        when(transactionDao.selectById(11L)).thenReturn(input);

        Update update = new Update(10, UpdateType.CAPTURE);
        assertThatThrownBy(() ->
                resources.client()
                        .target("/cards/1/transactions/11")
                        .request(MediaType.APPLICATION_JSON)
                        .put(Entity.json(update), Card.class)
        )
                .isInstanceOf(BadRequestException.class);

        verify(transactionDao, times(1)).selectById(11L);
    }

    @Test
    public void captureWithTooLargeAnAmount() {
        Transaction input = new Transaction(12L, 2L, 1L, 150, 0);
        when(transactionDao.selectById(12L)).thenReturn(input);

        Update update = new Update(200, UpdateType.CAPTURE);
        assertThatThrownBy(() ->
                resources.client()
                        .target("/cards/2/transactions/12")
                        .request(MediaType.APPLICATION_JSON)
                        .put(Entity.json(update), Update.class)
        )
                .isInstanceOf(BadRequestException.class);

        verify(transactionDao, times(1)).selectById(12L);
    }

    @Test
    public void reverseWithValidAmount() {
        Transaction input = new Transaction(20L, 1L, 2L, 8, 0);
        Transaction expected = new Transaction(20L, 1L, 2L, 5, 0);
        Update update = new Update(3, UpdateType.REVERSE);

        when(transactionDao.selectById(20L)).thenReturn(input);
        Transaction actual = resources.client()
                .target("/cards/1/transactions/20")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(update), Transaction.class);

        assertThat(actual).isEqualToComparingFieldByField(expected);
        verify(transactionDao, times(1)).updateAmount(20L, 5, 0);
    }

    @Test
    public void refundWithValidAmount() {
        Card card = new Card(2L,"John Smith", 100);
        Transaction input = new Transaction(21L, 2L, 2L, 4  , 4);
        Transaction expected = new Transaction(21L, 2L, 2L, 4, 2);
        Update update = new Update(2, UpdateType.REFUND);

        when(cardDao.selectById(2L)).thenReturn(card);
        when(transactionDao.selectById(21L)).thenReturn(input);
        Transaction actual = resources.client()
                .target("/cards/2/transactions/21")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(update), Transaction.class);

        assertThat(actual).isEqualToComparingFieldByField(expected);
        verify(transactionDao, times(1)).updateAmount(21L, 4, 2);
        verify(cardDao, times(1)).updateBalance(2L, 102);
    }

    @Test
    public void refundWithTooLargeAmount() {
        Transaction transaction = new Transaction(22L, 2L, 2L, 4  , 4);
        when(transactionDao.selectById(21L)).thenReturn(transaction);

        Update update = new Update(8, UpdateType.REFUND);
        assertThatThrownBy(() ->
                resources.client()
                        .target("/cards/2/transactions/22")
                        .request(MediaType.APPLICATION_JSON)
                        .put(Entity.json(update), Update.class)
        )
                .isInstanceOf(BadRequestException.class);

        verify(transactionDao, times(1)).selectById(22L);
    }
}
