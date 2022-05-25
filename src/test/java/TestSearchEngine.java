import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;

public class TestSearchEngine {

	@Test
	public void test_word_index_sorted_by_word_count() {
		WordIndexImpl index = new WordIndexImpl();
		
		PageEntry pe1 = new PageEntry("doc1.pdf", 1, 5);
		PageEntry pe2 = new PageEntry("doc2.pdf", 8, 10);
		PageEntry pe3 = new PageEntry("doc3.pdf", 2, 3);
		
		index.addEntry("cat", pe1);
		index.addEntry("cat", pe2);
		index.addEntry("cat", pe3);
		
		List<PageEntry> entries = index.getEntries("cat");
		assertEquals(3, entries.size());
		
		assertEquals(pe2, entries.get(0));
		assertEquals(pe1, entries.get(1));
		assertEquals(pe3, entries.get(2));		
	}
	
	@Test
	public void test_word_index_empty_list_for_non_existent_word() {
		WordIndexImpl index = new WordIndexImpl();
		index.addEntry("cat", new PageEntry("doc1.pdf", 1, 5));
		
		List<PageEntry> entries = index.getEntries("dog");
		assertTrue(entries.isEmpty());
	}
	
	@Test
	public void test_word_index_built_correctly() {
		Document documentMock1 = mock(Document.class);
		when(documentMock1.getName()).thenReturn("a.pdf");
		when(documentMock1.getNumberOfPages()).thenReturn(2);
		when(documentMock1.getWordsFromPage(1)).thenReturn(Arrays.asList("apple", "bananas", "Apple", "pear", "Orange"));
		when(documentMock1.getWordsFromPage(2)).thenReturn(Arrays.asList("Apple", "Orange", "pear", "orange", "orange", "pear"));

		Document documentMock2 = mock(Document.class);
		when(documentMock2.getName()).thenReturn("b.pdf");
		when(documentMock2.getNumberOfPages()).thenReturn(1);
		when(documentMock2.getWordsFromPage(1)).thenReturn(Arrays.asList("orange", "LeMoNs"));

		Document[] documents = { documentMock1, documentMock2 };

		DocumentService documentServiceMock = mock(DocumentService.class);		
		when(documentServiceMock.getDocumentsInDirectory(any())).thenReturn(Arrays.stream(documents));
		
		IndexBuilder builder = new IndexBuilder(documentServiceMock);
		WordIndex index = builder.build(new File(""));
		
		assertEquals(5, index.size());

		List<PageEntry> entries = index.getEntries("apple");
		assertEquals(2, entries.size());
		assertTrue(entries.get(0).equals(new PageEntry("a.pdf", 1, 2)));
		assertTrue(entries.get(1).equals(new PageEntry("a.pdf", 2, 1)));

		entries = index.getEntries("bananas");
		assertEquals(1, entries.size());
		assertTrue(entries.get(0).equals(new PageEntry("a.pdf", 1, 1)));
		
		entries = index.getEntries("pear");
		assertEquals(2, entries.size());
		assertTrue(entries.get(0).equals(new PageEntry("a.pdf", 2, 2)));
		assertTrue(entries.get(1).equals(new PageEntry("a.pdf", 1, 1)));

		entries = index.getEntries("orange");
		assertEquals(3, entries.size());
		assertTrue(entries.get(0).equals(new PageEntry("a.pdf", 2, 3)));
		assertTrue(entries.get(1).equals(new PageEntry("b.pdf", 1, 1)));
		assertTrue(entries.get(2).equals(new PageEntry("a.pdf", 1, 1)));		
	}
	
	private SearchServer setupSearchServer() throws IOException {
		List<PageEntry> testResult = Arrays.asList(new PageEntry("a.pdf", 10, 5));
		
		WordIndex wordIndexMock = mock(WordIndex.class);
		when(wordIndexMock.getEntries("needle")).thenReturn(testResult);
		
    	SearchEngine searchEngine = new BooleanSearchEngine(wordIndexMock);
    	return new SearchServer(searchEngine);
	}
	
	private HttpResponse<String> sendHttpRequest(String word) throws IOException, InterruptedException {
    	var client = HttpClient.newHttpClient();
    	
    	StringBuilder sb = new StringBuilder("http://localhost:");
    	sb.append(SearchServer.PORT);
    	if (word.length() > 0)
    		sb.append('/').append(word);
    	
    	var request = HttpRequest.newBuilder(URI.create(sb.toString())).build();
    	return client.send(request, BodyHandlers.ofString());
	}
	
	@Test
	public void server_returns_existing_word_correctly() {
		try (
			SearchServer server = setupSearchServer();
		) {
			server.start();
			
			HttpResponse<String> response = sendHttpRequest("needle");
			assertEquals(200, response.statusCode());
			assertEquals("[{\"pdfName\":\"a.pdf\",\"page\":10,\"count\":5}]", response.body());
		} catch (IOException e) {
			fail("Exception caught", e);
		} catch (InterruptedException e) {
			fail("Interrupted", e);
		}
	}
	
	@Test
	public void server_returns_empty_response_when_searching_non_existing_word() {
		try (
			SearchServer server = setupSearchServer();
		) {
			server.start();
		
			HttpResponse<String> response = sendHttpRequest("cat");
			assertEquals(200, response.statusCode());
			assertEquals("[]", response.body());
			
		} catch (IOException e) {
			fail("Exception caught", e);
		} catch (InterruptedException e) {
			fail("Interrupted", e);
		}
	}
	
	@Test
	public void server_handles_missing_word_in_request_correctly() {
		try (
			SearchServer server = setupSearchServer();
		) {
			server.start();
			HttpResponse<String> response = sendHttpRequest("");
			assertEquals(400, response.statusCode());
			assertEquals("\"Search word not specified\"", response.body());
		} catch (IOException e) {
			fail("Exception caught", e);
		} catch (InterruptedException e) {
			fail("Interrupted", e);
		}
	}
}
