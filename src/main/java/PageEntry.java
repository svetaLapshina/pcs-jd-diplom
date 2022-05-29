import java.util.Comparator;

public class PageEntry implements Comparable<PageEntry> {
    private String pdfName;
    private int page;
    private int count;
    
    public PageEntry(String pdfName, int page, int count) {
    	this.pdfName = pdfName;
    	this.page = page;
    	this.count = count;
    }
    
    public String getPdfName() {
    	return pdfName;
    }
    
    public int getPage() {
    	return page;
    }
    
    public int getCount() {
    	return count;
    }
    
    @Override
	public int compareTo(PageEntry other) {
    	return Comparator.comparing(PageEntry::getCount)
    			.thenComparing(PageEntry::getPage, Comparator.reverseOrder())
    			.thenComparing(PageEntry::getPdfName)
    			.compare(other, this);
    	
		//return other.count - count;
	}
    
    @Override
    public String toString() {
    	return new StringBuilder(pdfName)
    		.append('/')
    		.append(page)
    		.append('/')
    		.append(count).toString();
    }

    public boolean equals(PageEntry other) {
    	return pdfName.equals(other.pdfName) && page == other.page && count == other.count;
    }

    // ???
}
