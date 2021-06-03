package model.utilities;

import javafx.scene.chart.XYChart;
import model.models.Cost;
import model.models.GraphOptions;
import model.models.ModelInstanceWithCost;
import model.models.ModelInstance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.reducing;

public class GraphDataService {
    public static Map<LocalDateTime, Double> convertToCumulative(Map<LocalDateTime, Double> data) {
        double previousValue = 0.0;
        for (Map.Entry<LocalDateTime, Double> entry : data.entrySet()) {
            double newVal = entry.getValue() + previousValue;
            entry.setValue(newVal);
            previousValue = newVal;
        }
        return data;
    }

    public static <T extends ModelInstance> Stream<T> getStreamOverTime(Stream<T> stream, GraphOptions options) {
        if (options.getStart() == null || options.getEnd() == null) {
            return stream;
        }
        return stream.filter(objectWithDate -> objectWithDate.getDate().isAfter(options.getStart()) && objectWithDate.getDate().isBefore(options.getEnd()));
    }

//    public static Map<String, Double> convertToHistogram(Map<LocalDateTime, Double> data, GraphOptions options) {
//        int interval = options.getHistogramDaysInterval();
//
//        LinkedHashMap<String, Double> results = new LinkedHashMap<>();
//        data.forEach((key, value) -> {
//            long diff = key.until(key, ChronoUnit.DAYS);
//            long daysAfterStart = diff - (diff % interval);
//            results.put(options.getStart().plusDays(daysAfterStart).toLocalDate().toString(), value);
//        });
//        return results;
//    }

    public static XYChart.Series<Number, Number> getLineChartSeries(Map<LocalDateTime, Double> data) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (Map.Entry<LocalDateTime, Double> entry : data.entrySet()) {
            LocalDateTime date = entry.getKey();
            Double frequency = entry.getValue();
            series.getData().add(new XYChart.Data<>(date.atZone(ZoneId.systemDefault()).toEpochSecond(), frequency));
        }
        return series;
    }

    public static XYChart.Series<String, Number> getHistogramSeries(Map<String, Long> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            String date = entry.getKey();
            Long frequency = entry.getValue();
            series.getData().add(new XYChart.Data<>(date, frequency));
        }
        return series;
    }

    public static Map<LocalDateTime, Double> getMapOverTime(Map<LocalDateTime, Double> data, GraphOptions options) {
        if (options.isCumulative()) {
            convertToCumulative(data);
        }
        if (options.getStart() == null || options.getEnd() == null) {
            return data;
        }
        LinkedHashMap<LocalDateTime, Double> result = new LinkedHashMap<>();
        for (Map.Entry<LocalDateTime, Double> entry : data.entrySet()) {
            if (entry.getKey().isAfter(options.getStart()) && entry.getKey().isBefore(options.getEnd())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


    public static <T extends ModelInstance> Map<LocalDateTime, Double> hourlyCount(Stream<T> stream) throws ExecutionException, InterruptedException {
        LinkedHashMap<LocalDateTime, Double> results = stream.map((ModelInstance value) -> value.getDate().toLocalDate().atTime(value.getDate().getHour(), 0, 0))
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, reducing(0D, e -> 1D, Double::sum)));

        return results;
    }

    public static <T extends ModelInstance> Map<LocalDateTime, Double> dailyCount(Stream<T> stream) throws ExecutionException, InterruptedException {
        LinkedHashMap<LocalDateTime, Double> results = stream.map((ModelInstance value) -> value.getDate().toLocalDate().atTime(0, 0, 0))
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, reducing(0D, e -> 1D, Double::sum)));

        return results;
    }

    public static <T extends ModelInstance> Map<LocalDateTime, Double> weeklyCount(Stream<T> stream) throws ExecutionException, InterruptedException {
        WeekFields weekFields = WeekFields.of(Locale.UK);
        TemporalField woy = weekFields.weekOfWeekBasedYear();
        LinkedHashMap<LocalDateTime, Double> results = stream.map((ModelInstance value) -> value.getDate().toLocalDate()
                .withYear(value.getDate().getYear())
                .with(weekFields.weekOfYear(), value.getDate().get(woy))
                .with(weekFields.dayOfWeek(), 1).atTime(0, 0, 0))
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, reducing(0D, e -> 1D, Double::sum)));

        return results;
    }

    public static <T extends ModelInstance> Map<LocalDateTime, Double> monthlyCount(Stream<T> stream) throws ExecutionException, InterruptedException {
        LinkedHashMap<LocalDateTime, Double> results = stream.map((ModelInstance value) -> value.getDate().toLocalDate().withDayOfMonth(1).atTime(0, 0, 0))
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, reducing(0D, e -> 1D, Double::sum)));

        return results;
    }

    public static <T extends ModelInstance> Map<LocalDateTime, Double> yearlyCount(Stream<T> stream) throws ExecutionException, InterruptedException {
        return stream.map((ModelInstance click) -> click.getDate().toLocalDate().withMonth(1).withDayOfMonth(1).atTime(0, 0, 0))
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, reducing(0D, e -> 1D, Double::sum)));
    }

    public static <T extends ModelInstanceWithCost> Map<LocalDateTime, Double> hourlyCost(Stream<T> stream) throws ExecutionException, InterruptedException {
        return stream.map(value -> new Cost(value.getDate().toLocalDate().atTime(value.getDate().getHour(), 0, 0), value.getCost()))
                .collect(Collectors.groupingBy(Cost::getDate, LinkedHashMap::new, Collectors.summingDouble(Cost::getCost)));
    }

    public static <T extends ModelInstanceWithCost> Map<LocalDateTime, Double> dailyCost(Stream<T> stream) throws ExecutionException, InterruptedException {
        return stream.map(value -> new Cost(value.getDate().toLocalDate().atTime(0, 0, 0), value.getCost()))
                .collect(Collectors.groupingBy(Cost::getDate, LinkedHashMap::new, Collectors.summingDouble(Cost::getCost)));
    }

    public static <T extends ModelInstanceWithCost> Map<LocalDateTime, Double> weeklyCost(Stream<T> stream) throws ExecutionException, InterruptedException {
        WeekFields weekFields = WeekFields.of(Locale.UK);
        TemporalField woy = weekFields.weekOfWeekBasedYear();
        return stream.map(value -> new Cost(value.getDate().toLocalDate()
                .withYear(value.getDate().getYear())
                .with(weekFields.weekOfYear(), value.getDate().get(woy))
                .with(weekFields.dayOfWeek(), 1).atTime(0, 0, 0), value.getCost()))
                .collect(Collectors.groupingBy(Cost::getDate, LinkedHashMap::new, Collectors.summingDouble(Cost::getCost)));
    }

    public static <T extends ModelInstanceWithCost> Map<LocalDateTime, Double> monthlyCost(Stream<T> stream) throws ExecutionException, InterruptedException {
        return stream.map(value -> new Cost(value.getDate().toLocalDate().withDayOfMonth(1).atTime(0, 0, 0), value.getCost()))
                .collect(Collectors.groupingBy(Cost::getDate, LinkedHashMap::new, Collectors.summingDouble(Cost::getCost)));
    }

    public static <T extends ModelInstanceWithCost> Map<LocalDateTime, Double> yearlyCost(Stream<T> stream) throws ExecutionException, InterruptedException {
        LinkedHashMap<LocalDateTime, Double> results = stream.map(value -> new Cost(value.getDate().toLocalDate().withMonth(1).withDayOfMonth(1).atTime(0, 0, 0), value.getCost()))
                .collect(Collectors.groupingBy(Cost::getDate, LinkedHashMap::new, Collectors.summingDouble(Cost::getCost)));

        return results;
    }
}
