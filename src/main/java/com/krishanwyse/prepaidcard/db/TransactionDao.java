package com.krishanwyse.prepaidcard.db;

import com.krishanwyse.prepaidcard.core.Transaction;
import com.krishanwyse.prepaidcard.core.mapper.TransactionMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(TransactionMapper.class)
public interface TransactionDao {
    @SqlUpdate(
            "INSERT INTO transactions " +
            "(card_id, merchant_id, remaining_amount, captured_amount) " +
            "VALUES (:card, :merchant, :remaining, :captured)"
    )
    @GetGeneratedKeys
    long insert(@BindBean Transaction transaction);

    @SqlQuery("SELECT * FROM transactions WHERE id = :id")
    Transaction selectById(@Bind("id") long id);

    @SqlUpdate("UPDATE transactions SET remaining_amount = :remaining, captured_amount = :captured WHERE id = :id")
    void updateAmount(@Bind("id") long id, @Bind("remaining") double remaining, @Bind("captured") double captured);
}
