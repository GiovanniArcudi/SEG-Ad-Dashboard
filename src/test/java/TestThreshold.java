import model.models.GraphOptions;
import model.models.enums.BounceDefinition;
import model.models.enums.FileType;
import model.services.ServerLogService;
import model.utilities.CSVService;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestThreshold {

    public ServerLogService serverLogService = new ServerLogService("src/test/resources/example-dataset/server_log.csv");

    @Test
    public void testBouncesThreshold() {
        GraphOptions graphOptions = new GraphOptions();
        graphOptions.setBounceDefinition(BounceDefinition.THRESHOLD);
        graphOptions.setBounceThreshold(-1.0);

        // Test for bounce threshold = -1 (only n/a exit date)
        ArrayList<String> numberOfBounces = new ArrayList<>();
        for (int i = 1; i <= serverLogService.getCount(); i++) {
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/server_log.csv", FileType.SERVER_LOG).get(i)).contains("n/a")) {
                numberOfBounces.add(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/server_log.csv", FileType.SERVER_LOG).get(i)));
            }
        }

        assertEquals(numberOfBounces.size(), serverLogService.getBounceStream(graphOptions).count());
        graphOptions.setBounceDefinition(BounceDefinition.SINGLE_PAGE);
        graphOptions.setBounceThreshold(20.0);
        assertEquals(21, serverLogService.getBounceStream(graphOptions).count());
        graphOptions.setBounceThreshold(55.0);
        assertEquals(21, serverLogService.getBounceStream(graphOptions).count());
        graphOptions.setBounceDefinition(BounceDefinition.THRESHOLD);

        // Manual Testing for threshold >= 0
        graphOptions.setBounceThreshold(0.0);
        assertEquals(8, serverLogService.getBounceStream(graphOptions).count());
        graphOptions.setBounceThreshold(30.0);
        assertEquals(24, serverLogService.getBounceStream(graphOptions).count());
        graphOptions.setBounceThreshold(100.0);
        assertEquals(31, serverLogService.getBounceStream(graphOptions).count());
        graphOptions.setBounceThreshold(100000.0);
        assertEquals(50, serverLogService.getBounceStream(graphOptions).count());
    }
}
