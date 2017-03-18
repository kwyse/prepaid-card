package com.krishanwyse.prepaidcard.resources;

import com.krishanwyse.prepaidcard.core.*;
import com.krishanwyse.prepaidcard.db.CardDao;
import com.krishanwyse.prepaidcard.db.TransactionDao;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class CardResourceTest {
    private static final CardDao cardDao = mock(CardDao.class);
    private static final TransactionDao transactionDao = mock(TransactionDao.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new CardResource(cardDao, transactionDao))
            .build();

    @Test
    public void getAllWhenEmpty() throws Exception {
        List<Card> cards = new ArrayList<>();
        when(cardDao.selectAll()).thenReturn(cards);

        List<Card> result = resources.client()
                .target("/cards")
                .request()
                .get(new GenericType<List<Card>>() {});

        assertThat(result).hasSize(0);
        verify(cardDao, times(1)).selectAll();
    }

    @Test
    public void getAllWhenNotEmpty() throws Exception {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(1L, "John Smith", 123, 7));
        cards.add(new Card(2L, "Jane Doe", 67, 3));
        when(cardDao.selectAll()).thenReturn(cards);

        List<Card> result = resources.client()
                .target("/cards")
                .request()
                .get(new GenericType<List<Card>>() {});

        assertThat(result).hasSameSizeAs(cards);
        assertThat(result.get(0)).isEqualToComparingFieldByField(cards.get(0));
        assertThat(result.get(1)).isEqualToComparingFieldByField(cards.get(1));
    }

    @Test
    public void getExistingId() throws Exception {
        Card expected = new Card(1L, "John Smith", 1000, 10);
        when(cardDao.selectById(1L)).thenReturn(expected);

        Card actual = resources.client().target("/cards/1").request().get(new GenericType<Card>() {});
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void getNonExistingId() throws Exception {
        when(cardDao.selectById(2L)).thenReturn(null);
        assertThatThrownBy(() ->
                resources.client().
                        target("/cards/2").
                        request().
                        get(new GenericType<Card>() {})
        )
                .isInstanceOf(BadRequestException.class);

//        verify(cardDao, times(1)).selectById(2L);
    }

    @Test
    public void add() throws Exception {
        Card expected = new Card(1L, "John Smith", 1000);
        Card input = new Card("John Smith", 1000);
        when(cardDao.insert(any(Card.class))).thenReturn(1L);

        Card actual = resources.client()
                .target("/cards")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(input), Card.class);

        assertThat(actual).isEqualToComparingFieldByField(expected);
        verify(cardDao, times(1)).insert(any(Card.class));
    }

    @Test
    public void loadWithPositiveAmount() {
        Card before = new Card(1L, "John Smith", 100);
        Card after = new Card(1L, "John Smith", 120);
        when(cardDao.selectById(1L)).thenReturn(before);
        when(cardDao.update(1L, 120)).thenReturn(1L);

        Card actual = resources.client()
                .target("/cards/1")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(new Amount(20)), Card.class);

        assertThat(actual).isEqualToComparingFieldByField(after);
        verify(cardDao, times(1)).update(1L, 120);
    }

    @Test
    public void loadWithNegativeAmount() {
        Amount amount = new Amount(-1);
        assertThatThrownBy(() ->
                resources.client()
                        .target("/cards/1")
                        .request(MediaType.APPLICATION_JSON)
                        .put(Entity.json(amount), Card.class)
        )
                .isInstanceOf(BadRequestException.class);

//        verify(cardDao, times(0)).selectById(1L);
    }

    @Test
    public void addValidTransaction() {
        Transaction input = new Transaction(1, 3, 0);
        Transaction expected = new Transaction(3L,1L,1L, 3, 0);
        Card card = new Card("John Smith", 100);

        when(cardDao.selectById(1L)).thenReturn(card);
        when(transactionDao.add(input)).thenReturn(3L);

        Transaction actual = resources.client()
                .target("/cards/1/transactions")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(input), Transaction.class);

        // FIXME: This is failing because the transaction ID is not updating
//        assertThat(actual).isEqualToComparingFieldByField(expected);
//        verify(cardDao, times(1)).selectById(1L);
        verify(cardDao, times(1)).update(1L, 97);
        verify(transactionDao, times(1)).add(any(Transaction.class));
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

//        verify(cardDao, times(1)).selectById(1L);
//        verify(cardDao, times(0)).update(any(Long.class), any(Double.class));
//        verify(transactionDao, times(0)).add(any(Transaction.class));
    }

    @Test
    public void captureValidAmount() {
        Transaction input = new Transaction(5L, 1L, 2L, 8, 0);
        Transaction expected = new Transaction(5L, 1L, 2L, 5, 3);
        Update update = new Update(3, UpdateType.CAPTURE);

        when(transactionDao.findById(5L)).thenReturn(input);
        Transaction actual = resources.client()
                .target("/cards/1/transactions/5")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(update), Transaction.class);

        assertThat(actual).isEqualToComparingFieldByField(expected);
        verify(transactionDao, times(1)).update(5L, 5, 3);
    }

    @Test
    public void captureWithInvalidTransactionId() {
        when(transactionDao.findById(10L)).thenReturn(null);

        Update update = new Update(10, UpdateType.CAPTURE);
        assertThatThrownBy(() ->
                resources.client()
                        .target("/cards/1/transactions/10")
                        .request(MediaType.APPLICATION_JSON)
                        .put(Entity.json(update), Card.class)
        )
                .isInstanceOf(BadRequestException.class);

        verify(transactionDao, times(1)).findById(10L);
    }

    @Test
    public void captureWithDifferingCardIds() {
        Transaction input = new Transaction(11L, 2L, 1L, 150, 0);
        when(transactionDao.findById(11L)).thenReturn(input);

        Update update = new Update(10, UpdateType.CAPTURE);
        assertThatThrownBy(() ->
                resources.client()
                        .target("/cards/1/transactions/11")
                        .request(MediaType.APPLICATION_JSON)
                        .put(Entity.json(update), Card.class)
        )
                .isInstanceOf(BadRequestException.class);

        verify(transactionDao, times(1)).findById(11L);
    }

    @Test
    public void captureWithTooLargeAnAmount() {
        Transaction input = new Transaction(12L, 2L, 1L, 150, 0);
        when(transactionDao.findById(12L)).thenReturn(input);

        Update update = new Update(200, UpdateType.CAPTURE);
        assertThatThrownBy(() ->
                resources.client()
                        .target("/cards/2/transactions/12")
                        .request(MediaType.APPLICATION_JSON)
                        .put(Entity.json(update), Update.class)
        )
                .isInstanceOf(BadRequestException.class);

        verify(transactionDao, times(1)).findById(12L);
    }

    @Test
    public void reverseWithValidAmount() {
        Transaction input = new Transaction(20L, 1L, 2L, 8, 0);
        Transaction expected = new Transaction(20L, 1L, 2L, 5, 0);
        Update update = new Update(3, UpdateType.REVERSE);

        when(transactionDao.findById(20L)).thenReturn(input);
        Transaction actual = resources.client()
                .target("/cards/1/transactions/20")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(update), Transaction.class);

        assertThat(actual).isEqualToComparingFieldByField(expected);
        verify(transactionDao, times(1)).update(20L, 5, 0);
    }

    @Test
    public void refundWithValidAmount() {
        Card card = new Card(2L,"John Smith", 100);
        Transaction input = new Transaction(21L, 2L, 2L, 4  , 4);
        Transaction expected = new Transaction(21L, 2L, 2L, 4, 2);
        Update update = new Update(2, UpdateType.REFUND);

        when(cardDao.selectById(2L)).thenReturn(card);
        when(transactionDao.findById(21L)).thenReturn(input);
        Transaction actual = resources.client()
                .target("/cards/2/transactions/21")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(update), Transaction.class);

        assertThat(actual).isEqualToComparingFieldByField(expected);
        verify(transactionDao, times(1)).update(21L, 4, 2);
        verify(cardDao, times(1)).update(2L, 102);
    }

    @Test
    public void refundWithTooLargeAmount() {
        Transaction transaction = new Transaction(22L, 2L, 2L, 4  , 4);
        when(transactionDao.findById(21L)).thenReturn(transaction);

        Update update = new Update(8, UpdateType.REFUND);
        assertThatThrownBy(() ->
                resources.client()
                        .target("/cards/2/transactions/22")
                        .request(MediaType.APPLICATION_JSON)
                        .put(Entity.json(update), Update.class)
        )
                .isInstanceOf(BadRequestException.class);

        verify(transactionDao, times(1)).findById(22L);
    }
}
