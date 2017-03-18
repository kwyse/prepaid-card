package com.krishanwyse.prepaidcard.db;

import com.krishanwyse.prepaidcard.core.BlockedCard;
import com.krishanwyse.prepaidcard.core.mapper.BlockedCardMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(BlockedCardMapper.class)
public interface BlockedCardDao {
    @SqlQuery(
            "SELECT C.id, C.name, C.balance, SUM(T.remaining_amount) AS blocked " +
            "FROM cards C " +
            "LEFT JOIN transactions T ON C.id = T.card_id " +
            "GROUP BY C.id"
    )
    List<BlockedCard> getAll();

    @SqlQuery(
            "SELECT C.id, C.name, C.balance, SUM(T.remaining_amount) AS blocked " +
            "FROM cards C " +
            "LEFT JOIN transactions T ON C.id = T.card_id " +
            "WHERE C.id = :id " +
            "GROUP BY C.id"
    )
    BlockedCard findById(@Bind("id") long id);
}
