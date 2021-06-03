package model.models;

import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import java.time.LocalDateTime;

public class Click extends ModelInstanceWithCost {

    /**
     * Attributes are used to represent fields of the class as index-able parameters for CQEngine. For reference, look at the index feature matrix here:
     * https://github.com/npgall/cqengine#feature-matrix-for-included-indexes
     */
    public static final SimpleAttribute<Click, String> UNIQUE_ID = new SimpleAttribute<Click, String>("uuid") {
        @Override
        public String getValue(Click click, QueryOptions queryOptions) {
            return click.getUuid();
        }
    };

    public static final SimpleAttribute<Click, String> ID = new SimpleAttribute<Click, String>("id") {
        @Override
        public String getValue(Click click, QueryOptions queryOptions) {
            return click.getId();
        }
    };

    public static final SimpleAttribute<Click, LocalDateTime> DATE = new SimpleAttribute<Click, LocalDateTime>("date") {
        @Override
        public LocalDateTime getValue(Click click, QueryOptions queryOptions) {
            return click.getDate();
        }
    };

    public static final SimpleAttribute<Click, Double> COST = new SimpleAttribute<Click, Double>("cost") {
        @Override
        public Double getValue(Click click, QueryOptions queryOptions) {
            return click.getCost();
        }
    };

    /**
     * The Click class represents the data model for a click, which is a single line of a click log CSV.
     *
     * @param line a CSV line
     */
    public Click(String[] line) {
        super(line[0], line[1], line[2]);
    }

    public String toString() {
        return getDate() + ", " + getId() + ", " + getCost();
    }
}
