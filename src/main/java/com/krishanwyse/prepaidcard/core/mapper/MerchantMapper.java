package com.krishanwyse.prepaidcard.core.mapper;

import com.krishanwyse.prepaidcard.core.Merchant;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MerchantMapper implements ResultSetMapper<Merchant> {
    public Merchant map(int index, ResultSet set, StatementContext context) throws SQLException {
        return new Merchant(
                set.getLong("id"),
                set.getString("name"),
                set.getDouble("balance")
        );
    }
}
