package ca.utoronto.utm.mcs;

import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import dagger.Module;
import dagger.Provides;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Module
public class DaggerModule {

    private static HttpServer server;
    private static MongoClient db;

    @Provides public MongoClient provideMongoClient() {
        db = MongoClients.create();
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

}
