package com.krishanwyse.prepaidcard.db;

import com.krishanwyse.prepaidcard.core.Transaction;
import com.krishanwyse.prepaidcard.core.mapper.TransactionMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(TransactionMapper.class)
public interface TransactionDao {
    @SqlUpdate(
            "INSERT INTO transactions " +
            "(card, merchant, remaining, captured) " +
            "VALUES (:card, :merchant, :remaining, :captured)"
    )
    @GetGeneratedKeys
    long add(@BindBean Transaction transaction);

    @SqlQuery("SELECT * FROM transactions WHERE id = :id")
    Transaction findById(@Bind("id") long id);

    @SqlQuery("SELECT SUM(remaining) FROM transactions JOIN cards ON transactions.card = :id")
    int getBlockedAmountById(@Bind("id") long id);

    @SqlUpdate("UPDATE transactions SET remaining = :remaining, captured = :captured WHERE id = :id")
    long update(@Bind("id") long id, @Bind("remaining") double remaining, @Bind("captured") double captured);
}
