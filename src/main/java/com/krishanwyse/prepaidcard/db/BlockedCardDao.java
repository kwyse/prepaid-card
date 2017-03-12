package com.krishanwyse.prepaidcard.db;

import com.krishanwyse.prepaidcard.core.BlockedCard;
import com.krishanwyse.prepaidcard.core.mapper.BlockedCardMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(BlockedCardMapper.class)
public interface BlockedCardDao {
    @SqlQuery(
            "SELECT T.card AS id, C.name, C.balance, SUM(T.remaining) AS blocked " +
            "FROM cards C " +
            "JOIN transactions T ON C.id = T.card " +
            "GROUP BY T.card"
    )
    List<BlockedCard> getAll();
}
