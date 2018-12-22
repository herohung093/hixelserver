package server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import server.config.SearchProperties;
import server.search.SearchData;
import server.search.SearchDataIngestionService;
import server.search.SearchDataRepository;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * @see SearchController
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SearchControllerTest {

    private MockMvc mockMvc;
    private ObjectWriter objectWriter;

    @SuppressWarnings("unused")
    @Autowired
    private SearchController searchController;

    private static final ArrayList<SearchData> testData = new ArrayList<SearchData>(){{
        add(new SearchData("AAPL", "Apple", "NASDAQ"));
        add(new SearchData("AMRN", "Amarin", "NASDAQ"));
        add(new SearchData("AMZN", "Amazon.com", "NASDAQ"));
        add(new SearchData("ALXN", "Alexian Pharmaceuticals", "NASDAQ"));
        add(new SearchData("AMAT", "Applied Materials", "NASDAQ"));
        add(new SearchData("GOOG", "Alphabet", "NASDAQ"));
        add(new SearchData("TSLA", "Tesla", "NASDAQ"));
        add(new SearchData("BAC", "Bank of America", "NYSE"));
        add(new SearchData("WFC", "Wells Fargo", "NYSE"));
        add(new SearchData("KO", "Coca Cola", "NYSE"));
    }};

    private static final ArrayList<SearchData> expectedResult = new ArrayList<SearchData>(){{
        add(new SearchData("AAPL", "Apple", "NASDAQ"));
        add(new SearchData("AMRN", "Amarin", "NASDAQ"));
        add(new SearchData("AMZN", "Amazon.com", "NASDAQ"));
        add(new SearchData("ALXN", "Alexian Pharmaceuticals", "NASDAQ"));
        add(new SearchData("AMAT", "Applied Materials", "NASDAQ"));
    }};

    @SuppressWarnings("unused")
    @MockBean
    private SearchProperties searchProperties;

    @SuppressWarnings("unused")
    @MockBean
    private SearchDataIngestionService searchDataIngestionService;

    @Before
    public void setUp() {
        mockMvc = standaloneSetup(searchController).build();
        objectWriter = new ObjectMapper().writer();
    }


    /**
     * Black-box test that ensures that the controller returns the expected results, limited to the configured number.
     * @see SearchController#searchQuery
     * @see SearchProperties#getResponseSize
     */
    @Test
    public void searchQuery() throws Exception {
        // Mocking the search properties config
        when(searchProperties.getResponseSize()).thenReturn(5);
        when(searchDataIngestionService.ingestData()).thenReturn(new SearchDataRepository(testData));
        searchController.fixedIntervalIngestion();

        mockMvc.perform(get("/search?query=a").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(expectedResult)));
    }

    /**
     * Tests that the method will not throw an exception when ingestion fails
     */
    @Test
    public void fixedIntervalIngestionExceptionalCase() throws Exception{
        when(searchDataIngestionService.ingestData()).thenThrow(searchDataIngestionService.new IngestionFailureException(""));
        searchController.fixedIntervalIngestion();
    }
}