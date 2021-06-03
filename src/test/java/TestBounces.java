import model.MainModel;
import model.models.GraphOptions;
import model.models.enums.FileType;
import model.services.ServerLogService;
import model.utilities.CSVService;
import model.utilities.GraphDataService;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestBounces {

    public MainModel mainModel = new MainModel();
    public ServerLogService serverLogService = new ServerLogService("src/test/resources/example-dataset/server_log.csv");

    /*
     * NUMBER OF BOUNCES
     */
    @Test
    public void testNumberOfBounces() throws ExecutionException, InterruptedException {
        GraphOptions go = new GraphOptions();
        assertEquals(mainModel.bouncesCount(go), serverLogService.getBounceStream(go).count());
        assertNotEquals(GraphDataService.dailyCount(serverLogService.getBounceStream(go)).size(), GraphDataService.hourlyCount(serverLogService.getBounceStream(go)).size());
        assertNotEquals(GraphDataService.hourlyCount(serverLogService.getBounceStream(go)).size(), GraphDataService.monthlyCount(serverLogService.getBounceStream(go)).size());
        assertNotEquals(GraphDataService.monthlyCount(serverLogService.getBounceStream(go)).size(), GraphDataService.weeklyCount(serverLogService.getBounceStream(go)).size());
        assertNotEquals(GraphDataService.monthlyCount(serverLogService.getBounceStream(go)).size(), GraphDataService.yearlyCount(serverLogService.getBounceStream(go)).size());
    }

    @Test
    public void testBouncesPerDay() throws ExecutionException, InterruptedException {
        GraphOptions go = new GraphOptions();
        List<LocalDateTime> dailyDates = new ArrayList<>(GraphDataService.dailyCount(serverLogService.getBounceStream(go)).keySet());
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.dailyCount(serverLogService.getBounceStream(go)).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(serverLogService.getBounceStream(go).count())));
        assertEquals(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/server_log.csv", FileType.SERVER_LOG).get(1)).substring(1, 11), dailyDates.get(0).toString().substring(0, 10));
        int occurrencesOfFirstDate = 1;

        for (int i = 1; i <= serverLogService.getBounceStream(go).count(); i++) {
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/server_log.csv", FileType.SERVER_LOG).get(i)).substring(1, 11)
                    .equals(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/server_log.csv", FileType.SERVER_LOG).get(i + 1)).substring(1, 11))) {
                occurrencesOfFirstDate += 1;
            } else {
                break;
            }
        }

        double firstOccurrence = occurrences.get(0);
        int firstOccur = (int) (firstOccurrence);
        assertEquals(Integer.parseInt(String.valueOf(occurrencesOfFirstDate)), firstOccur);
        assertEquals(GraphDataService.dailyCount(serverLogService.getBounceStream(go)).values().size(), GraphDataService.dailyCount(serverLogService.getBounceStream(go)).keySet().size());
        assertEquals(GraphDataService.dailyCount(serverLogService.getBounceStream(go)).size(), GraphDataService.dailyCount(serverLogService.getBounceStream(go)).size());
    }

    @Test
    public void testBouncesPerHour() throws ExecutionException, InterruptedException {
        GraphOptions go = new GraphOptions();
        List<LocalDateTime> hourlyDates = new ArrayList<>(GraphDataService.hourlyCount(serverLogService.getBounceStream(go)).keySet());
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.hourlyCount(serverLogService.getBounceStream(go)).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(serverLogService.getBounceStream(go).count())));
        assertEquals(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/server_log.csv", FileType.SERVER_LOG).get(1)).substring(1, 11), hourlyDates.get(0).toString().substring(0, 10));
        int occurrencesOfFirstDate = 1;

        for (int i = 1; i <= serverLogService.getBounceStream(go).count(); i++) {
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/server_log.csv", FileType.SERVER_LOG).get(i)).substring(1, 11)
                    .equals(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/server_log.csv", FileType.SERVER_LOG).get(i + 1)).substring(1, 11))) {
                occurrencesOfFirstDate += 1;
            } else {
                break;
            }
        }

        double firstOccurrence = occurrences.get(0);
        int firstOccur = (int) (firstOccurrence);
        assertEquals(Integer.parseInt(String.valueOf(occurrencesOfFirstDate)), firstOccur);
        assertEquals(GraphDataService.hourlyCount(serverLogService.getBounceStream(go)).values().size(), GraphDataService.hourlyCount(serverLogService.getBounceStream(go)).keySet().size());
        assertEquals(GraphDataService.hourlyCount(serverLogService.getBounceStream(go)).size(), GraphDataService.hourlyCount(serverLogService.getBounceStream(go)).size());
    }

    @Test
    public void testBouncesPerWeeks() throws ExecutionException, InterruptedException {
        GraphOptions go = new GraphOptions();
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.weeklyCount(serverLogService.getBounceStream(go)).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(serverLogService.getBounceStream(go).count())));
        assertEquals(GraphDataService.weeklyCount(serverLogService.getBounceStream(go)).values().size(), GraphDataService.weeklyCount(serverLogService.getBounceStream(go)).keySet().size());
        assertEquals(GraphDataService.weeklyCount(serverLogService.getBounceStream(go)).size(), GraphDataService.weeklyCount(serverLogService.getBounceStream(go)).size());
    }

    @Test
    public void testBouncesPerMonths() throws ExecutionException, InterruptedException {
        GraphOptions go = new GraphOptions();
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.monthlyCount(serverLogService.getBounceStream(go)).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(serverLogService.getBounceStream(go).count())));
        assertEquals(GraphDataService.monthlyCount(serverLogService.getBounceStream(go)).values().size(), GraphDataService.monthlyCount(serverLogService.getBounceStream(go)).keySet().size());
        assertEquals(GraphDataService.monthlyCount(serverLogService.getBounceStream(go)).size(), GraphDataService.monthlyCount(serverLogService.getBounceStream(go)).size());
    }

    @Test
    public void testBouncesPerYears() throws ExecutionException, InterruptedException {
        GraphOptions go = new GraphOptions();
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.yearlyCount(serverLogService.getBounceStream(go)).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(serverLogService.getBounceStream(go).count())));
        assertEquals(GraphDataService.yearlyCount(serverLogService.getBounceStream(go)).values().size(), GraphDataService.yearlyCount(serverLogService.getBounceStream(go)).keySet().size());
        assertEquals(GraphDataService.yearlyCount(serverLogService.getBounceStream(go)).size(), GraphDataService.yearlyCount(serverLogService.getBounceStream(go)).size());
    }
}
