import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SearchServer {
	public static final int PORT = 8989;
	
	private static final int OK = 200;
	private static final int BAD_REQUEST = 400;
	
	private AtomicBoolean running = new AtomicBoolean(false);
	
	private SearchEngine searchEngine;
	
	private SearchServer() {}
	
	public SearchServer(SearchEngine searchEngine) {
		this.searchEngine = searchEngine;
    }
	
	public void stop() {
		running.set(false);
	}
	
	private Optional<String> getSearchWord(BufferedReader reader) throws IOException {
		String s;
        while ((s = reader.readLine()) != null) {
        	if (s.isEmpty()) break;
        	
            if (s.startsWith("GET")) {
            	String[] tokens = s.split("\\s+");
            	if (tokens.length > 1 && tokens[1].length() > 1) {
            		int pos = tokens[1].indexOf('/', 1);
            		String searchWord = (pos > 0) ? tokens[1].substring(1, pos) : tokens[1].substring(1);
            		String decoded = URLDecoder.decode(searchWord, StandardCharsets.UTF_8);
            		return Optional.of(decoded);
            	}
            }
        }

        return Optional.empty();
    }

	private void sendResponse(BufferedWriter out, int statusCode, String response) throws IOException {
		if (OK == statusCode)
			out.write("HTTP/1.0 200 OK\r\n");
		else
			out.write("HTTP/1.0 400 Bad Request\r\n");
		
		out.write("Content-Type: application/json\r\n");
		out.write("Content-Length: " + response.getBytes().length + "\r\n");
		out.write("\r\n");
		out.write(response);
	}

	public void start() {
		running.set(true);
		run();
	}
	
	private void run() {
		try (
			ServerSocket serverSocket = new ServerSocket(PORT);
		) {	
			while (running.get()) {
				Socket clientSocket = serverSocket.accept();

				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				Optional<String> searchWord = getSearchWord(in);
	        
				String response;
				int statusCode = OK;
				GsonBuilder builder = new GsonBuilder();
				Gson gson = builder.create();
				
				if (searchWord.isPresent()) {
					System.out.println("Searching for: " + searchWord.get());
					List<PageEntry> entries = searchEngine.search(searchWord.get());
					Type listType = new TypeToken<List<PageEntry>>() {}.getType();
					response = gson.toJson(entries, listType);
				} else {
					response = gson.toJson("Search word not specified");
					statusCode = BAD_REQUEST;
				}
	    	    	
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
				sendResponse(out, statusCode, response);
	        
				out.close();
				in.close();
				clientSocket.close();
			}
		} catch (IOException ex) {
			System.err.println("Exception caught: " + ex.getMessage());
		}
	}
}
