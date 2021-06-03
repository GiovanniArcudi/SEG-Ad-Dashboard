import model.MainModel;
import model.models.enums.FileType;
import model.services.ClickService;
import model.services.ImpressionService;
import model.services.ServerLogService;
import model.utilities.CSVService;
import model.utilities.GraphDataService;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class TestCostPerAcquisition {

    public MainModel mainModel = new MainModel();
    public ImpressionService impressionService = new ImpressionService("src/test/resources/example-dataset/impression_log.csv");
    public ClickService clickService = new ClickService("src/test/resources/example-dataset/click_log.csv");
    public ServerLogService serverLogService = new ServerLogService("src/test/resources/example-dataset/server_log.csv");

    /*
     * COST PER ACQUISITION
     */
    @Test
    public void testCostPerAcquisition() {
        assertEquals(String.valueOf((clickService.getCost() + impressionService.getCost()) / serverLogService.getConversions()).substring(0, 6), String.valueOf(mainModel.CPA()).substring(0, 6));
        double clickCost = 0;
        double impressionCost = 0;
        double conversionCost = 0;

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

        for (int i = 1; i <= serverLogService.getCount(); i++) {
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/server_log.csv", FileType.SERVER_LOG).get(i)).contains("Yes")) {
                conversionCost += 1.0;
            }
        }
        assertEquals(String.valueOf((clickCost + impressionCost) / conversionCost).substring(0, 3), String.valueOf(mainModel.CPA()).substring(0, 3));
    }

    @Test
    public void testCostPerAcquisitionPerDay() throws ExecutionException, InterruptedException {

        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.dailyCost(clickService.getStream()).values().toArray()));
        ArrayList<Double> impressionOccurrences = new ArrayList(Arrays.asList(GraphDataService.dailyCost(impressionService.getStream()).values().toArray()));
        ArrayList<Double> conversionOccurrences = new ArrayList(Arrays.asList(GraphDataService.dailyCount(serverLogService.getConversionStream()).values().toArray()));
        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }
        double plus1 = 0.0;

        for (int i = 0; i <= impressionOccurrences.size() - 1; i++) {
            plus1 = plus1 + impressionOccurrences.get(i);
        }
        double plus2 = 0.0;

        for (int i = 0; i <= conversionOccurrences.size() - 1; i++) {
            plus2 = plus2 + conversionOccurrences.get(i);
        }
        assertEquals(String.valueOf((plus + plus1) / plus2).substring(0, 6), String.valueOf(mainModel.CPA()).substring(0, 6));
    }

    @Test
    public void testCostPerAcquisitionPerWeek() throws ExecutionException, InterruptedException {

        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.weeklyCost(clickService.getStream()).values().toArray()));
        ArrayList<Double> impressionOccurrences = new ArrayList(Arrays.asList(GraphDataService.weeklyCost(impressionService.getStream()).values().toArray()));
        ArrayList<Double> conversionOccurrences = new ArrayList(Arrays.asList(GraphDataService.weeklyCount(serverLogService.getConversionStream()).values().toArray()));
        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }
        double plus1 = 0.0;

        for (int i = 0; i <= impressionOccurrences.size() - 1; i++) {
            plus1 = plus1 + impressionOccurrences.get(i);
        }
        double plus2 = 0.0;

        for (int i = 0; i <= conversionOccurrences.size() - 1; i++) {
            plus2 = plus2 + conversionOccurrences.get(i);
        }
        assertEquals(String.valueOf((plus + plus1) / plus2).substring(0, 6), String.valueOf(mainModel.CPA()).substring(0, 6));
    }

    @Test
    public void testCostPerAcquisitionPerHour() throws ExecutionException, InterruptedException {
        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.hourlyCost(clickService.getStream()).values().toArray()));
        ArrayList<Double> impressionOccurrences = new ArrayList(Arrays.asList(GraphDataService.hourlyCost(impressionService.getStream()).values().toArray()));
        ArrayList<Double> conversionOccurrences = new ArrayList(Arrays.asList(GraphDataService.hourlyCount(serverLogService.getConversionStream()).values().toArray()));
        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }
        double plus1 = 0.0;

        for (int i = 0; i <= impressionOccurrences.size() - 1; i++) {
            plus1 = plus1 + impressionOccurrences.get(i);
        }
        double plus2 = 0.0;

        for (int i = 0; i <= conversionOccurrences.size() - 1; i++) {
            plus2 = plus2 + conversionOccurrences.get(i);
        }
        assertEquals(String.valueOf((plus + plus1) / plus2).substring(0, 6), String.valueOf(mainModel.CPA()).substring(0, 6));
    }

    @Test
    public void testCostPerAcquisitionPerMonth() throws ExecutionException, InterruptedException {

        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.monthlyCost(clickService.getStream()).values().toArray()));
        ArrayList<Double> impressionOccurrences = new ArrayList(Arrays.asList(GraphDataService.monthlyCost(impressionService.getStream()).values().toArray()));
        ArrayList<Double> conversionOccurrences = new ArrayList(Arrays.asList(GraphDataService.monthlyCount(serverLogService.getConversionStream()).values().toArray()));
        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }

        double plus1 = 0.0;

        for (int i = 0; i <= impressionOccurrences.size() - 1; i++) {
            plus1 = plus1 + impressionOccurrences.get(i);
        }

        double plus2 = 0.0;

        for (int i = 0; i <= conversionOccurrences.size() - 1; i++) {
            plus2 = plus2 + conversionOccurrences.get(i);
        }

        assertEquals(String.valueOf((plus + plus1) / plus2).substring(0, 6), String.valueOf(mainModel.CPA()).substring(0, 6));
    }

    @Test
    public void testCostPerAcquisitionPerYear() throws ExecutionException, InterruptedException {

        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.yearlyCost(clickService.getStream()).values().toArray()));
        ArrayList<Double> impressionOccurrences = new ArrayList(Arrays.asList(GraphDataService.yearlyCost(impressionService.getStream()).values().toArray()));
        ArrayList<Double> conversionOccurrences = new ArrayList(Arrays.asList(GraphDataService.yearlyCount(serverLogService.getConversionStream()).values().toArray()));

        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }

        double plus1 = 0.0;

        for (int i = 0; i <= impressionOccurrences.size() - 1; i++) {
            plus1 = plus1 + impressionOccurrences.get(i);
        }

        double plus2 = 0.0;

        for (int i = 0; i <= conversionOccurrences.size() - 1; i++) {
            plus2 = plus2 + conversionOccurrences.get(i);
        }

        assertEquals(String.valueOf((plus + plus1) / plus2).substring(0, 6), String.valueOf(mainModel.CPA()).substring(0, 6));
    }
}
