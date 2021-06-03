package model.models.enums;

import model.models.ModelInstance;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import model.models.ModelInstanceWithCost;

import static java.util.stream.Collectors.reducing;

public enum Metric {
    CLICKS,
    IMPRESSIONS,
    UNIQUES,
    CONVERSIONS,
    BOUNCES,
    COST,
    CTR,
    CPA,
    CPC,
    CPM,
    BOUNCE_RATE,
    CLICK_COST_HISTOGRAM;

    public static Metric fromString(String s) {
        switch (s) {
            case "Impressions":
                return IMPRESSIONS;
            case "Clicks":
                return CLICKS;
            case "Uniques":
                return UNIQUES;
            case "Conversions":
                return CONVERSIONS;
            case "Bounces":
                return BOUNCES;
            case "Cost":
                return COST;
            case "CTR":
                return CTR;
            case "CPA":
                return CPA;
            case "CPC":
                return CPC;
            case "CPM":
                return CPM;
            case "Bounce Rate":
                return BOUNCE_RATE;
            case "Click Cost Histogram":
                return CLICK_COST_HISTOGRAM;
            default:
                System.err.println("Unrecognised metric: " + s);
                return CLICKS;
        }
    }

    /**
     * Different metrics must be counted in different ways. Cumulating cost requires a collector on the cost attribute, whereas impressions or clicks might be counted.
     *
     * @param <T> A metric with a date, that has the getDate() method to return a LocalDateTime
     * @return A collector to be used to collect a stream (provide cumulative data)
     */
    public static <T extends ModelInstance> Collector<T, ?, ?> getCollectorFromMetric(Metric m) {
        switch (m) {
            default:
                return reducing(0D, e -> 1D, Double::sum);
            case COST:
            case CPA:
            case CPC:
            case CPM:
                return (Collector<T, ?, ?>) Collectors.summingDouble(ModelInstanceWithCost::getCost);
        }
    }
}
