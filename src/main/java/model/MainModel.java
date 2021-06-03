package model;

import model.models.Click;
import model.models.GraphOptions;
import model.models.ModelInstance;
import model.models.ModelInstanceWithCost;
import model.models.enums.Metric;
import model.models.enums.TimeInterval;
import model.services.ClickService;
import model.services.ImpressionService;
import model.services.ServerLogService;
import model.utilities.GraphDataService;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainModel {

    private ClickService _clickService;
    private ImpressionService _impressionService;
    private ServerLogService _serverLogService;
    private boolean error = true;

    /**
     * Construct {@link MainModel} from specified log file paths
     *
     * @param clickLogPath          The path from content route for the click log
     * @param impressionLogPath     The path from content route for the impression log path
     * @param serverLogPath         The path from content route for the server log path
     */
    public MainModel(String name, String clickLogPath, String impressionLogPath, String serverLogPath) {

        CompletableFuture<Void> loadingTask = CompletableFuture.allOf(
                CompletableFuture.supplyAsync(() -> new ClickService(clickLogPath)).thenAccept(cs -> this._clickService = cs),
                CompletableFuture.supplyAsync(() -> new ImpressionService(impressionLogPath)).thenAccept(is -> this._impressionService = is),
                CompletableFuture.supplyAsync(() -> new ServerLogService(serverLogPath)).thenAccept(ss -> this._serverLogService = ss));
        loadingTask.join();
        setError(_clickService.isHasError() || _impressionService.isHasError() || _serverLogService.isHasError());
        setLoading();
    }

    /**
     * Construct {@link MainModel} with default files
     */
    public MainModel() {
        this("Example campaign", "src/test/resources/example-dataset/click_log.csv", "src/test/resources/example-dataset/impression_log.csv", "src/test/resources/example-dataset/server_log.csv");
    }

    public MainModel(String name, String filepath) {
        this(name, filepath + "/click_log.csv", filepath + "/impression_log.csv", filepath + "/server_log.csv");
    }

    private Map<LocalDateTime, Double> averageCountData(Map<LocalDateTime, Double> numerator, Map<LocalDateTime, Double> denominator) {
        Map<LocalDateTime, Double> result = new LinkedHashMap<>();

        // If the denominator doesn't contain the key, use the last known value as it has not changed
        double lastKnownValue = 0;

        for (LocalDateTime date : numerator.keySet()) {
            if (denominator.containsKey(date)) {
                lastKnownValue = denominator.get(date);
            }
            if (lastKnownValue > 0) {
                result.put(date, numerator.get(date) / lastKnownValue);
            } else {
                result.put(date, 0.0);
            }
        }
        return result;
    }

    private Map<LocalDateTime, Double> groupCountDataByInterval(Stream stream, TimeInterval interval) throws ExecutionException, InterruptedException {
        switch (interval) {
            case HOUR:
                return GraphDataService.hourlyCount(stream);
            default:
                return GraphDataService.dailyCount(stream);
            case WEEK:
                return GraphDataService.weeklyCount(stream);
            case MONTH:
                return GraphDataService.monthlyCount(stream);
            case YEAR:
                return GraphDataService.yearlyCount(stream);
        }
    }

    private Map<LocalDateTime, Double> groupCostDataByInterval(Stream<ModelInstanceWithCost> stream, TimeInterval interval) throws ExecutionException, InterruptedException {
        switch (interval) {
            case HOUR:
                return GraphDataService.hourlyCost(stream);
            default:
                return GraphDataService.dailyCost(stream);
            case WEEK:
                return GraphDataService.weeklyCost(stream);
            case MONTH:
                return GraphDataService.monthlyCost(stream);
            case YEAR:
                return GraphDataService.yearlyCost(stream);
        }
    }

    public Map<LocalDateTime, Double> getDataOverTime(Metric metric, GraphOptions options) throws ExecutionException, InterruptedException {
        switch (metric) {
            default:
                return GraphDataService.getMapOverTime(groupCountDataByInterval(_impressionService.getStream(options), options.getGranularity()), options);
            case CLICKS:
                return GraphDataService.getMapOverTime(groupCountDataByInterval(_clickService.getStream(), options.getGranularity()), options);
            case UNIQUES:
                return GraphDataService.getMapOverTime(groupCountDataByInterval(_clickService.getUniqueStream(), options.getGranularity()), options);
            case CONVERSIONS:
                return GraphDataService.getMapOverTime(groupCountDataByInterval(_serverLogService.getConversionStream(), options.getGranularity()), options);
            case BOUNCES:
                return GraphDataService.getMapOverTime(groupCountDataByInterval(_serverLogService.getBounceStream(options), options.getGranularity()), options);
            case COST:
                Stream<ModelInstanceWithCost> costStream = Stream.concat(_clickService.getStream(), (_impressionService.getStream(options))).sorted(Comparator.comparing(ModelInstance::getDate));
                return GraphDataService.getMapOverTime(groupCostDataByInterval(costStream, options.getGranularity()), options);
            case CTR: // Click per impression
                Map<LocalDateTime, Double> clicks = getDataOverTime(Metric.CLICKS, options);
                Map<LocalDateTime, Double> impressions = getDataOverTime(Metric.IMPRESSIONS, options);
                return averageCountData(clicks, impressions);
            case CPA:// Cost per conversion
                Map<LocalDateTime, Double> cost = getDataOverTime(Metric.COST, options);
                Map<LocalDateTime, Double> conversions = getDataOverTime(Metric.CONVERSIONS, options);
                return averageCountData(cost, conversions);
            case CPC:
                cost = getDataOverTime(Metric.COST, options);
                clicks = getDataOverTime(Metric.CLICKS, options);
                return averageCountData(cost, clicks);
            case CPM: // Cost per 1000 impressions
                cost = getDataOverTime(Metric.COST, options);
                impressions = getDataOverTime(Metric.IMPRESSIONS, options);
                impressions.replaceAll((key, value) -> value / 1000);
                return averageCountData(cost, impressions);
            case BOUNCE_RATE: // Bounce per click
                Map<LocalDateTime, Double> bounces = getDataOverTime(Metric.BOUNCES, options);
                clicks = getDataOverTime(Metric.CLICKS, options);
                return averageCountData(bounces, clicks);
        }
    }

    //----------------------------------------------------------------------
    //METRICS:

    public long impressionsCount() {
        return _impressionService.getCount();
    }

    public long impressionsCount(GraphOptions graphOptions) {
        return GraphDataService.getStreamOverTime(_impressionService.getStream(graphOptions), graphOptions).count();
    }

    public long clicksCount() {
        return _clickService.getCount();
    }

    public long clicksCount(GraphOptions graphOptions) {
        return GraphDataService.getStreamOverTime(_clickService.getStream(), graphOptions).count();
    }

    //Get the number of uniques. This is the number of unique IDs in the click log
    public long uniquesCount() {
        return _clickService.getUniques();
    }

    public long uniquesCount(GraphOptions graphOptions) {
        return GraphDataService.getStreamOverTime(_clickService.getUniqueStream(), graphOptions).count();
    }

    public long bouncesCount(GraphOptions graphOptions) {
        if (graphOptions.getStart() == null || graphOptions.getEnd() == null) {
            return _serverLogService.getBounceStream(graphOptions).count();
        }
        return GraphDataService.getStreamOverTime(_serverLogService.getBounceStream(graphOptions), graphOptions).count();
    }

    // Get the number of conversions
    public long conversionsCount() {
        return _serverLogService.getConversions();
    }

    public long conversionsCount(GraphOptions graphOptions) {
        return GraphDataService.getStreamOverTime(_serverLogService.getConversionStream(), graphOptions).count();
    }

    // Get the total cost
    public Double totalCost() {
        return _clickService.getCost() + _impressionService.getCost();
    }

    public Double totalCost(GraphOptions graphOptions) {
        return GraphDataService.getStreamOverTime(Stream.concat(_clickService.getStream(), _impressionService.getStream(graphOptions)), graphOptions).mapToDouble(ModelInstanceWithCost::getCost).sum();
    }

    public Map<String, Long> getHistogramData(GraphOptions options) {
        Stream<Click> clickStream = _clickService.getStream();
        if(options.getStart() != null && options.getEnd() != null) {
            clickStream = clickStream.filter(click -> click.getDate().isAfter(options.getStart()) && click.getDate().isBefore(options.getEnd()));
        }
        return clickStream.map(click -> String.valueOf(Math.floor(click.getCost()))).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    // CTR: Clicks per impression
    public Double CTR() {
        return clicksCount() / (double) impressionsCount();
    }

    public Double CTR(GraphOptions graphOptions) {
        return clicksCount(graphOptions) / (double) impressionsCount(graphOptions);
    }

    // CPA: Cost per conversion
    public Double CPA() {
        return totalCost() / conversionsCount();
    }

    public Double CPA(GraphOptions graphOptions) {
        return totalCost(graphOptions) / conversionsCount(graphOptions);
    }

    // CPC: Cost per click
    public Double CPC() {
        return totalCost() / clicksCount();
    }

    public Double CPC(GraphOptions graphOptions) {
        return totalCost(graphOptions) / clicksCount(graphOptions);
    }

    // CPM: Cost per thousand impressions
    public Double CPM() {
        return totalCost() / ((double) impressionsCount() / 1000);
    }

    public Double CPM(GraphOptions graphOptions) {
        return totalCost(graphOptions) / ((double) impressionsCount(graphOptions) / 1000);
    }

    public Double bounceRate(GraphOptions graphOptions) {
        if (graphOptions.getStart() == null || graphOptions.getEnd() == null) {
            return bouncesCount(graphOptions) / (double) clicksCount();
        }
        return bouncesCount(graphOptions) / (double) clicksCount(graphOptions);
    }

    public void setLoading() {
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
