package model.models;

import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import model.models.enums.AgeBracket;
import model.models.enums.Context;
import model.models.enums.Gender;
import model.models.enums.Income;
import java.time.LocalDateTime;


public class Impression extends ModelInstanceWithCost {

    public static final SimpleAttribute<Impression, String> UNIQUE_ID = new SimpleAttribute<Impression, String>("uuid") {
        @Override
        public String getValue(Impression impression, QueryOptions queryOptions) {
            return impression.getUuid();
        }
    };

    public static final SimpleAttribute<Impression, LocalDateTime> DATE = new SimpleAttribute<Impression, LocalDateTime>("date") {
        @Override
        public LocalDateTime getValue(Impression impression, QueryOptions queryOptions) {
            return impression.getDate();
        }
    };

    public static final SimpleAttribute<Impression, String> ID = new SimpleAttribute<Impression, String>("id") {
        @Override
        public String getValue(Impression impression, QueryOptions queryOptions) {
            return impression.getId();
        }
    };

    public static final SimpleAttribute<Impression, Gender> GENDER = new SimpleAttribute<Impression, Gender>("gender") {
        @Override
        public Gender getValue(Impression impression, QueryOptions queryOptions) {
            return impression.gender;
        }
    };

    public static final SimpleAttribute<Impression, AgeBracket> AGE_BRACKET = new SimpleAttribute<Impression, AgeBracket>("ageBracket") {
        @Override
        public AgeBracket getValue(Impression impression, QueryOptions queryOptions) {
            return impression.ageBracket;
        }
    };

    public static final SimpleAttribute<Impression, Income> INCOME = new SimpleAttribute<Impression, Income>("impressionCost") {
        @Override
        public Income getValue(Impression impression, QueryOptions queryOptions) {
            return impression.income;
        }
    };

    public static final SimpleAttribute<Impression, Context> CONTEXT = new SimpleAttribute<Impression, Context>("impressionCost") {
        @Override
        public Context getValue(Impression impression, QueryOptions queryOptions) {
            return impression.context;
        }
    };

    public static final SimpleAttribute<Impression, Double> COST = new SimpleAttribute<Impression, Double>("impressionCost") {
        @Override
        public Double getValue(Impression impression, QueryOptions queryOptions) {
            return impression.getCost();
        }
    };

    private final Gender gender;
    private final AgeBracket ageBracket;
    private final Income income;
    private final Context context;

    public Impression(String[] line) {
        super(line[0], line[1], line[6]);
        this.gender = Gender.fromIdentifier(line[2]);
        this.ageBracket = AgeBracket.fromIdentifier(line[3]);
        this.income = Income.fromIdentifier(line[4]);
        this.context = Context.fromIdentifier(line[5]);
    }

    public String toString() {
        return getDate() + " | " + getId() + " | " + gender + " | " + ageBracket + " | " + income + " | " + context + " | " + getCost();
    }

    public Gender getGender() {
        return gender;
    }

    public AgeBracket getAgeBracket() {
        return ageBracket;
    }

    public Income getIncome() {
        return income;
    }

    public Context getContext() {
        return context;
    }
}








