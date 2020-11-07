package ca.utoronto.utm.mcs.API;

import ca.utoronto.utm.mcs.Utils;
import ca.utoronto.utm.mcs.exceptions.BadRequestException;
import ca.utoronto.utm.mcs.exceptions.NotFoundException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;

public class BlogPostApi implements HttpHandler {

    private MongoClient db;

    @Inject
    public BlogPostApi(MongoClient db) {
        this.db = db;
    }

    @Override
    public void handle(HttpExchange r) {
        try {
            if (r.getRequestMethod().equals("PUT")) {
                handlePut(r);
            }else if(r.getRequestMethod().equals("GET")) {
                handleGet(r);
            }else if(r.getRequestMethod().equals("DELETE")) {
                handleDelete(r);
            }else {
                r.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePut(HttpExchange r) throws Exception{
        String title, author, content;
        JSONArray tags;
        try{
            String body = Utils.convert(r.getRequestBody());
            JSONObject deserialized = new JSONObject(body);
            title = deserialized.getString("title");
            author = deserialized.getString("author");
            content = deserialized.getString("content");
            tags = deserialized.getJSONArray("tags");
        } catch (JSONException e) {
            r.sendResponseHeaders(400, -1);
            return;
        }

        Document document = new Document();
        document.put("title", title);
        document.put("author", author);
        document.put("content", content);
        //document.put("tags", tags);

        try{
            db.getDatabase("csc301a2").getCollection("posts").insertOne(document);
        } catch (Exception e) {
            r.sendResponseHeaders(500, -1);
            return;
        }
        r.sendResponseHeaders(200, -1);
    }

    public void handleGet(HttpExchange r) throws IOException {
    }

    public void handleDelete(HttpExchange r) throws IOException {
    }
}
