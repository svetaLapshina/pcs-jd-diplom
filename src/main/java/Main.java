import java.io.File;

public class Main {
	
    public static void main(String[] args) {
    	DocumentService documentService = new DocumentServiceImpl();
    	WordIndex wordIndex = new IndexBuilder(documentService).build(new File("pdfs"));    	
    	
    	SearchServer server = new SearchServer(new BooleanSearchEngine(wordIndex));
    	server.start();    	
    }	
}
