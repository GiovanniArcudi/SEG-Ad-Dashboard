package model.models;

import model.models.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GraphOptions {

    // Default values
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean isCumulative = true;
    private TimeInterval granularity = TimeInterval.DAY;
    private BounceDefinition bounceDefinition = BounceDefinition.SINGLE_PAGE;
    private Double bounceThreshold = 10.0;
    private final Set<AgeBracket> ageFilter = new HashSet<>();
    private final Set<Context> contextFilter = new HashSet<>();
    private final Set<Gender> genderFilter = new HashSet<>();
    private final Set<Income> incomeFilter = new HashSet<>();

    public GraphOptions() {
        initialiseSets();
    }

    private void initialiseSets() {
        ageFilter.addAll(Arrays.asList(AgeBracket.Above_54, AgeBracket.Between_25_34, AgeBracket.Between_35_44, AgeBracket.Between_45_54, AgeBracket.Under_25));
        contextFilter.addAll(Arrays.asList(Context.Social_media, Context.Hobbies, Context.Travel, Context.Blog, Context.News, Context.Shopping));
        genderFilter.addAll(Arrays.asList(Gender.Female, Gender.Male));
        incomeFilter.addAll(Arrays.asList(Income.High, Income.Medium, Income.Low));
    }

    public BounceDefinition getBounceDefinition() {
        return bounceDefinition;
    }

    public void setBounceDefinition(BounceDefinition bounceDefinition) {
        this.bounceDefinition = bounceDefinition;
    }

    public Double getBounceThreshold() {
        return bounceThreshold;
    }

    public void setBounceThreshold(Double bounceThreshold) {
        this.bounceThreshold = bounceThreshold;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        if (start != null) {
            this.start = start.atTime(0, 0, 0);
        }
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        if (end != null) {
            this.end = end.atTime(23, 59, 59);
        }
    }

    public boolean isCumulative() {
        return isCumulative;
    }

    public void setCumulative(boolean cumulative) {
        isCumulative = cumulative;
    }

    public TimeInterval getGranularity() {
        return granularity;
    }

    public void setGranularity(TimeInterval granularity) {
        this.granularity = granularity;
    }

    public void addAgeBracket(AgeBracket bracket) {
        ageFilter.add(bracket);
    }

    public void removeAgeBracket(AgeBracket bracket) {
        ageFilter.remove(bracket);
    }

    public Set<AgeBracket> getAgeFilter() {
        return ageFilter;
    }

    public void addContext(Context context) {
        contextFilter.add(context);
    }

    public void removeContext(Context context) {
        contextFilter.remove(context);
    }

    public Set<Context> getContextFilter() {
        return contextFilter;
    }

    public void addGender(Gender gender) {
        genderFilter.add(gender);
    }

    public void removeGender(Gender gender) {
        genderFilter.remove(gender);
    }

    public Set<Gender> getGenderFilter() {
        return genderFilter;
    }

    public void addIncome(Income income) {
        incomeFilter.add(income);
    }

    public void removeIncome(Income income) {
        incomeFilter.remove(income);
    }

    public Set<Income> getIncomeFilter() {
        return incomeFilter;
    }
}
