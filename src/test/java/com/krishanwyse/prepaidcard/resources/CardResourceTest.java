package com.krishanwyse.prepaidcard.resources;

import com.krishanwyse.prepaidcard.core.*;
import com.krishanwyse.prepaidcard.db.CardDao;
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
    private static final CardDao dao = mock(CardDao.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new CardResource(dao))
            .build();

    @Test
    public void getAllWhenEmpty() throws Exception {
        List<Card> cards = new ArrayList<>();
        when(dao.selectAll()).thenReturn(cards);

        List<Card> result = resources.client()
                .target("/cards")
                .request()
                .get(new GenericType<List<Card>>() {});

        assertThat(result).hasSize(0);
        verify(dao, times(1)).selectAll();
    }

    @Test
    public void getAllWhenNotEmpty() throws Exception {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(1L, "John Smith", 123, 7));
        cards.add(new Card(2L, "Jane Doe", 67, 3));
        when(dao.selectAll()).thenReturn(cards);

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
        when(dao.selectById(1L)).thenReturn(expected);

        Card actual = resources.client().target("/cards/1").request().get(new GenericType<Card>() {});
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void getNonExistingId() throws Exception {
        when(dao.selectById(2L)).thenReturn(null);
        assertThatThrownBy(() ->
                resources.client().
                        target("/cards/2").
                        request().
                        get(new GenericType<Card>() {})
        )
                .isInstanceOf(BadRequestException.class);

//        verify(dao, times(1)).selectByCardId(2L);
    }

    @Test
    public void add() throws Exception {
        Card expected = new Card(1L, "John Smith", 1000);
        Card input = new Card("John Smith", 1000);
        when(dao.insert(any(Card.class))).thenReturn(1L);

        Card actual = resources.client()
                .target("/cards")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(input), Card.class);

        assertThat(actual).isEqualToComparingFieldByField(expected);
        verify(dao, times(1)).insert(any(Card.class));
    }

    @Test
    public void loadWithPositiveAmount() {
        Card before = new Card(1L, "John Smith", 100);
        Card after = new Card(1L, "John Smith", 120);
        when(dao.selectById(1L)).thenReturn(before);
        when(dao.updateBalance(1L, 120)).thenReturn(1L);

        Card actual = resources.client()
                .target("/cards/1")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(new Amount(20)), Card.class);

        assertThat(actual).isEqualToComparingFieldByField(after);
        verify(dao, times(1)).updateBalance(1L, 120);
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

//        verify(dao, times(0)).selectByCardId(1L);
    }
}
