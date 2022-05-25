import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class WordIndexImpl implements WordIndex {
	private Map<String, Set<PageEntry>> index = new HashMap<>();
	
	@Override
	public long size() {
		return index.size();
	}
	
	@Override
	public List<PageEntry> getEntries(String word) {
		return index.getOrDefault(word, new TreeSet<>()).stream().collect(Collectors.toList());
	}

	
	public void addEntry(String word, PageEntry entry) {
		Set<PageEntry> set = index.getOrDefault(word, new TreeSet<>());
		set.add(entry);
		index.put(word, set);
	}	
}
