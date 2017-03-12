package com.krishanwyse.prepaidcard.core.mapper;

import com.krishanwyse.prepaidcard.core.Transaction;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionMapper implements ResultSetMapper<Transaction> {
    public Transaction map(int index, ResultSet set, StatementContext context) throws SQLException {
        return new Transaction(
                set.getLong("id"),
                set.getLong("card"),
                set.getLong("merchant"),
                set.getLong("remaining"),
                set.getLong("captured")
        );
    }
}