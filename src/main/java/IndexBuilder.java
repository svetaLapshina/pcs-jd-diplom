import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IndexBuilder {
	private DocumentService documentService;
	
	public IndexBuilder(DocumentService documentService) {
		this.documentService = documentService;
	}

	public WordIndex build(File pdfsDir) {
		WordIndexImpl index = new WordIndexImpl();
		documentService.getDocumentsInDirectory(pdfsDir).forEach(document -> indexDocument(document, index));
		return index;
	}

	private void indexDocument(Document document, WordIndexImpl index) {	
		int numberOfPages = document.getNumberOfPages();
		for (int pageNumber = 1; pageNumber <= numberOfPages; pageNumber++) {
			List<String> words = document.getWordsFromPage(pageNumber);				
			Map<String, Integer> frequencies = countWords(words);

			for (Map.Entry<String, Integer> entry: frequencies.entrySet()) {
				PageEntry pageEntry = new PageEntry(document.getName(), pageNumber, entry.getValue());
				index.addEntry(entry.getKey(), pageEntry);
			}
		}
	}
		
	private Map<String, Integer> countWords(List<String> words) {
		Map<String, Integer> frequencies = new HashMap<>();
		for (var word : words) {
		    if (word.isEmpty()) {
		        continue;
		    }
		    frequencies.put(word.toLowerCase(), frequencies.getOrDefault(word.toLowerCase(), 0) + 1);
		}
		
		return frequencies;
	}	
}
