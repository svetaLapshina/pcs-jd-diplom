import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.io.IOException;

public class SearchServer {
	public static final int PORT = 8989;
		
	private SearchEngine searchEngine;
	
	private SearchServer() {}
	
	public SearchServer(SearchEngine searchEngine) {
		this.searchEngine = searchEngine;
    }

	public void start() {
		try (ServerSocket serverSocket = new ServerSocket(PORT);) {
			while (true) { 
				try (
						Socket socket = serverSocket.accept();
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	                    PrintWriter out = new PrintWriter(socket.getOutputStream());
	             ) {
					String searchWord = in.readLine().trim();
					List<PageEntry> entries = searchEngine.search(searchWord);
					Type listType = new TypeToken<List<PageEntry>>() {}.getType();
					GsonBuilder builder = new GsonBuilder();
					Gson gson = builder.create();
					String response = gson.toJson(entries, listType);
					out.write(response);
	             }
	          } // while
	      } catch (IOException e) {
	          System.out.println("Can't start search server!");
	          e.printStackTrace();
	      }
	}	
}

