import model.MainModel;
import model.models.enums.FileType;
import model.services.ClickService;
import model.services.ImpressionService;
import model.utilities.CSVService;
import model.utilities.GraphDataService;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class TestCost {
    public MainModel mainModel = new MainModel();
    public ImpressionService impressionService = new ImpressionService("src/test/resources/example-dataset/impression_log.csv");
    public ClickService clickService = new ClickService("src/test/resources/example-dataset/click_log.csv");

    /*
     * Total cost test
     * Also test the daily, hourly, weekly, monthly, yearly costs so that
     * for example dailyCost(clickService.getStream()) and dailyCost(impressionService.getStream()) works
     */

    @Test
    public void testTotalCost() {

        assertEquals((String.valueOf(impressionService.getCost() + clickService.getCost()).substring(0, 6)), (String.valueOf(mainModel.totalCost()).substring(0, 6)));
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
        assertEquals(String.valueOf(impressionCost).substring(0, 4), String.valueOf(impressionService.getCost()).substring(0, 4));
        assertEquals(String.valueOf(clickCost).substring(0, 5), String.valueOf(clickService.getCost()).substring(0, 5));
        assertEquals(String.valueOf(clickCost + impressionCost).substring(0, 5), String.valueOf(mainModel.totalCost()).substring(0, 5));
    }

    @Test
    public void testTotalCostPerDay() throws ExecutionException, InterruptedException {

        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.dailyCost(clickService.getStream()).values().toArray()));
        ArrayList<Double> impressionOccurrences = new ArrayList(Arrays.asList(GraphDataService.dailyCost(impressionService.getStream()).values().toArray()));
        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }
        double plus1 = 0.0;

        for (int i = 0; i <= impressionOccurrences.size() - 1; i++) {
            plus1 = plus1 + impressionOccurrences.get(i);
        }
        assertEquals(String.valueOf(plus + plus1).substring(0, 6), String.valueOf(mainModel.totalCost()).substring(0, 6));
    }

    @Test
    public void testTotalCostPerHour() throws ExecutionException, InterruptedException {

        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.hourlyCost(clickService.getStream()).values().toArray()));
        ArrayList<Double> impressionOccurrences = new ArrayList(Arrays.asList(GraphDataService.hourlyCost(impressionService.getStream()).values().toArray()));
        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }

        double plus1 = 0.0;

        for (int i = 0; i <= impressionOccurrences.size() - 1; i++) {
            plus1 = plus1 + impressionOccurrences.get(i);
        }
        assertEquals(String.valueOf(plus + plus1).substring(0, 6), String.valueOf(mainModel.totalCost()).substring(0, 6));
    }

    @Test
    public void testTotalCostPerWeeks() throws ExecutionException, InterruptedException {

        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.weeklyCost(clickService.getStream()).values().toArray()));
        ArrayList<Double> impressionOccurrences = new ArrayList(Arrays.asList(GraphDataService.weeklyCost(impressionService.getStream()).values().toArray()));

        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }

        double plus1 = 0.0;

        for (int i = 0; i <= impressionOccurrences.size() - 1; i++) {
            plus1 = plus1 + impressionOccurrences.get(i);
        }

        assertEquals(String.valueOf(plus + plus1).substring(0, 6), String.valueOf(mainModel.totalCost()).substring(0, 6));
    }

    @Test
    public void testTotalCostPerMonth() throws ExecutionException, InterruptedException {

        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.monthlyCost(clickService.getStream()).values().toArray()));
        ArrayList<Double> impressionOccurrences = new ArrayList(Arrays.asList(GraphDataService.monthlyCost(impressionService.getStream()).values().toArray()));

        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }

        double plus1 = 0.0;

        for (int i = 0; i <= impressionOccurrences.size() - 1; i++) {
            plus1 = plus1 + impressionOccurrences.get(i);
        }

        assertEquals(String.valueOf(plus + plus1).substring(0, 6), String.valueOf(mainModel.totalCost()).substring(0, 6));
    }

    @Test
    public void testTotalCostPerYear() throws ExecutionException, InterruptedException {

        ArrayList<Double> clickOccurrences = new ArrayList(Arrays.asList(GraphDataService.yearlyCost(clickService.getStream()).values().toArray()));
        ArrayList<Double> impressionOccurrences = new ArrayList(Arrays.asList(GraphDataService.yearlyCost(impressionService.getStream()).values().toArray()));

        double plus = 0.0;

        for (int i = 0; i <= clickOccurrences.size() - 1; i++) {
            plus = plus + clickOccurrences.get(i);
        }

        double plus1 = 0.0;

        for (int i = 0; i <= impressionOccurrences.size() - 1; i++) {
            plus1 = plus1 + impressionOccurrences.get(i);
        }

        assertEquals(String.valueOf(plus + plus1).substring(0, 6), String.valueOf(mainModel.totalCost()).substring(0, 6));
    }

}
