import java.util.List;

public interface Document {
	public String getName();
	public int getNumberOfPages();
	public List<String> getWordsFromPage(int pageNumber);
}
