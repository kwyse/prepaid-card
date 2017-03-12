package com.krishanwyse.prepaidcard.db;

import com.krishanwyse.prepaidcard.core.Card;
import com.krishanwyse.prepaidcard.core.mapper.CardMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(CardMapper.class)
public interface CardDao {
    @SqlUpdate("INSERT INTO cards (name, balance) VALUES (:name, :balance)")
    @GetGeneratedKeys
    long insert(@BindBean Card card);

    @SqlQuery("SELECT * FROM cards WHERE id = :id")
    Card findById(@Bind("id") long id);

    @SqlUpdate("UPDATE cards SET balance = :balance WHERE id = :id")
    long update(@Bind("id") long id, @Bind("balance") double balance);
}
