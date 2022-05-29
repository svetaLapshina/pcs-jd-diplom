import java.io.File;
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
			Map<String, Integer> freqs = countWords(words);

			for (Map.Entry<String, Integer> entry: freqs.entrySet()) {
				PageEntry pageEntry = new PageEntry(document.getName(), pageNumber, entry.getValue());
				index.addEntry(entry.getKey(), pageEntry);
			}
		}
	}
		
	private Map<String, Integer> countWords(List<String> words) {
		Map<String, Integer> freqs = new HashMap<>();
		for (var word : words) {
		    if (word.isEmpty()) {
		        continue;
		    }
		    freqs.put(word.toLowerCase(), freqs.getOrDefault(word.toLowerCase(), 0) + 1);
		}
		
		return freqs;
	}	
}
