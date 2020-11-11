package ca.utoronto.utm.mcs.API;

import ca.utoronto.utm.mcs.Utils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class BlogPostApi implements HttpHandler {

    private MongoClient db;
    private String databaseName;
    private String collectionName;
    private MongoCollection collection ;

    @Inject
    public BlogPostApi(MongoClient db, @Named("databaseName") String databaseName, @Named("collectionName") String collectionName) {
        this.db = db;
        this.databaseName = databaseName;
        this.collectionName = collectionName;
    }

    @Override
    public void handle(HttpExchange r) {
        try {
            this.collection = db.getDatabase(databaseName).getCollection(collectionName);
            switch (r.getRequestMethod()) {
                case "PUT":
                    handlePut(r);
                    break;
                case "GET":
                    handleGet(r);
                    break;
                case "DELETE":
                    handleDelete(r);
                    break;
                default:
                    r.sendResponseHeaders(405, -1);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePut(HttpExchange r) throws IOException {
        try{
            String title, author, content;
            JSONArray tags;
            Document document = new Document();
            ArrayList tagsArray = new ArrayList();
            JSONObject resJson = new JSONObject();
            String res;

            try{
                String body = Utils.convert(r.getRequestBody());
                JSONObject deserialized = new JSONObject(body);
                title = deserialized.getString("title");
                author = deserialized.getString("author");
                content = deserialized.getString("content");
                tags = deserialized.getJSONArray("tags");
                for (int i = 0; i < tags.length(); i++) {
                    tagsArray.add(tags.getString(i));
                }
            } catch (JSONException e) {
                r.sendResponseHeaders(400, -1);
                return;
            }

            document.put("title", title);
            document.put("author", author);
            document.put("content", content);
            document.put("tags", tagsArray);
            collection.insertOne(document);

            String id = document.getObjectId("_id").toString();
            resJson.put("_id", id);
            res = resJson.toString();
            r.sendResponseHeaders(200, res.length());
            OutputStream os = r.getResponseBody();
            os.write(res.getBytes());
            os.close();

        } catch (Exception e){
        	r.sendResponseHeaders(500, -1);
        }
    }

    public void handleGet(HttpExchange r) throws IOException {
    	try {
    		String title = null, _id = null;
    		JSONObject query;
    		
    		try{
                String body = Utils.convert(r.getRequestBody());
                query = new JSONObject(body);
                if(query.has("_id"))
                	_id = query.getString("_id");
                else if(query.has("title"))
                	title = query.getString("title");
                else {
                	r.sendResponseHeaders(400, -1);
                    return;
                }
            } catch (JSONException e) {
                r.sendResponseHeaders(400, -1);
                return;
            }

    		Document queryDoc = new Document();
    		String response = null;
    		
    		if(_id != null) {
    			try {queryDoc.put("_id", new ObjectId(_id));}
    			catch(IllegalArgumentException e) {
    				r.sendResponseHeaders(400, -1);
    				return;
    			}
    			Document resDoc = (Document) collection.find(queryDoc).first();
    			if(resDoc == null) {
    				r.sendResponseHeaders(404, -1);
                    return;
    			}
    			JSONObject oid = new JSONObject();
    			oid.put("$oid", resDoc.getObjectId("_id"));
    			resDoc.replace("_id", oid);
;    			JSONArray arr = new JSONArray();
    			arr.put(resDoc);
    			response = arr.toString();
    		} else if(title != null) {
    			queryDoc.append("title", (new Document()).append("$regex", title));
    			Document sortDoc = new Document();
    			sortDoc.put("title", 1);
    			MongoCursor resCursor = collection.find(queryDoc).sort(sortDoc).cursor();
    			JSONArray arr = new JSONArray();
    			while(resCursor.hasNext()) {
    				Document curDoc = (Document)resCursor.next();
    				JSONObject oid = new JSONObject();
    				oid.put("$oid", curDoc.getObjectId("_id"));
    				curDoc.replace("_id", oid);
    				arr.put(curDoc);
    			}
    			if(arr.length() == 0) {
    				r.sendResponseHeaders(404, -1);
                    return;
    			}
    			
    			response = arr.toString();
    		}
    		
            r.sendResponseHeaders(200, response.length());
            OutputStream os = r.getResponseBody();
            os.write(response.getBytes());
            os.close();
    		
    	} catch (Exception e){
            r.sendResponseHeaders(500, -1);
        }
    }

    public void handleDelete(HttpExchange r) throws IOException {
        try {
            String _id;
            Document document = new Document();

            try{
                String body = Utils.convert(r.getRequestBody());
                JSONObject deserialized = new JSONObject(body);
                _id = deserialized.getString("_id");
            } catch (JSONException e) {
                r.sendResponseHeaders(400, -1);
                return;
            }

            try{document.put("_id", new ObjectId(_id));}
            catch(IllegalArgumentException e) {
            	r.sendResponseHeaders(400, -1);
				return;
            }

            if(collection.find(document).first() == null){
                r.sendResponseHeaders(404, -1);
                return;
            }
            collection.findOneAndDelete(document);
            r.sendResponseHeaders(200, -1);
        } catch (Exception e){
            r.sendResponseHeaders(500, -1);
        }
    }
}
