import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;


public class DocumentImpl implements Document {
	private String fileName;
	private Optional<PdfDocument> document = Optional.empty();
	
	public DocumentImpl(File file) {
		this.fileName = file.getName();
		try {
			document = Optional.of(new PdfDocument(new PdfReader(file)));
		} catch (IOException e) {
			// Log error
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return fileName;
	}
	
	public int getNumberOfPages() {
		return document.isEmpty() ? 0 : document.get().getNumberOfPages();
	}
	
	public List<String> getWordsFromPage(int pageNumber) {
		if (document.isEmpty())
			return Collections.emptyList();
		
		PdfPage page = document.get().getPage(pageNumber);
		if (null != page) {
			String text = PdfTextExtractor.getTextFromPage(page);
			return Arrays.asList(text.split("\\P{IsAlphabetic}+"));
		} else {
			// @todo log error document tree broken
			return Collections.emptyList();
		}
	}
}
