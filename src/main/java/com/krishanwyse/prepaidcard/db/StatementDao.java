package com.krishanwyse.prepaidcard.db;

import com.krishanwyse.prepaidcard.core.StatementEntry;
import com.krishanwyse.prepaidcard.core.mapper.StatementEntryMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(StatementEntryMapper.class)
public interface StatementDao {
    @SqlQuery(
            "SELECT M.name, T.created_at as created, (T.remaining_amount + T.captured_amount) AS amount " +
            "FROM merchants M " +
            "JOIN transactions T ON M.id = T.merchant_id " +
            "JOIN cards C ON C.id = T.card_id " +
            "WHERE C.id = :id " +
            "ORDER BY T.created_at ASC"
    )
    List<StatementEntry> selectByCardId(@Bind("id") long id);
}
