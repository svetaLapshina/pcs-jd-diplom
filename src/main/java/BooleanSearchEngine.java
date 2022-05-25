import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    
	private WordIndex wordIndex;
	
    public BooleanSearchEngine(WordIndex wordIndex) throws IOException {
    	this.wordIndex = wordIndex;
    }

    @Override
    public List<PageEntry> search(String word) {
    	return wordIndex.getEntries(word);
    }
    
}
