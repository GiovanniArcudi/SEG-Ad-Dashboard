import model.MainModel;
import model.models.GraphOptions;
import model.models.enums.FileType;
import model.services.ClickService;
import model.services.ImpressionService;
import model.utilities.CSVService;
import org.junit.Test;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestCostThroughRate {
    public MainModel mainModel = new MainModel();
    public ImpressionService impressionService = new ImpressionService("src/test/resources/example-dataset/impression_log.csv");
    public ClickService clickService = new ClickService("src/test/resources/example-dataset/click_log.csv");

    /*
     * COST THROUGH RATE
     */
    @Test
    public void testCostThroughRate() {
        assertEquals(((double) clickService.getCount() / impressionService.getCount()), mainModel.CTR(), 0.0);

        // Check for 2015-01-04 if the correct output is displayed
        double clickCost = 0;
        double impressionCost = 0;

        for (int i = 1; i <= clickService.getCount(); i++) {
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i)).contains("2015-01-04")) {
                clickCost += 1.0;
            }
        }

        for (int i = 1; i <= impressionService.getCount(); i++) {
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/impression_log.csv", FileType.IMPRESSION_LOG).get(i)).contains("2015-01-04")) {
                impressionCost += 1.0;
            }
        }

        GraphOptions graphOptions1 = new GraphOptions();
        LocalDate localDate = LocalDate.of(2015, 1, 4);
        graphOptions1.setStart(localDate);
        graphOptions1.setEnd(localDate);
        assertEquals(clickCost / impressionCost, mainModel.CTR(graphOptions1), 0.0);
    }
}
