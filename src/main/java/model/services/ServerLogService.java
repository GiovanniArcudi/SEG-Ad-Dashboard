package model.services;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import com.googlecode.cqengine.resultset.ResultSet;
import com.univocity.parsers.conversions.Conversion;
import model.models.Click;
import model.models.GraphOptions;
import model.models.ServerLog;
import model.models.enums.BounceDefinition;
import model.models.enums.FileType;
import model.utilities.CSVService;
import model.utilities.DateService;
import model.utilities.TimerService;

import java.io.File;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.googlecode.cqengine.codegen.AttributeBytecodeGenerator.createAttributes;
import static com.googlecode.cqengine.query.QueryFactory.*;
import static com.googlecode.cqengine.query.QueryFactory.ascending;

public class ServerLogService extends DataProviderService implements Runnable {
    private final IndexedCollection<ServerLog> serverLog = new ConcurrentIndexedCollection<ServerLog>();
    private final SQLParser<ServerLog> parser = SQLParser.forPojoWithAttributes(ServerLog.class, createAttributes(ServerLog.class));

    private int count;
    private long conversions;
    private final String filepath;
    private boolean hasError = false;
    private int bounces;

    public ServerLogService(String filepath) {
        this.filepath = filepath;
        run();
    }

    public long getConversions() {
        return conversions;
    }

    public int getCount() {
        return count;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public boolean isHasError() {
        return hasError;
    }

    private boolean isValid(String[] line) {
        return DateService.isValidDateString(line[0]) && line[1].length() > 0 && (DateService.isValidDateString(line[2]) || line[2].equals("n/a")) && CSVService.isInteger(line[3]) && (line[4].equals("Yes") || line[4].equals("No"));
    }

    private void initialiseMetrics() {
        count = serverLog.size();
        conversions = runQuery(equal(ServerLog.SUCCESSFUL_CONVERSION, true), queryOptions()).size();
    }

    private void initialiseIndexedCollection() {
        serverLog.addIndex(HashIndex.onAttribute(ServerLog.ID));
        serverLog.addIndex(NavigableIndex.onAttribute(ServerLog.VIEWS));
    }

    public ResultSet<ServerLog> runQuery(Query<ServerLog> query, QueryOptions queryOptions) {
        return serverLog.retrieve(query, queryOptions);
    }

    public ResultSet<ServerLog> runQuery(String SQLQuery) {
        return parser.retrieve(serverLog, SQLQuery);
    }

    private void readDataFromCSV(String filepath) {
        List<String[]> parsedCSV = CSVService.parse(filepath, FileType.SERVER_LOG);
        if (parsedCSV.size() == 0) {
            setHasError(true);
        } else {
            // Delete the header row
            parsedCSV.remove(0);
            TimerService timer = new TimerService("Instantiated server logs");
            timer.start();
            List<ServerLog> serverLogsFromCSV = parsedCSV.stream().map(ServerLog::new).collect(Collectors.toList());
            serverLog.addAll(serverLogsFromCSV);
            timer.stop();
        }
    }

    public Stream<ServerLog> getStream() {
        return serverLog.stream();
    }

    @Override
    public void run() {
        initialiseIndexedCollection();
        readDataFromCSV(filepath);
        initialiseMetrics();
    }

    public Stream<ServerLog> getConversionStream() {
        return serverLog.retrieve(all(ServerLog.class), queryOptions(orderBy(ascending(ServerLog.ENTRY_DATE)))).stream().filter(ServerLog::getSuccessfulConversion);
    }

    public Stream<ServerLog> getBounceStream(GraphOptions options) {
        if (options.getBounceDefinition() == BounceDefinition.THRESHOLD) {
            return serverLog.retrieve(all(ServerLog.class), queryOptions(orderBy(ascending(ServerLog.ENTRY_DATE)))).stream().filter(serverLog -> !serverLog.hasExitDate() || (serverLog.hasExitDate() && (ChronoUnit.SECONDS.between(serverLog.getDate(), serverLog.getExitDate()) <= options.getBounceThreshold())));
        }
        return serverLog.retrieve(all(ServerLog.class), queryOptions(orderBy(ascending(ServerLog.ENTRY_DATE)))).stream().filter(serverLog -> serverLog.getViews() == 1);
    }
}
