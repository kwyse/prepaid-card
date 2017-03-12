package com.krishanwyse.prepaidcard.core.mapper;

import com.krishanwyse.prepaidcard.core.StatementEntry;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatementEntryMapper implements ResultSetMapper<StatementEntry> {
    public StatementEntry map(int index, ResultSet set, StatementContext context) throws SQLException {
        return new StatementEntry(
                set.getString("name"),
                set.getDouble("amount"),
                set.getTimestamp("created")
        );
    }
}

