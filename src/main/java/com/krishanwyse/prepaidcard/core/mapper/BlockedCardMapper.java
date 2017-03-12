package com.krishanwyse.prepaidcard.core.mapper;

import com.krishanwyse.prepaidcard.core.BlockedCard;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BlockedCardMapper implements ResultSetMapper<BlockedCard> {
    public BlockedCard map(int index, ResultSet set, StatementContext context) throws SQLException {
        return new BlockedCard(
                set.getLong("id"),
                set.getString("name"),
                set.getDouble("balance"),
                set.getDouble("blocked")
        );
    }
}
