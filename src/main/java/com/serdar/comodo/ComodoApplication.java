package com.serdar.comodo;

import com.serdar.comodo.db.ConnectionSummaryDao;
import com.serdar.comodo.model.ConnectionSummary;
import com.serdar.comodo.resource.ConnectionSummaryResource;
import com.serdar.comodo.resource.FileUploadResource;
import com.serdar.comodo.util.CSVReader;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;

import java.util.List;

public class ComodoApplication extends Application<AppConfiguration> {

    private static List<ConnectionSummary> summaries;

    public static void main(String[] args) throws Exception {
        new ComodoApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));

    }

    @Override
    public void run(AppConfiguration configuration,
                    Environment environment) {

        //DBIFactory factory = new DBIFactory();
        JdbcConnectionPool jdbcConnectionPool = JdbcConnectionPool.create("jdbc:h2:./src/main/resources/sample", "", "");
        DBI jdbi = new DBI(jdbcConnectionPool);

        ConnectionSummaryDao connectionSummaryDAO = jdbi.onDemand(ConnectionSummaryDao.class);
        //Creating connection_summary table if not exist
        connectionSummaryDAO.createTable();

        //init file upload location to this resource
        final FileUploadResource fileUploadResource = new FileUploadResource(connectionSummaryDAO, configuration.getFileLocation());

        final ConnectionSummaryResource connectionSummaryResource = new ConnectionSummaryResource(connectionSummaryDAO);

        CSVReader.getInstance().init();

        //for enabling mutipart feature
        environment.jersey().register(MultiPartFeature.class);
        environment.jersey().register(fileUploadResource);
        environment.jersey().register(connectionSummaryResource);

    }



}