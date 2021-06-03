package model.models;

import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import model.utilities.DateService;

import java.time.LocalDateTime;

public class ServerLog extends ModelInstance {
    public static final SimpleAttribute<ServerLog, LocalDateTime> ENTRY_DATE = new SimpleAttribute<ServerLog, LocalDateTime>("entryDate") {
        @Override
        public LocalDateTime getValue(ServerLog serverLog, QueryOptions queryOptions) {
            return serverLog.getDate();
        }
    };
    public static final SimpleAttribute<ServerLog, String> ID = new SimpleAttribute<ServerLog, String>("id") {
        @Override
        public String getValue(ServerLog serverLog, QueryOptions queryOptions) {
            return serverLog.getId();
        }
    };
    public static final SimpleAttribute<ServerLog, LocalDateTime> EXIT_DATE = new SimpleAttribute<ServerLog, LocalDateTime>("exitDate") {
        @Override
        public LocalDateTime getValue(ServerLog serverLog, QueryOptions queryOptions) {
            return DateService.parseDate(serverLog.exitDate);
        }
    };
    public static final SimpleAttribute<ServerLog, Integer> VIEWS = new SimpleAttribute<ServerLog, Integer>("views") {
        @Override
        public Integer getValue(ServerLog serverLog, QueryOptions queryOptions) {
            return serverLog.views;
        }
    };
    public static final SimpleAttribute<ServerLog, Boolean> SUCCESSFUL_CONVERSION = new SimpleAttribute<ServerLog, Boolean>("successfulConversion") {
        @Override
        public Boolean getValue(ServerLog serverLog, QueryOptions queryOptions) {
            return serverLog.successfulConversion;
        }
    };
    private final String exitDate;
    private final Integer views;
    private final Boolean successfulConversion;

    public ServerLog(String[] line) {
        super(line[0], line[1]);
        this.exitDate = line[2];
        this.views = Integer.parseInt(line[3]);
        this.successfulConversion = line[4].equals("Yes");
    }

    public LocalDateTime getExitDate() {
        return DateService.parseDate(exitDate);
    }

    public boolean hasExitDate() {
        return !exitDate.equals("n/a");
    }

    public String toString() {
        return getDate() + " | " + getId() + " | " + exitDate + " | " + views + " | " + successfulConversion;
    }

    public Integer getViews() {
        return views;
    }

    public Boolean getSuccessfulConversion() {
        return successfulConversion;
    }
}
