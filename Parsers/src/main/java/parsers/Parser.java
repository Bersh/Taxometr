package parsers;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Parser interface
 *
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 22.10.12
 */
public interface Parser {
    /**
     * Parse years list from select
     *
     * @return years list
     * @throws java.io.IOException is parsing fails
     */
    ArrayList<String> getYears() throws IOException;

    int getLastRequestResultsCount();

    /**
     * Set current page number to 0
     */
    void resetCurrentPage();
}
