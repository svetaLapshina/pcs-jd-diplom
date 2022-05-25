import java.io.File;
import java.io.IOException;

public class Main {
	
    public static void main(String[] args) {
    	DocumentService documentService = new DocumentServiceImpl();
    	WordIndex wordIndex = new IndexBuilder(documentService).build(new File("pdfs"));    	
    	
    	SearchServer server;
    	try {
    		server = new SearchServer(new BooleanSearchEngine(wordIndex));
    	} catch (IOException e) {
    		System.err.println("Failed to create a search server" + e);
    		return;
    	}
    	
    	server.start();
    }
}
