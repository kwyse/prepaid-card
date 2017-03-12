package com.krishanwyse.prepaidcard.core.mapper;

import com.krishanwyse.prepaidcard.core.Card;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CardMapper implements ResultSetMapper<Card> {
    public Card map(int index, ResultSet set, StatementContext context) throws SQLException {
        return new Card(
                set.getLong("id"),
                set.getString("name"),
                set.getDouble("balance")
        );
    }
}
