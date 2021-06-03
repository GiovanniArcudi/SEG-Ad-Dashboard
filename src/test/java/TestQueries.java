import model.MainModel;
import model.services.ClickService;
import model.services.ImpressionService;
import model.services.ServerLogService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestQueries {
    public MainModel mainModel = new MainModel();
    public ImpressionService impressionService = new ImpressionService("src/test/resources/example-dataset/impression_log.csv");
    public ClickService clickService = new ClickService("src/test/resources/example-dataset/click_log.csv");
    public ServerLogService serverLogService = new ServerLogService("src/test/resources/example-dataset/server_log.csv");

    //  The tests are based entirely on the example-dataset found within the resources
    @Test
    public void testNumberOfMetrics() {
        // Manual test of Number Of Impressions
        assertEquals(143, impressionService.getCount());

        // Manual test of Number of Clicks
        assertEquals(50, clickService.getCount());

        // Manual test of Number of Services
        assertEquals(50, serverLogService.getCount());
    }
}