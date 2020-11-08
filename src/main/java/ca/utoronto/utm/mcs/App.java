package ca.utoronto.utm.mcs;

import ca.utoronto.utm.mcs.API.DaggerBlogPostApiComponent;


public class App
{
    static int port = 8080;

    public static void main(String[] args)
    {
    	Dagger service = DaggerDaggerComponent.create().buildMongoHttp();

    	//Create your server context here
        service.getServer().createContext("/api/v1/post", DaggerBlogPostApiComponent.create().buildAPI());

        service.getServer().start();
    	
    	System.out.printf("Server started on port %d", port);
    }
}
