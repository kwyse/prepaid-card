package com.krishanwyse.prepaidcard.db;

import com.krishanwyse.prepaidcard.core.Merchant;
import com.krishanwyse.prepaidcard.core.mapper.MerchantMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(MerchantMapper.class)
public interface MerchantDao {
    @SqlQuery("SELECT * FROM merchants")
    List<Merchant> selectAll();

    @SqlQuery("SELECT * FROM merchants WHERE id = :id")
    Merchant selectById(@Bind("id") long id);

    @SqlUpdate("UPDATE merchants SET balance = :balance WHERE id = :id")
    long updateBalance(@Bind("id") long id, @Bind("balance") double balance);
}
