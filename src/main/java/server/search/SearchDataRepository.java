package server.search;

import java.util.ArrayList;

/**
 * A wrapper around a list of <code>SearchData</code> entries
 * The only public functionality of this object is to allow the search data to be queried with a search query
 */
public class SearchDataRepository {
    /**
     * A list of search data entries
     */
    private final ArrayList<SearchData> ingested;

    /**
     * Creates a <code>SearchDataRepository</code> object
     * @param ingested A list of search data entries which will populate this repository
     */
    public SearchDataRepository(ArrayList<SearchData> ingested) {
        this.ingested = ingested;
    }

    /**
     * Takes a query and returns a list of search data entries which match it
     * @param query The search query to be matched with search data entries
     * @param limit Maximum number of matches to return
     * @return Returns a list of search data entries which matched the query
     * @see SearchData#matches
     */
    public ArrayList<SearchData> query(String query, int limit) {
        ArrayList<SearchData> results = new ArrayList<>();

        for (SearchData entry : ingested) {
            if (entry.matches(query)) {
                results.add(entry);

                if (results.size() >= limit)
                    break;
            }
        }

        return results;
    }
}
