package ca.utoronto.utm.mcs;

import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import dagger.Module;
import dagger.Provides;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import javax.inject.Named;

@Module
public class DaggerModule {

    private static HttpServer server;
    private static MongoClient db;
    private static String databaseName;
    private static String collectionName;

    @Provides public MongoClient provideMongoClient() {
        if (db == null){
            db = MongoClients.create();
        }
        return db;
    }

    @Provides public HttpServer provideHttpServer() {
        try {
            server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return server;
        }
    }

    @Provides
    @Named("databaseName")
    public String provideDatabaseName() {
        databaseName = "csc301a2";
        return databaseName;
    }

    @Provides
    @Named("collectionName")
    public String provideCollectionName() {
        collectionName = "posts";
        return collectionName;
    }
}
