package com.krishanwyse.prepaidcard.db;

import com.krishanwyse.prepaidcard.core.Card;
import com.krishanwyse.prepaidcard.core.mapper.CardMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(CardMapper.class)
public interface CardDao {
    @SqlUpdate("INSERT INTO cards (name, balance) VALUES (:name, :balance)")
    @GetGeneratedKeys
    long insert(@BindBean Card card);

    @SqlQuery(
            "SELECT C.id, C.name, C.balance, SUM(T.remaining_amount) AS blocked " +
            "FROM cards C " +
            "LEFT JOIN transactions T ON C.id = T.card_id " +
            "GROUP BY C.id " +
            "ORDER BY C.id ASC"
    )
    List<Card> selectAll();

    @SqlQuery(
            "SELECT C.id, C.name, C.balance, SUM(T.remaining_amount) AS blocked " +
            "FROM cards C " +
            "LEFT JOIN transactions T ON C.id = T.card_id " +
            "WHERE C.id = :id " +
            "GROUP BY C.id"
    )
    Card selectById(@Bind("id") long id);

    @SqlUpdate("UPDATE cards SET balance = :balance WHERE id = :id")
    long updateBalance(@Bind("id") long id, @Bind("balance") double balance);
}
