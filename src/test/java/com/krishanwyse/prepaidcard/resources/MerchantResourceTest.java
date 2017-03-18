package com.krishanwyse.prepaidcard.resources;

import com.krishanwyse.prepaidcard.core.Amount;
import com.krishanwyse.prepaidcard.core.Merchant;
import com.krishanwyse.prepaidcard.db.MerchantDao;
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

public class MerchantResourceTest {
    private static final MerchantDao merchantDao = mock(MerchantDao.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new MerchantResource(merchantDao))
            .build();

    @Test
    public void getAllWhenEmpty() throws Exception {
        List<Merchant> merchants = new ArrayList<>();
        when(merchantDao.getAll()).thenReturn(merchants);

        List<Merchant> result = resources.client()
                .target("/merchants")
                .request()
                .get(new GenericType<List<Merchant>>() {});

        assertThat(result).hasSize(0);
        verify(merchantDao, times(1)).getAll();
    }

    @Test
    public void getAllWhenNotEmpty() throws Exception {
        List<Merchant> merchants = new ArrayList<>();
        merchants.add(new Merchant(1, "Magic Coffee", 10000));
        merchants.add(new Merchant(2, "Grim's Frozen Sandwiches", 6500));
        merchants.add(new Merchant(3, "Kraken Kale", 12000));
        when(merchantDao.getAll()).thenReturn(merchants);

        List<Merchant> result = resources.client()
                .target("/merchants")
                .request()
                .get(new GenericType<List<Merchant>>() {});

        assertThat(result).hasSameSizeAs(merchants);
        assertThat(result.get(0)).isEqualToComparingFieldByField(merchants.get(0));
        assertThat(result.get(1)).isEqualToComparingFieldByField(merchants.get(1));
        assertThat(result.get(2)).isEqualToComparingFieldByField(merchants.get(2));
    }

    @Test
    public void getExistingId() throws Exception {
        Merchant expected = new Merchant(1, "Magic Coffee", 10000);
        when(merchantDao.findById(1L)).thenReturn(expected);

        Merchant actual = resources.client()
                .target("/merchants/1")
                .request()
                .get(new GenericType<Merchant>() {});

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void getNonExistingId() throws Exception {
        when(merchantDao.findById(2L)).thenReturn(null);
        assertThatThrownBy(() ->
            resources.client().
                    target("/merchants/2").
                    request().
                    get(new GenericType<Merchant>() {})
        )
                .isInstanceOf(BadRequestException.class);

//        verify(merchantDao, times(1)).selectById(2L);
    }

    @Test
    public void updateWithValidBalance() throws Exception {
        Merchant before = new Merchant(1, "Magic Coffee", 10000);
        Merchant after = new Merchant(1, "Magic Coffee", 9500);
        when(merchantDao.findById(1L)).thenReturn(before).thenReturn(after);
        when(merchantDao.update(1L, 9500)).thenReturn(1L);

        Amount amount = new Amount(500);
        Merchant actual = resources.client()
                .target("/merchants/1")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(amount), Merchant.class);

        assertThat(actual).isEqualToComparingFieldByField(after);
        verify(merchantDao, times(2)).findById(1);
        verify(merchantDao, times(1)).update(1L, 9500);
    }

    @Test
    public void updateWithTooLargeAmount() throws Exception {
        Merchant merchant = new Merchant("Magic Coffee", 9000);
        when(merchantDao.findById(1L)).thenReturn(merchant);

        // TODO: This should check the error message
        // TODO: It should also check the other exception branch
        Amount amount = new Amount(11000);
        assertThatThrownBy(() ->
                resources.client()
                        .target("/merchants/1")
                        .request(MediaType.APPLICATION_JSON)
                        .put(Entity.json(amount), Merchant.class)
        )
                .isInstanceOf(BadRequestException.class);

//        verify(merchantDao, times(1)).selectById(1L);
    }

    @Test
    public void updateWithNegativeAmount() throws Exception {
        Merchant merchant = new Merchant("Magic Coffee", 9000);
        when(merchantDao.findById(1L)).thenReturn(merchant);

        // TODO: This should check the error message
        // TODO: It should also check the other exception branch
        Amount amount = new Amount(-1);
        assertThatThrownBy(() ->
                resources.client()
                        .target("/merchants/2")
                        .request(MediaType.APPLICATION_JSON)
                        .put(Entity.json(amount), Merchant.class)
        )
                .isInstanceOf(BadRequestException.class);

//        verify(merchantDao, times(1)).selectById(1L);
    }
}
