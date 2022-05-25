import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class DocumentServiceImpl implements DocumentService {

	@Override
	public Stream<Document> getDocumentsInDirectory(File pdfsDir) {
		return Arrays.stream(pdfsDir.listFiles())
				.filter(x -> { return !x.isDirectory() && x.getName().endsWith(".pdf"); })
				.map(x -> new DocumentImpl(x));
	}
}
