import model.MainModel;
import model.models.GraphOptions;
import model.models.enums.AgeBracket;
import model.models.enums.Context;
import model.models.enums.Gender;
import model.models.enums.Income;
import model.services.ClickService;
import model.services.ImpressionService;
import model.services.ServerLogService;
import org.junit.Test;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class TestFilter {
    public MainModel mainModel = new MainModel();
    public ImpressionService impressionService = new ImpressionService("src/test/resources/example-dataset/impression_log.csv");
    public ClickService clickService = new ClickService("src/test/resources/example-dataset/click_log.csv");
    public ServerLogService serverLogService = new ServerLogService("src/test/resources/example-dataset/server_log.csv");

    /*
     * DATE RANGE FILTER
     */
    @Test
    public void testDateRange() {
        GraphOptions go = new GraphOptions();
        GraphOptions graphOptions1 = new GraphOptions();
        LocalDate localDate = LocalDate.of(2015, 1, 1);
        LocalDate localDate1 = LocalDate.of(2015, 2, 27);
        graphOptions1.setStart(localDate);
        graphOptions1.setEnd(localDate1);

        // Test for all dates if the count is correct
        assertEquals(mainModel.impressionsCount(graphOptions1), impressionService.getCount());
        assertEquals(mainModel.clicksCount(graphOptions1), clickService.getCount());
        assertEquals(mainModel.uniquesCount(graphOptions1), clickService.getUniques());
        assertEquals(mainModel.conversionsCount(graphOptions1), serverLogService.getConversions());
        assertEquals(mainModel.bouncesCount(graphOptions1), serverLogService.getBounceStream(go).count());
        assertEquals(mainModel.totalCost(graphOptions1), (impressionService.getCost() + clickService.getCost()), 0.00001);
        assertEquals(mainModel.CTR(graphOptions1), clickService.getCount() / (double) impressionService.getCount(), 0.00001);
        assertEquals(mainModel.CPA(graphOptions1), ((clickService.getCost() + impressionService.getCost()) / (double) serverLogService.getConversions()), 0.00001);
        assertEquals(mainModel.CPC(graphOptions1), (clickService.getCost() + impressionService.getCost()) / clickService.getCount(), 0.00001);
        assertEquals(mainModel.CPM(graphOptions1), (clickService.getCost() + impressionService.getCost()) / ((double) impressionService.getCount() / 1000), 0.00001);
        assertEquals(mainModel.bounceRate(graphOptions1), serverLogService.getBounceStream(go).count() / (double) clickService.getCount(), 0.000001);

        // Manually test for fixed dates if the output is correct
        GraphOptions graphOptions2 = new GraphOptions();
        LocalDate localDate2 = LocalDate.of(2015, 1, 4);
        LocalDate localDate3 = LocalDate.of(2015, 2, 24);
        graphOptions2.setStart(localDate2);
        graphOptions2.setEnd(localDate3);
        assertEquals(mainModel.impressionsCount(graphOptions2), 129);
        assertEquals(mainModel.clicksCount(graphOptions2), 45);
        assertEquals(mainModel.uniquesCount(graphOptions2), 45);
        assertEquals(mainModel.conversionsCount(graphOptions2), 2);
        assertEquals(mainModel.bouncesCount(graphOptions2), 18);
        assertEquals(mainModel.totalCost(graphOptions2), 252.72477, 0.0001);
        assertEquals(mainModel.CTR(graphOptions2), 0.3488, 0.0001);
        assertEquals(mainModel.CPA(graphOptions2), 126.362387, 0.0001);
        assertEquals(mainModel.CPC(graphOptions2), 5.6161, 0.0001);
        assertEquals(mainModel.CPM(graphOptions2), 1959.1067, 0.0001);
        assertEquals(mainModel.bounceRate(graphOptions2), 0.4, 0.0001);
    }

    /*
     * GENDER FILTER
     */
    @Test
    public void testGenderFilter() {

        // Manual tests only for impression_log
        GraphOptions graphOptions = new GraphOptions();
        Gender genderF = Gender.Female;
        Gender genderM = Gender.Male;
        graphOptions.removeGender(genderF);
        assertEquals(mainModel.impressionsCount(graphOptions), 82);
        graphOptions.addGender(genderF);
        graphOptions.removeGender(genderM);
        assertEquals(mainModel.impressionsCount(graphOptions), 61);
        graphOptions.removeGender(genderF);
        assertEquals(mainModel.impressionsCount(graphOptions), 0);
        graphOptions.addGender(genderF);
        graphOptions.addGender(genderM);
        assertEquals(mainModel.impressionsCount(graphOptions), 143);
    }

    /*
     * AGE FILTER
     */
    @Test
    public void testAgeFilter() {

        // Manual tests for the age
        GraphOptions graphOptions = new GraphOptions();
        AgeBracket ageU25 = AgeBracket.Under_25;
        AgeBracket ageU34 = AgeBracket.Between_25_34;
        AgeBracket ageU44 = AgeBracket.Between_35_44;
        AgeBracket ageU54 = AgeBracket.Between_45_54;
        AgeBracket ageA54 = AgeBracket.Above_54;
        graphOptions.removeAgeBracket(ageA54);
        assertEquals(mainModel.impressionsCount(graphOptions), 121);
        graphOptions.addAgeBracket(ageA54);
        assertEquals(mainModel.impressionsCount(graphOptions), 143);
        graphOptions.removeAgeBracket(ageU25);
        graphOptions.removeAgeBracket(ageU34);
        graphOptions.removeAgeBracket(ageU44);
        assertEquals(mainModel.impressionsCount(graphOptions), 73);
        graphOptions.removeAgeBracket(ageU54);
        assertEquals(mainModel.impressionsCount(graphOptions), 22);
        graphOptions.removeAgeBracket(ageA54);
        assertEquals(mainModel.impressionsCount(graphOptions), 0);
    }

    /*
     * INCOME FILTER
     */
    @Test
    public void testIncomeFilter() {

        GraphOptions graphOptions = new GraphOptions();
        Income lowIncome = Income.Low;
        Income mediumIncome = Income.Medium;
        Income highIncome = Income.High;
        graphOptions.removeIncome(lowIncome);
        assertEquals(mainModel.impressionsCount(graphOptions), 128);
        graphOptions.removeIncome(mediumIncome);
        assertEquals(mainModel.impressionsCount(graphOptions), 67);
        graphOptions.removeIncome(highIncome);
        assertEquals(mainModel.impressionsCount(graphOptions), 0);
        graphOptions.addIncome(mediumIncome);
        assertEquals(mainModel.impressionsCount(graphOptions), 61);
        graphOptions.addIncome(lowIncome);
        assertEquals(mainModel.impressionsCount(graphOptions), 76);
    }

    @Test
    public void testContextFilter() {

        GraphOptions graphOptions = new GraphOptions();
        Context contextShopping = Context.Shopping;
        Context contextNews = Context.News;
        Context contextBlog = Context.Blog;
        Context contextTravel = Context.Travel;
        Context contextSocialM = Context.Social_media;
        Context contextHobbies = Context.Hobbies;
        graphOptions.removeContext(contextBlog);
        graphOptions.removeContext(contextHobbies);
        graphOptions.removeContext(contextSocialM);
        assertEquals(mainModel.impressionsCount(graphOptions), 65);
        graphOptions.removeContext(contextTravel);
        graphOptions.removeContext(contextNews);
        assertEquals(mainModel.impressionsCount(graphOptions), 27);
        graphOptions.removeContext(contextShopping);
        assertEquals(mainModel.impressionsCount(graphOptions), 0);
        graphOptions.addContext(contextShopping);
        graphOptions.addContext(contextBlog);
        graphOptions.addContext(contextTravel);
        assertEquals(mainModel.impressionsCount(graphOptions), 54);
        graphOptions.addContext(contextHobbies);
        graphOptions.addContext(contextNews);
        graphOptions.addContext(contextSocialM);
        assertEquals(mainModel.impressionsCount(graphOptions), 143);
    }
}
