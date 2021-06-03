import model.MainModel;
import model.models.enums.FileType;
import model.services.ServerLogService;
import model.utilities.CSVService;
import model.utilities.GraphDataService;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestConversions {
    public MainModel mainModel = new MainModel();
    public ServerLogService serverLogService = new ServerLogService("src/test/resources/example-dataset/server_log.csv");

    /*
     * NUMBER OF CONVERSIONS
     */
    @Test
    public void testNumberOfConversions() throws ExecutionException, InterruptedException {

        assertEquals(mainModel.conversionsCount(), serverLogService.getConversions());
        assertEquals(GraphDataService.dailyCount(serverLogService.getConversionStream()).size(), GraphDataService.hourlyCount(serverLogService.getConversionStream()).size());
        assertNotEquals(GraphDataService.hourlyCount(serverLogService.getConversionStream()).size(), GraphDataService.monthlyCount(serverLogService.getConversionStream()).size());
        assertNotEquals(GraphDataService.monthlyCount(serverLogService.getConversionStream()).size(), GraphDataService.weeklyCount(serverLogService.getConversionStream()).size());
        assertEquals(GraphDataService.monthlyCount(serverLogService.getConversionStream()).size(), GraphDataService.yearlyCount(serverLogService.getConversionStream()).size());
        ArrayList<String> numOfConv = new ArrayList<>();

        for (int i = 1; i <= serverLogService.getConversions(); i++) {
            CSVService.parse("src/test/resources/example-dataset/server_log.csv", FileType.SERVER_LOG);
            numOfConv.add(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/server_log.csv", FileType.SERVER_LOG).get(i)));
        }

        assertEquals(numOfConv.size(), serverLogService.getConversions());
    }

    @Test
    public void testConversionsPerDays() throws ExecutionException, InterruptedException {

        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.dailyCount(serverLogService.getConversionStream()).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(serverLogService.getConversionStream().count())));
        assertEquals(GraphDataService.dailyCount(serverLogService.getConversionStream()).values().size(), GraphDataService.dailyCount(serverLogService.getConversionStream()).keySet().size());
        assertEquals(GraphDataService.dailyCount(serverLogService.getConversionStream()).size(), GraphDataService.dailyCount(serverLogService.getConversionStream()).size());
    }

    @Test
    public void testConversionsPerHours() throws ExecutionException, InterruptedException {

        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.hourlyCount(serverLogService.getConversionStream()).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(serverLogService.getConversionStream().count())));
        assertEquals(GraphDataService.hourlyCount(serverLogService.getConversionStream()).values().size(), GraphDataService.hourlyCount(serverLogService.getConversionStream()).keySet().size());
        assertEquals(GraphDataService.hourlyCount(serverLogService.getConversionStream()).size(), GraphDataService.hourlyCount(serverLogService.getConversionStream()).size());
    }

    @Test
    public void testConversionsPerWeeks() throws ExecutionException, InterruptedException {
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.weeklyCount(serverLogService.getConversionStream()).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(serverLogService.getConversionStream().count())));
        assertEquals(GraphDataService.weeklyCount(serverLogService.getConversionStream()).values().size(), GraphDataService.weeklyCount(serverLogService.getConversionStream()).keySet().size());
        assertEquals(GraphDataService.weeklyCount(serverLogService.getConversionStream()).size(), GraphDataService.weeklyCount(serverLogService.getConversionStream()).size());
    }

    @Test
    public void testConversionsPerMonths() throws ExecutionException, InterruptedException {
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.monthlyCount(serverLogService.getConversionStream()).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(serverLogService.getConversionStream().count())));
        assertEquals(GraphDataService.monthlyCount(serverLogService.getConversionStream()).values().size(), GraphDataService.monthlyCount(serverLogService.getConversionStream()).keySet().size());
        assertEquals(GraphDataService.monthlyCount(serverLogService.getConversionStream()).size(), GraphDataService.monthlyCount(serverLogService.getConversionStream()).size());
    }

    @Test
    public void testConversionsPerYears() throws ExecutionException, InterruptedException {
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.yearlyCount(serverLogService.getConversionStream()).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(serverLogService.getConversionStream().count())));
        assertEquals(GraphDataService.yearlyCount(serverLogService.getConversionStream()).values().size(), GraphDataService.yearlyCount(serverLogService.getConversionStream()).keySet().size());
        assertEquals(GraphDataService.yearlyCount(serverLogService.getConversionStream()).size(), GraphDataService.yearlyCount(serverLogService.getConversionStream()).size());
    }
}
