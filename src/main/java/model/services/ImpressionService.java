package model.services;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.index.unique.UniqueIndex;
import model.models.GraphOptions;
import model.models.Impression;
import model.models.enums.*;
import model.utilities.CSVService;
import model.utilities.DateService;
import model.utilities.TimerService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.googlecode.cqengine.query.QueryFactory.*;
import static com.googlecode.cqengine.query.option.EngineThresholds.INDEX_ORDERING_SELECTIVITY;

public class ImpressionService extends DataProviderService implements Runnable {

    private final IndexedCollection<Impression> impressions = new ConcurrentIndexedCollection<>();

    private int count;
    private Double totalCost;
    private final String filepath;
    private boolean hasError = false;

    public ImpressionService(String filepath) {
        this.filepath = filepath;
        run();
    }

    @Override
    public void run() {
        initialiseIndexedCollection();
        readDataFromCSV(filepath);
        if (!hasError) {
            initialiseMetrics();
        }
    }

    private void initialiseMetrics() {
        count = impressions.size();
        Stream<Double> cost = impressions.stream().map(Impression::getCost);
        totalCost = cost.reduce(0.0, Double::sum);
    }

    /**
     * Initialise the indexed collection for CQEngine using the attributes specified
     * in the Impression class.
     */
    private void initialiseIndexedCollection() {
        impressions.addIndex(UniqueIndex.onAttribute(Impression.UNIQUE_ID));
        impressions.addIndex(NavigableIndex.onAttribute((Attribute) Impression.DATE));
    }

    /**
     * Parses a CSV of impression data into a list of Impression objects
     *
     * @param filepath The the path from source root to a file as a String.
     */
    private void readDataFromCSV(String filepath) {
        List<String[]> parsedCSV = CSVService.parse(filepath, FileType.IMPRESSION_LOG);
        if (parsedCSV.size() == 0) {
            setHasError(true);
        } else {

            // Delete the header row
            parsedCSV.remove(0);
            TimerService timer = new TimerService("Instantiated impressions");
            timer.start();
            List<Impression> impressionsFromCSV = parsedCSV.stream().filter(this::isValid).map(Impression::new).collect(Collectors.toList());
            impressions.addAll(impressionsFromCSV);
            timer.stop();
        }
    }

    private boolean isValid(String[] line) {
        return DateService.isValidDateString(line[0]) && line[1].length() > 0 && Gender.isValidGender(line[2]) && AgeBracket.isValidAgeBracket(line[3]) && Income.isValidIncome(line[4]) && Context.isValidContext(line[5]) && CSVService.isDouble(line[6]);
    }

    public Stream<Impression> getStream() {
        return impressions.retrieve(all(Impression.class), queryOptions(orderBy(ascending(Impression.DATE)), applyThresholds(threshold(INDEX_ORDERING_SELECTIVITY, 1.0)))).stream();
    }

    public Stream<Impression> getStream(GraphOptions options) {
        return impressions.retrieve(all(Impression.class), queryOptions(orderBy(ascending(Impression.DATE)), applyThresholds(threshold(INDEX_ORDERING_SELECTIVITY, 1.0)))).stream().filter(impression -> (options.getAgeFilter().contains(impression.getAgeBracket())) && (options.getContextFilter().contains(impression.getContext())) && (options.getGenderFilter().contains(impression.getGender())) && (options.getIncomeFilter().contains(impression.getIncome())));
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public boolean isHasError() {
        return hasError;
    }

    public Double getCost() {
        return totalCost;
    }

    public int getCount() {
        return count;
    }
}
