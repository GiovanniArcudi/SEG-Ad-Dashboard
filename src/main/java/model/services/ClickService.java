package model.services;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.index.unique.UniqueIndex;
import model.models.Click;
import model.models.enums.FileType;
import model.utilities.CSVService;
import model.utilities.DateService;
import model.utilities.GraphDataService;
import model.utilities.TimerService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.googlecode.cqengine.query.QueryFactory.*;
import static com.googlecode.cqengine.query.option.EngineThresholds.INDEX_ORDERING_SELECTIVITY;

/**
 * ClickService allows for interaction with Click objects. It is instantiated with a filepath
 * indicating the CSV click log file to be used.
 */
public class ClickService extends DataProviderService implements Runnable {

    private final IndexedCollection<Click> clicks = new ConcurrentIndexedCollection<>();
    private final String filepath;
    private Double cost;
    private int count;
    private boolean hasError = false;

    @Override
    public void run() {
        initialiseIndexedCollection();
        readDataFromCSV(filepath);
        initialiseMetrics();
    }

    /**
     * Initialise the indexed collection for CQEngine using the attributes specified
     * in the Click class.
     */
    private void initialiseIndexedCollection() {
        clicks.addIndex(UniqueIndex.onAttribute(Click.UNIQUE_ID));
        clicks.addIndex(NavigableIndex.onAttribute((SimpleAttribute) Click.DATE));
    }


    /**
     * Parses a CSV of click data into a list of Click objects
     *
     * @param filepath The the path from source root to a file as a String.
     */
    private void readDataFromCSV(String filepath) {
        List<String[]> parsedCSV = CSVService.parse(filepath, FileType.CLICK_LOG);

        if (parsedCSV.size() == 0) {
            setHasError(true);
        } else {

            // Delete the header row
            parsedCSV.remove(0);
            TimerService timer = new TimerService("Instantiated clicks");
            timer.start();
            List<Click> clicksFromCSV = parsedCSV.stream().filter(this::isValid).map(Click::new).collect(Collectors.toList());
            clicks.addAll(clicksFromCSV);
            timer.stop();
        }
    }

    public ClickService(String filepath) {
        this.filepath = filepath;
        run();
    }

    private void initialiseMetrics() {
        cost = clicks.stream().map(Click::getCost).reduce(0.0, Double::sum);
        count = clicks.size();
    }

    public Double getCost() {
        return cost;
    }

    public int getCount() {
        return count;
    }

    public long getUniques() {
        return clicks.retrieve(all(Click.class), queryOptions(orderBy(ascending(Click.DATE)), applyThresholds(threshold(INDEX_ORDERING_SELECTIVITY, 1.0)))).stream().filter(GraphDataService.distinctByKey(Click::getId)).count();
    }

    private boolean isValid(String[] line) {
        return DateService.isValidDateString(line[0]) && line[1].length() > 0 && CSVService.isDouble(line[2]);
    }

    public Stream<Click> getUniqueStream() {
        return clicks.retrieve(all(Click.class), queryOptions(orderBy(ascending(Click.DATE)), applyThresholds(threshold(INDEX_ORDERING_SELECTIVITY, 1.0)))).stream().filter(GraphDataService.distinctByKey(Click::getId));
    }

    public Stream<Click> getStream() {
        return clicks.retrieve(all(Click.class), queryOptions(orderBy(ascending(Click.DATE)), applyThresholds(threshold(INDEX_ORDERING_SELECTIVITY, 1.0)))).stream();
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public boolean isHasError() {
        return hasError;
    }
}
