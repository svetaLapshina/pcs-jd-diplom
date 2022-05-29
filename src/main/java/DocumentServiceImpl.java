import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class DocumentServiceImpl implements DocumentService {

	/*
	@Override
	public List<Document> getDocumentsInDirectory(File pdfsDir) {
		List<Document> docList = new ArrayList<Document>();
		
		for (File file: pdfsDir.listFiles()) {
			if (!file.isDirectory() && file.getName().endsWith(".pdf")) {				
				docList.add(new DocumentImpl(file));
			}
		}		
		
		return docList;
	}*/
	
	@Override
	public Stream<Document> getDocumentsInDirectory(File pdfsDir) {
		return Arrays.stream(pdfsDir.listFiles())
				.filter(x -> { return !x.isDirectory() && x.getName().endsWith(".pdf"); })
				.map(x -> new DocumentImpl(x));
	}
}
