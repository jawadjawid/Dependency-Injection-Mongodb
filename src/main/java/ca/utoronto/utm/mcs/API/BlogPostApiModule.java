package ca.utoronto.utm.mcs.API;

import dagger.Module;
import dagger.Provides;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import javax.inject.Named;

@Module
public class BlogPostApiModule {

    private static MongoClient db;
    private static String databaseName;
    private static String collectionName;

    @Provides
    public MongoClient provideMongoClient() {
        if (db == null){
            db = MongoClients.create();
        }
        return db;
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
