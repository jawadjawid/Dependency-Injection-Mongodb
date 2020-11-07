package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import ca.utoronto.utm.mcs.API.BlogPostApi;
import ca.utoronto.utm.mcs.API.DaggerBlogPostApiComponent;
import ca.utoronto.utm.mcs.DaggerDaggerComponent;


import com.sun.net.httpserver.HttpServer;

import javax.inject.Inject;

public class App
{
    static int port = 8080;

    public static void main(String[] args) throws IOException
    {
    	Dagger service = DaggerDaggerComponent.create().buildMongoHttp();

    	//Create your server context here
        service.getServer().createContext("/api/v1/post", DaggerBlogPostApiComponent.create().buildAPI());

        service.getServer().start();
    	
    	System.out.printf("Server started on port %d", port);
    }
}
