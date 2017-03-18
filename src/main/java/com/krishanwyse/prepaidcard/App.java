package com.krishanwyse.prepaidcard;

import com.krishanwyse.prepaidcard.db.*;
import com.krishanwyse.prepaidcard.resources.CardResource;
import com.krishanwyse.prepaidcard.resources.MerchantResource;
import com.krishanwyse.prepaidcard.resources.StatementResource;
import com.krishanwyse.prepaidcard.resources.TransactionResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

public class App extends Application<AppConfiguration> {
    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> boostrap) {
        boostrap.addBundle(new MigrationsBundle<AppConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(AppConfiguration config) {
                return config.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(AppConfiguration config, Environment env) {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(env, config.getDataSourceFactory(), "postgresql");

        final CardDao cardDao = jdbi.onDemand(CardDao.class);
        final TransactionDao transactionDao = jdbi.onDemand(TransactionDao.class);
        final MerchantDao merchantDao = jdbi.onDemand(MerchantDao.class);
        final StatementDao statementDao = jdbi.onDemand(StatementDao.class);

        env.jersey().register(new CardResource(cardDao));
        env.jersey().register(new MerchantResource(merchantDao));
        env.jersey().register(new TransactionResource(cardDao, transactionDao));
        env.jersey().register(new StatementResource(statementDao));
    }
}
