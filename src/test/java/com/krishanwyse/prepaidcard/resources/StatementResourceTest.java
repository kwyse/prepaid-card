package com.krishanwyse.prepaidcard.resources;

import com.krishanwyse.prepaidcard.core.StatementEntry;
import com.krishanwyse.prepaidcard.db.StatementDao;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StatementResourceTest {
    private static final StatementDao dao = mock(StatementDao.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new StatementResource(dao))
            .build();

    @Test
    public void getAllWithNoStatements() {
        List<StatementEntry> empty = new ArrayList<>();
        when(dao.getAll(1L)).thenReturn(Collections.emptyList());

        List<StatementEntry> actual = resources.client()
                .target("/statements/1")
                .request()
                .get(new GenericType<List<StatementEntry>>() {});

        assertThat(actual).isEmpty();
        verify(dao, times(1)).getAll(1L);
    }

    @Test
    public void getAllWithStatements() {
        List<StatementEntry> entries = new ArrayList<>();
        entries.add(new StatementEntry("Magic Coffee", 3, new Timestamp(1L)));
        entries.add(new StatementEntry("Magic Coffee", 4, new Timestamp(2L)));
        when(dao.getAll(2L)).thenReturn(entries);

        List<StatementEntry> actual = resources.client()
                .target("/statements/2")
                .request()
                .get(new GenericType<List<StatementEntry>>() {});

        assertThat(actual).hasSameSizeAs(entries);
        assertThat(actual.get(0)).isEqualToComparingFieldByField(entries.get(0));
        assertThat(actual.get(1)).isEqualToComparingFieldByField(entries.get(1));
        verify(dao, times(1)).getAll(2L);
    }
}
