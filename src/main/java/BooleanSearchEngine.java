import java.util.List;

public class BooleanSearchEngine implements SearchEngine {
    
	private WordIndex wordIndex;
	
    public BooleanSearchEngine(WordIndex wordIndex) {
    	this.wordIndex = wordIndex;
    }

    @Override
    public List<PageEntry> search(String word) {
    	return wordIndex.getEntries(word.toLowerCase());
    }
    
}
