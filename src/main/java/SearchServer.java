import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.io.IOException;

public class SearchServer implements java.lang.AutoCloseable {
	public static final int PORT = 8989;
	
	private static final int OK = 200;
	private static final int BAD_REQUEST = 400;
	
	private HttpServer server;
	
	private SearchEngine searchEngine;
	
	private SearchServer() {}
	
	public SearchServer(SearchEngine searchEngine) throws IOException {
		this.searchEngine = searchEngine;
		
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/", this::performSearch);
    }

    public void start() {
        System.out.println("Search server running on http://localhost:" + PORT + "/");
        server.start();
    }
    
    public void close() {
    	server.stop(0);
    	System.out.println("Search server stopped");
    }
    

    Optional<String> getSearchWordFromURL(HttpExchange h) {
    	for (String token: h.getRequestURI().toString().split("/")) {
    		if (token.length() > 0) {
    			String decodedString = URLDecoder.decode(token, StandardCharsets.UTF_8);
    			return Optional.of(decodedString);
    		}
    	}    	
    	return Optional.empty();
    }
    
    
    protected void performSearch(HttpExchange h) {
    	String response;
    	int statusCode = OK;
        GsonBuilder builder = new GsonBuilder();
    	Gson gson = builder.create();
    	
    	Optional<String> searchWord = getSearchWordFromURL(h);
    	if (searchWord.isPresent()) {
    		System.out.println("Searching for " + searchWord.get());
    		List<PageEntry> entries = searchEngine.search(searchWord.get());
        	Type listType = new TypeToken<List<PageEntry>>() {}.getType();
        	response = gson.toJson(entries, listType);
    	} else {
    		response = gson.toJson("Search word not specified");
    		statusCode = BAD_REQUEST;
    	}
    	
    	try {
			h.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
			h.sendResponseHeaders(statusCode, response.getBytes().length);
			h.getResponseBody().write(response.getBytes());
		} catch (IOException e) {
			System.err.println("Exception sending response" + e);
		}
    	
        h.close();
    }
}
