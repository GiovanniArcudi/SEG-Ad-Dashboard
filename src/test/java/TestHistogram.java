import model.MainModel;
import model.models.GraphOptions;
import model.models.enums.FileType;
import model.services.ClickService;
import model.services.ImpressionService;
import model.utilities.CSVService;
import org.junit.Test;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestHistogram {

    public MainModel mainModel = new MainModel();
    public ImpressionService impressionService = new ImpressionService("src/test/resources/example-dataset/impression_log.csv");
    public ClickService clickService = new ClickService("src/test/resources/example-dataset/click_log.csv");

    @Test
    public void testHistogramValues() {
        long clickCost = 0;
        for (int i = 1; i <= clickService.getCount(); i++) {
            String str = Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i));
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i)).substring(str.length() - 9, str.length() - 1).startsWith("0") &&
                    Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i)).substring(str.length() - 11, str.length() - 1).startsWith(",")) {
                clickCost += 1;
            }
        }
        GraphOptions graphOptions = new GraphOptions();
        Map<String, Long> frequencyMap = mainModel.getHistogramData(graphOptions);
        long clickCostCount0 = frequencyMap.get("0.0");
        assertEquals(clickCost, clickCostCount0);
        long clickCostCount4 = frequencyMap.get("4.0");
        assertEquals(1, clickCostCount4);
    }

    @Test
    public void testFrequenciesSum() {
        int freqSum = 0;
        GraphOptions graphOptions = new GraphOptions();
        Map<String, Long> frequencyMap = mainModel.getHistogramData(graphOptions);
        long clickCostCount0 = frequencyMap.get("0.0");
        freqSum += clickCostCount0;
        long clickCostCount4 = frequencyMap.get("4.0");
        freqSum += clickCostCount4;
        long clickCostCount5 = frequencyMap.get("5.0");
        freqSum += clickCostCount5;
        long clickCostCount6 = frequencyMap.get("6.0");
        freqSum += clickCostCount6;
        long clickCostCount7 = frequencyMap.get("7.0");
        freqSum += clickCostCount7;
        long clickCostCount8 = frequencyMap.get("8.0");
        freqSum += clickCostCount8;
        long clickCostCount9 = frequencyMap.get("9.0");
        freqSum += clickCostCount9;
        long clickCostCount10 = frequencyMap.get("10.0");
        freqSum += clickCostCount10;
        long clickCostCount11 = frequencyMap.get("11.0");
        freqSum += clickCostCount11;
        long clickCostCount12 = frequencyMap.get("12.0");
        freqSum += clickCostCount12;
        long clickCostCount13 = frequencyMap.get("13.0");
        freqSum += clickCostCount13;
        long clickCostCount14 = frequencyMap.get("14.0");
        freqSum += clickCostCount14;
        assertEquals(clickService.getCount(), freqSum);
    }

    @Test
    public void testHistogramsDateRange() {
        GraphOptions graphOptions1 = new GraphOptions();
        LocalDate localDate = LocalDate.of(2019, 1, 1);
        LocalDate localDate1 = LocalDate.of(2019, 2, 27);
        graphOptions1.setStart(localDate);
        graphOptions1.setEnd(localDate1);
        Map<String, Long> frequencyMap = mainModel.getHistogramData(graphOptions1);
        assertEquals(0, frequencyMap.size());

        GraphOptions graphOptions2 = new GraphOptions();
        LocalDate localDate3 = LocalDate.of(2015, 1, 1);
        LocalDate localDate4 = LocalDate.of(2015, 1, 5);
        graphOptions2.setStart(localDate3);
        graphOptions2.setEnd(localDate4);
        Map<String, Long> frequencyMap1 = mainModel.getHistogramData(graphOptions2);
        long clickCostFor0 = 0;
        for (int i = 1; i <= 4; i++) {
            String str = Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i));
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i)).substring(str.length() - 9, str.length() - 1).startsWith("0") &&
                    Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i)).substring(str.length() - 11, str.length() - 1).startsWith(",")) {
                clickCostFor0 += 1;
            }

        }
        long clickCost1 = 0;
        long clickCount = 0;
        for (int i = 1; i <= clickService.getCount(); i++) {
            String str = Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i));
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i)).startsWith("[2015-01-05")) {
                clickCost1 += Long.parseLong(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/click_log.csv", FileType.CLICK_LOG).get(i)).substring(str.length() - 9, str.length() - 8));
                clickCount += 1;
            }
        }
        long impressionCost = 0;
        for (int i = 1; i <= impressionService.getCount(); i++) {
            String str = Arrays.toString(CSVService.parse("src/test/resources/example-dataset/impression_log.csv", FileType.IMPRESSION_LOG).get(i));
            if (Arrays.toString(CSVService.parse("src/test/resources/example-dataset/impression_log.csv", FileType.IMPRESSION_LOG).get(i)).startsWith("[2015-01-05"))
                impressionCost += Long.parseLong(Arrays.toString(CSVService.parse("src/test/resources/example-dataset/impression_log.csv", FileType.IMPRESSION_LOG).get(i)).substring(str.length() - 9, str.length() - 8));
        }
        assertEquals(clickCostFor0, frequencyMap1.values().toArray()[0]);
        assertEquals((clickCost1 + impressionCost)/clickCount, Long.parseLong(String.valueOf(frequencyMap1.keySet().toArray()[1]).substring(0, 1)));
    }
}