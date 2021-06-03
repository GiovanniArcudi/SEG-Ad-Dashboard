import model.MainModel;
import model.models.enums.FileType;
import model.services.ImpressionService;
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

public class TestImpressions {

    public MainModel mainModel = new MainModel();
    public ImpressionService impressionService = new ImpressionService("src/test/resources/example-dataset/impression_log.csv");

    /*
     * NUMBER OF IMPRESSIONS
     */
    //Test so that the number of Impressions are correct
    @Test
    public void testNumberOfImpressions() throws ExecutionException, InterruptedException {
        //Check whether the number of Impressions of days,hours, weeks, months,years are different
        assertEquals(mainModel.impressionsCount(), impressionService.getCount());
        assertNotEquals(GraphDataService.dailyCount(impressionService.getStream()).size(), GraphDataService.hourlyCount(impressionService.getStream()).size());
        assertNotEquals(GraphDataService.hourlyCount(impressionService.getStream()).size(), GraphDataService.monthlyCount(impressionService.getStream()).size());
        assertNotEquals(GraphDataService.monthlyCount(impressionService.getStream()).size(), GraphDataService.weeklyCount(impressionService.getStream()).size());
        assertNotEquals(GraphDataService.monthlyCount(impressionService.getStream()).size(), GraphDataService.yearlyCount(impressionService.getStream()).size());

    }

    @Test
    public void testImpressionsPerDays() throws ExecutionException, InterruptedException {
        List<LocalDateTime> dailyDates = new ArrayList<>(GraphDataService.dailyCount(impressionService.getStream()).keySet());
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.dailyCount(impressionService.getStream()).values().toArray()));
        long plus = 0;
        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }
        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(impressionService.getCount())));
        assertEquals(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/impression_log.csv", FileType.IMPRESSION_LOG).get(1)).substring(1, 11), dailyDates.get(0).toString().substring(0, 10));
        int occurrencesOfFirstDate = 1;
        for (int i = 1; i <= impressionService.getCount(); i++) {
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/impression_log.csv", FileType.IMPRESSION_LOG).get(i)).substring(1, 11)
                    .equals(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/impression_log.csv", FileType.IMPRESSION_LOG).get(i + 1)).substring(1, 11))) {
                occurrencesOfFirstDate += 1;
            } else {
                break;
            }
        }
        double firstOccurrence = occurrences.get(0);
        int firstOccur = (int) (firstOccurrence);
        assertEquals(Integer.parseInt(String.valueOf(occurrencesOfFirstDate)), firstOccur);
        assertEquals(GraphDataService.dailyCount(impressionService.getStream()).values().size(), GraphDataService.dailyCount(impressionService.getStream()).keySet().size());
        assertEquals(GraphDataService.dailyCount(impressionService.getStream()).size(), GraphDataService.dailyCount(impressionService.getStream()).size());
    }

    @Test
    public void testImpressionsPerHours() throws ExecutionException, InterruptedException {
        List<LocalDateTime> hourlyDates = new ArrayList<>(GraphDataService.hourlyCount(impressionService.getStream()).keySet());
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.hourlyCount(impressionService.getStream()).values().toArray()));
        long plus = 0;
        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }
        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(impressionService.getCount())));
        assertEquals(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/impression_log.csv", FileType.IMPRESSION_LOG).get(1)).substring(1, 11), hourlyDates.get(0).toString().substring(0, 10));
        long occurrencesOfFirstDate = 1;
        for (int i = 1; i <= impressionService.getCount(); i++) {
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/impression_log.csv", FileType.IMPRESSION_LOG).get(i)).substring(1, 11)
                    .equals(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/impression_log.csv", FileType.IMPRESSION_LOG).get(i + 1)).substring(1, 11))) {
                occurrencesOfFirstDate = occurrencesOfFirstDate + 1;
            } else {
                break;
            }
        }
        double firstOccurrence = occurrences.get(0);
        int firstOccur = (int) (firstOccurrence);
        assertEquals(Integer.parseInt(String.valueOf(occurrencesOfFirstDate)), firstOccur);
        assertEquals(GraphDataService.hourlyCount(impressionService.getStream()).values().size(), GraphDataService.hourlyCount(impressionService.getStream()).keySet().size());
        assertEquals(GraphDataService.hourlyCount(impressionService.getStream()).size(), GraphDataService.hourlyCount(impressionService.getStream()).size());
    }

    @Test
    public void testImpressionsPerWeeks() throws ExecutionException, InterruptedException {
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.weeklyCount(impressionService.getStream()).values().toArray()));
        long plus = 0;
        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }
        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(impressionService.getCount())));
        assertEquals(GraphDataService.weeklyCount(impressionService.getStream()).values().size(), GraphDataService.weeklyCount(impressionService.getStream()).keySet().size());
        assertEquals(GraphDataService.weeklyCount(impressionService.getStream()).size(), GraphDataService.weeklyCount(impressionService.getStream()).size());
    }

    @Test
    public void testImpressionsPerMonths() throws ExecutionException, InterruptedException {
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.monthlyCount(impressionService.getStream()).values().toArray()));
        long plus = 0;
        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }
        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(impressionService.getCount())));
        assertEquals(GraphDataService.monthlyCount(impressionService.getStream()).values().size(), GraphDataService.monthlyCount(impressionService.getStream()).keySet().size());
        assertEquals(GraphDataService.monthlyCount(impressionService.getStream()).size(), GraphDataService.monthlyCount(impressionService.getStream()).size());
    }

    @Test
    public void testImpressionsPerYears() throws ExecutionException, InterruptedException {
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.yearlyCount(impressionService.getStream()).values().toArray()));
        long plus = 0;
        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }
        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(impressionService.getCount())));
        assertEquals(GraphDataService.yearlyCount(impressionService.getStream()).values().size(), GraphDataService.yearlyCount(impressionService.getStream()).keySet().size());
        assertEquals(GraphDataService.yearlyCount(impressionService.getStream()).size(), GraphDataService.yearlyCount(impressionService.getStream()).size());
    }
}
