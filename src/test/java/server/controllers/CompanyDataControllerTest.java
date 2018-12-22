package server.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import server.company.*;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * @see CompanyDataController
 */
@SuppressWarnings("unused")
@SpringBootTest
@RunWith(SpringRunner.class)
public class CompanyDataControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private CompanyDataController companyDataController;

    private CompanyData testData;

    @MockBean
    private CompanyDataFactory companyDataFactory;

    @Before
    public void setUp() {
        mockMvc = standaloneSetup(this.companyDataController).build();

        CompanyIdentifiers companyIdentifiers = new CompanyIdentifiers("AAPL", "Apple", "0000320193");

        XbrlElements xbrl = new XbrlElements();
        FinancialData financialData = new FinancialData(2017, xbrl);
        ArrayList<FinancialData> financialDataEntries = new ArrayList<>();
        financialDataEntries.add(financialData);

        testData = new CompanyData(companyIdentifiers, financialDataEntries);
    }

    /**
     * @see CompanyDataController#companyData
     */
    @Test
    public void companyData() throws Exception {
        // Mocking the company data factory
        when(companyDataFactory.newCompanyData("AAPL", 1)).thenReturn(this.testData);

        mockMvc.perform(get("/companydata?tickers=AAPL&years=1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].identifiers.ticker", is("AAPL")))
            .andExpect(jsonPath("$[0].identifiers.name", is("Apple")))
            .andExpect(jsonPath("$[0].identifiers.cik", is("0000320193")));
    }
}