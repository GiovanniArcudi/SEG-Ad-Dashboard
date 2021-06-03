import model.MainModel;
import model.models.enums.FileType;
import model.services.ClickService;
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

public class TestClicks {
    public MainModel mainModel = new MainModel();
    public ClickService clickService = new ClickService("src/test/resources/example-dataset/click_log.csv");

    /*
     * NUMBER OF CLICKS
     */

    //Test so that the number of clicks are correct
    @Test
    public void testNumberOfClicks() throws ExecutionException, InterruptedException {

        //Check whether the number of clicks of days,hours, weeks, months,years are different
        assertEquals(mainModel.clicksCount(), clickService.getCount());
        assertNotEquals(GraphDataService.dailyCount(clickService.getStream()).size(), GraphDataService.hourlyCount(clickService.getStream()).size());
        assertNotEquals(GraphDataService.hourlyCount(clickService.getStream()).size(), GraphDataService.monthlyCount(clickService.getStream()).size());
        assertNotEquals(GraphDataService.monthlyCount(clickService.getStream()).size(), GraphDataService.weeklyCount(clickService.getStream()).size());
        assertNotEquals(GraphDataService.monthlyCount(clickService.getStream()).size(), GraphDataService.yearlyCount(clickService.getStream()).size());
    }

    @Test
    public void testClicksPerDays() throws ExecutionException, InterruptedException {

        List<LocalDateTime> dailyDates = new ArrayList<>(GraphDataService.dailyCount(clickService.getStream()).keySet());
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.dailyCount(clickService.getStream()).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(clickService.getCount())));
        assertEquals(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(1)).substring(1, 11), dailyDates.get(0).toString().substring(0, 10));
        int occurrencesOfFirstDate = 1;

        for (int i = 1; i <= clickService.getCount(); i++) {
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i)).substring(1, 11)
                    .equals(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i + 1)).substring(1, 11))) {
                occurrencesOfFirstDate += 1;
            } else {
                break;
            }
        }

        double firstOccurrence = occurrences.get(0);
        int firstOccur = (int) (firstOccurrence);
        assertEquals(Integer.parseInt(String.valueOf(occurrencesOfFirstDate)), firstOccur);
        assertEquals(GraphDataService.dailyCount(clickService.getStream()).values().size(), GraphDataService.dailyCount(clickService.getStream()).keySet().size());
        assertEquals(GraphDataService.dailyCount(clickService.getStream()).size(), GraphDataService.dailyCount(clickService.getStream()).size());
    }

    @Test
    public void testClicksPerHours() throws ExecutionException, InterruptedException {

        List<LocalDateTime> hourlyDates = new ArrayList<>(GraphDataService.hourlyCount(clickService.getStream()).keySet());
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.hourlyCount(clickService.getStream()).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(clickService.getCount())));
        assertEquals(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(1)).substring(1, 11), hourlyDates.get(0).toString().substring(0, 10));
        long occurrencesOfFirstDate = 1;

        for (int i = 1; i <= clickService.getCount(); i++) {
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i)).substring(1, 11)
                    .equals(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i + 1)).substring(1, 11))) {
                occurrencesOfFirstDate = occurrencesOfFirstDate + 1;
            } else {
                break;
            }
        }

        double firstOccurrence = occurrences.get(0);
        int firstOccur = (int) (firstOccurrence);
        assertEquals(Integer.parseInt(String.valueOf(occurrencesOfFirstDate)), firstOccur);
        assertEquals(GraphDataService.hourlyCount(clickService.getStream()).values().size(), GraphDataService.hourlyCount(clickService.getStream()).keySet().size());
        assertEquals(GraphDataService.hourlyCount(clickService.getStream()).size(), GraphDataService.hourlyCount(clickService.getStream()).size());
    }

    @Test
    public void testClicksPerWeeks() throws ExecutionException, InterruptedException {

        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.weeklyCount(clickService.getStream()).values().toArray()));

        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(clickService.getCount())));
        assertEquals(GraphDataService.weeklyCount(clickService.getStream()).values().size(), GraphDataService.weeklyCount(clickService.getStream()).keySet().size());
        assertEquals(GraphDataService.weeklyCount(clickService.getStream()).size(), GraphDataService.weeklyCount(clickService.getStream()).size());
    }

    @Test
    public void testClicksPerMonths() throws ExecutionException, InterruptedException {

        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.monthlyCount(clickService.getStream()).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(clickService.getCount())));
        assertEquals(GraphDataService.monthlyCount(clickService.getStream()).values().size(), GraphDataService.monthlyCount(clickService.getStream()).keySet().size());
        assertEquals(GraphDataService.monthlyCount(clickService.getStream()).size(), GraphDataService.monthlyCount(clickService.getStream()).size());
    }

    @Test
    public void testClicksPerYears() throws ExecutionException, InterruptedException {

        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.yearlyCount(clickService.getStream()).values().toArray()));
        long plus = 0;

        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }

        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(clickService.getCount())));
        assertEquals(GraphDataService.yearlyCount(clickService.getStream()).values().size(), GraphDataService.yearlyCount(clickService.getStream()).keySet().size());
        assertEquals(GraphDataService.yearlyCount(clickService.getStream()).size(), GraphDataService.yearlyCount(clickService.getStream()).size());
    }
}
