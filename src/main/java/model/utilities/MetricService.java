package model.utilities;

import model.MainModel;
import model.models.GraphOptions;
import model.models.enums.BounceDefinition;

import java.util.concurrent.ExecutionException;

public class MetricService {
    public static String getMetricFromIndex(MainModel model, GraphOptions options, int index, boolean filtered) throws ExecutionException, InterruptedException {
        if (options.getStart() == null && options.getEnd() == null) {
            filtered = false;
        }
        long val = 0;
        Double dval = 0.0;
        switch (index) {
            default: // Impressions
                val = model.impressionsCount(options);
                return Long.toString(val);
            case 1: // Clicks
                val = filtered ? model.clicksCount(options) : model.clicksCount();
                return Long.toString(val);
            case 2: // Uniques
                val = filtered ? model.uniquesCount(options) : model.uniquesCount();
                return Long.toString(val);
            case 3: // Bounces
                val = model.bouncesCount(options);
                return Long.toString(val);
            case 4: // Conversions
                val = filtered ? model.conversionsCount(options) : model.conversionsCount();
                return Long.toString(val);
            case 5: // Cost
                dval = filtered ? model.totalCost(options) : model.totalCost();
                return String.format("£%.2f", dval);
            case 6: // CTR
                dval = filtered ? model.CTR(options) : model.CTR();
                return String.format("%.2f", dval);
            case 7: // CPA
                dval = filtered ? model.CPA(options) : model.CPA();
                return String.format("£%.2f", dval);
            case 8: // CPC
                dval = filtered ? model.CPC(options) : model.CPC();
                return String.format("£%.2f", dval);
            case 9: // CPM
                dval = filtered ? model.CPM(options) : model.CPM();
                return String.format("£%.2f", dval);
            case 10: // Bounce rate
                dval = model.bounceRate(options);
                return String.format("%.2f", dval);
        }
    }
}
