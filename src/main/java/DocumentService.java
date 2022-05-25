import java.io.File;
import java.util.stream.Stream;

public interface DocumentService {
	public Stream<Document> getDocumentsInDirectory(File path);
}
