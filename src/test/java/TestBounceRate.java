import model.MainModel;
import model.models.GraphOptions;
import model.services.ClickService;
import model.services.ServerLogService;
import model.utilities.GraphDataService;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class TestBounceRate {
    public MainModel mainModel = new MainModel();
    public ClickService clickService = new ClickService("src/test/resources/example-dataset/click_log.csv");
    public ServerLogService serverLogService = new ServerLogService("src/test/resources/example-dataset/server_log.csv");

    /*
     * BOUNCE RATE
     */

    @Test
    public void testBounceRate() {
        GraphOptions go = new GraphOptions();
        assertEquals(serverLogService.getBounceStream(go).count() / (double) clickService.getCount(), mainModel.bounceRate(go), 0.1);
    }

    @Test
    public void testBounceRatePerDay() throws ExecutionException, InterruptedException {
        GraphOptions go = new GraphOptions();
        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.dailyCount(clickService.getStream()).values().toArray()));
        ArrayList<Double> bounceOccurrences = new ArrayList(Arrays.asList(GraphDataService.dailyCount(serverLogService.getBounceStream(go)).values().toArray()));
        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }

        double plus1 = 0.0;

        for (int i = 0; i <= bounceOccurrences.size() - 1; i++) {
            plus1 = plus1 + bounceOccurrences.get(i);
        }

        assertEquals((plus1 / plus), mainModel.bounceRate(go), 0.2);
    }

    @Test
    public void testBounceRatePerHour() throws ExecutionException, InterruptedException {
        GraphOptions go = new GraphOptions();
        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.hourlyCount(clickService.getStream()).values().toArray()));
        ArrayList<Double> bounceOccurrences = new ArrayList(Arrays.asList(GraphDataService.hourlyCount(serverLogService.getBounceStream(go)).values().toArray()));
        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }

        double plus1 = 0.0;

        for (int i = 0; i <= bounceOccurrences.size() - 1; i++) {
            plus1 = plus1 + bounceOccurrences.get(i);
        }

        assertEquals((plus1 / plus), mainModel.bounceRate(go), 0.2);
    }

    @Test
    public void testBounceRatePerWeek() throws ExecutionException, InterruptedException {
        GraphOptions go = new GraphOptions();
        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.weeklyCount(clickService.getStream()).values().toArray()));
        ArrayList<Double> bounceOccurrences = new ArrayList(Arrays.asList(GraphDataService.weeklyCount(serverLogService.getBounceStream(go)).values().toArray()));
        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }

        double plus1 = 0.0;

        for (int i = 0; i <= bounceOccurrences.size() - 1; i++) {
            plus1 = plus1 + bounceOccurrences.get(i);
        }

        assertEquals((plus1 / plus), mainModel.bounceRate(go), 0.2);
    }

    @Test
    public void testBounceRatePerMonth() throws ExecutionException, InterruptedException {
        GraphOptions go = new GraphOptions();
        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.monthlyCount(clickService.getStream()).values().toArray()));
        ArrayList<Double> bounceOccurrences = new ArrayList(Arrays.asList(GraphDataService.monthlyCount(serverLogService.getBounceStream(go)).values().toArray()));
        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }

        double plus1 = 0.0;

        for (int i = 0; i <= bounceOccurrences.size() - 1; i++) {
            plus1 = plus1 + bounceOccurrences.get(i);
        }

        assertEquals((plus1 / plus), mainModel.bounceRate(go), 0.2);
    }

    @Test
    public void testBounceRatePerYear() throws ExecutionException, InterruptedException {
        GraphOptions go = new GraphOptions();
        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.yearlyCount(clickService.getStream()).values().toArray()));
        ArrayList<Double> bounceOccurrences = new ArrayList(Arrays.asList(GraphDataService.yearlyCount(serverLogService.getBounceStream(go)).values().toArray()));
        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }

        double plus1 = 0.0;

        for (int i = 0; i <= bounceOccurrences.size() - 1; i++) {
            plus1 = plus1 + bounceOccurrences.get(i);
        }

        assertEquals((plus1 / plus), mainModel.bounceRate(go), 0.2);
    }
}
