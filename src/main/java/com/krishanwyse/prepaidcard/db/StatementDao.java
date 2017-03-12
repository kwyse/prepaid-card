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
            "SELECT M.name, STRFTIME('%Y-%m-%d %H:%M:%f',T.created) as created, (T.remaining + T.captured) AS amount " +
            "FROM merchants M " +
            "JOIN transactions T ON M.id = T.merchant " +
            "JOIN cards C ON C.id = T.card " +
            "WHERE C.id = :id"
    )
    List<StatementEntry> getAll(@Bind("id") long id);
}
