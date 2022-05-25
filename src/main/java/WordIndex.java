import java.util.List;

public interface WordIndex {
	public long size();
	public List<PageEntry> getEntries(String word);
}
