import model.MainModel;
import model.models.enums.FileType;
import model.services.ClickService;
import model.services.ImpressionService;
import model.utilities.CSVService;
import org.junit.Test;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestCostPerClicks {
    public MainModel mainModel = new MainModel();
    public ImpressionService impressionService = new ImpressionService("src/test/resources/example-dataset/impression_log.csv");
    public ClickService clickService = new ClickService("src/test/resources/example-dataset/click_log.csv");

    /*
     * COST PER CLICKS
     * No need to test for daily,monthly,weekly,hourly,yearly streams since they are tested above for all 3 services (click,impression and server)
     */
    @Test
    public void testCostPerClicks() {
        assertEquals(String.valueOf((clickService.getCost() + impressionService.getCost()) / clickService.getCount()).substring(0, 6), String.valueOf(mainModel.CPC()).substring(0, 6));
        double clickCost = 0;
        double impressionCost = 0;

        for (int i = 1; i <= clickService.getCount(); i++) {
            String str = Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i));
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i)).substring(str.length() - 10, str.length() - 1).startsWith(",")) {
                clickCost += Double.parseDouble(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i)).substring(str.length() - 9, str.length() - 1));
            } else {
                clickCost += Double.parseDouble(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i)).substring(str.length() - 10, str.length() - 1));
            }
        }

        for (int i = 1; i <= impressionService.getCount(); i++) {
            String str = Arrays.toString(CSVService.parse("src/test/resources/example-dataset/impression_log.csv", FileType.IMPRESSION_LOG).get(i));
            impressionCost += Double.parseDouble(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/impression_log.csv", FileType.IMPRESSION_LOG).get(i)).substring(str.length() - 9, str.length() - 1));
        }
        assertEquals(String.valueOf((clickCost + impressionCost) / clickService.getCount()).substring(0, 3), String.valueOf(mainModel.CPC()).substring(0, 3));
    }
}
