import model.MainModel;
import model.services.ClickService;
import model.utilities.GraphDataService;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestUniques {

    public MainModel mainModel = new MainModel();
    public ClickService clickService = new ClickService("src/test/resources/example-dataset/click_log.csv");

    /*
     * NUMBER OF UNIQUES
     * Already checked that file input is read correctly in Number of Clicks
     *  and since uniques and clicks use the click_log file there is no need to re-check that reader methods work
     */
    @Test
    public void testNumberOfUniques() throws ExecutionException, InterruptedException {
        assertEquals(mainModel.uniquesCount(), clickService.getUniqueStream().count());
        assertEquals(mainModel.uniquesCount(), clickService.getUniques());
        assertNotEquals(GraphDataService.dailyCount(clickService.getUniqueStream()).size(), GraphDataService.hourlyCount(clickService.getUniqueStream()).size());
        assertNotEquals(GraphDataService.hourlyCount(clickService.getUniqueStream()).size(), GraphDataService.monthlyCount(clickService.getUniqueStream()).size());
        assertNotEquals(GraphDataService.monthlyCount(clickService.getUniqueStream()).size(), GraphDataService.weeklyCount(clickService.getUniqueStream()).size());
        assertNotEquals(GraphDataService.monthlyCount(clickService.getUniqueStream()).size(), GraphDataService.yearlyCount(clickService.getUniqueStream()).size());
    }

    @Test
    public void testUniquesPerHours() throws ExecutionException, InterruptedException {
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.hourlyCount(clickService.getUniqueStream()).values().toArray()));
        long plus = 0;
        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }
        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(clickService.getUniqueStream().count())));
        assertEquals(GraphDataService.hourlyCount(clickService.getUniqueStream()).values().size(), GraphDataService.hourlyCount(clickService.getUniqueStream()).keySet().size());
        assertEquals(GraphDataService.hourlyCount(clickService.getUniqueStream()).size(), GraphDataService.hourlyCount(clickService.getUniqueStream()).size());
    }

    @Test
    public void testUniquesPerWeeks() throws ExecutionException, InterruptedException {
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.weeklyCount(clickService.getUniqueStream()).values().toArray()));
        long plus = 0;
        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }
        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(clickService.getUniqueStream().count())));
        assertEquals(GraphDataService.weeklyCount(clickService.getUniqueStream()).values().size(), GraphDataService.weeklyCount(clickService.getUniqueStream()).keySet().size());
        assertEquals(GraphDataService.weeklyCount(clickService.getUniqueStream()).size(), GraphDataService.weeklyCount(clickService.getUniqueStream()).size());
    }

    @Test
    public void testUniquesPerMonths() throws ExecutionException, InterruptedException {
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.monthlyCount(clickService.getUniqueStream()).values().toArray()));
        long plus = 0;
        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }
        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(clickService.getUniqueStream().count())));
        assertEquals(GraphDataService.monthlyCount(clickService.getUniqueStream()).values().size(), GraphDataService.monthlyCount(clickService.getUniqueStream()).keySet().size());
        assertEquals(GraphDataService.monthlyCount(clickService.getUniqueStream()).size(), GraphDataService.monthlyCount(clickService.getUniqueStream()).size());
    }

    @Test
    public void testUniquesPerYears() throws ExecutionException, InterruptedException {
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.yearlyCount(clickService.getUniqueStream()).values().toArray()));
        long plus = 0;
        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }
        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(clickService.getUniqueStream().count())));
        assertEquals(GraphDataService.yearlyCount(clickService.getUniqueStream()).values().size(), GraphDataService.yearlyCount(clickService.getUniqueStream()).keySet().size());
        assertEquals(GraphDataService.yearlyCount(clickService.getUniqueStream()).size(), GraphDataService.yearlyCount(clickService.getUniqueStream()).size());
    }

    @Test
    public void testUniquesPerDays() throws ExecutionException, InterruptedException {
        ArrayList<Double> occurrences = new ArrayList(Arrays.asList(GraphDataService.dailyCount(clickService.getUniqueStream()).values().toArray()));
        long plus = 0;
        for (int i = 0; i <= occurrences.size() - 1; i++) {
            plus = (long) (plus + occurrences.get(i));
        }
        assertEquals(Integer.parseInt(String.valueOf(plus)), Integer.parseInt(String.valueOf(clickService.getUniqueStream().count())));
        assertEquals(GraphDataService.dailyCount(clickService.getUniqueStream()).values().size(), GraphDataService.dailyCount(clickService.getUniqueStream()).keySet().size());
        assertEquals(GraphDataService.dailyCount(clickService.getUniqueStream()).size(), GraphDataService.dailyCount(clickService.getUniqueStream()).size());
    }
}
