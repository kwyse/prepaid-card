package com.krishanwyse.prepaidcard;

import com.krishanwyse.prepaidcard.core.Transaction;
import com.krishanwyse.prepaidcard.db.BlockedCardDao;
import com.krishanwyse.prepaidcard.db.CardDao;
import com.krishanwyse.prepaidcard.db.MerchantDao;
import com.krishanwyse.prepaidcard.db.TransactionDao;
import com.krishanwyse.prepaidcard.resources.CardResource;
import com.krishanwyse.prepaidcard.resources.MerchantResource;
import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.Batch;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class App extends Application<AppConfiguration> {
    public static void main( String[] args ) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> boostrap) {
    }

    @Override
    public void run(AppConfiguration config, Environment env) {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(env, config.getDataSourceFactory(), "sqlite");

        Handle handle = jdbi.open();

        // TODO: Move this to migration script
        handle.execute("CREATE TABLE cards (id INTEGER PRIMARY KEY, name TEST, balance REAL)");
        handle.execute(
                "CREATE TABLE transactions " +
                "(id INTEGER PRIMARY KEY, card INT, merchant INT, remaining REAL, captured REAL, " +
                "FOREIGN KEY(card) REFERENCES cards(id))"
        );
//        handle.execute("create table transactions (id integer primary key, card int, merchant int, remaining real, captured real, foreign key(card) references cards(id))");

        handle.execute("CREATE TABLE merchants (id INTEGER PRIMARY KEY, name TEXT, balance REAL)");
        handle.execute("INSERT INTO merchants (name, balance) VALUES ('Clock Town Milk Bar', 10000)");

        handle.close();

        final CardDao cardDao = jdbi.onDemand(CardDao.class);
        final BlockedCardDao blockedCardDao = jdbi.onDemand(BlockedCardDao.class);
        final TransactionDao transactionDao = jdbi.onDemand(TransactionDao.class);
        env.jersey().register(new CardResource(cardDao, blockedCardDao, transactionDao));

        final MerchantDao merchantDao = jdbi.onDemand(MerchantDao.class);
        env.jersey().register(new MerchantResource(merchantDao));
    }
}
