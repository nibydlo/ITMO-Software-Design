import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class EventsStatisticImplTest {

    private final static Instant NOW = Instant.parse("2018-11-30T18:35:24.00Z");

    @Test
    void initTest() {
        Clock clock = new SettableClock(NOW);
        EventsStatistic eventsStatistic = new EventsStatisticImpl(clock);
        assertEquals(0, eventsStatistic.getEventStatisticByName("qwe"));
    }

    @Test
    void allActualTest() {
        Clock clock = new SettableClock(NOW);
        EventsStatistic eventsStatistic = new EventsStatisticImpl(clock);
        for (int i = 0; i < 60; i++) {
            eventsStatistic.incEvent("a");
        }
        assertEquals(1, eventsStatistic.getEventStatisticByName("a"));
    }

    @Test
    void noneActualTest() {
        SettableClock clock = new SettableClock(NOW.minus(2, ChronoUnit.HOURS));
        EventsStatistic eventsStatistic = new EventsStatisticImpl(clock);
        for (int i = 0; i < 60; i++) {
            eventsStatistic.incEvent("a");
        }
        clock.setNow(NOW);
        assertEquals(0, eventsStatistic.getEventStatisticByName("a"));
    }

    @Test
    void halfActualTest() {
        SettableClock clock = new SettableClock(NOW.minus(2, ChronoUnit.HOURS));
        EventsStatistic eventsStatistic = new EventsStatisticImpl(clock);
        for (int i = 0; i < 30; i++) {
            eventsStatistic.incEvent("a");
        }
        clock.setNow(clock.now().plus(90, ChronoUnit.MINUTES));
        for (int i = 0; i < 30; i++) {
            eventsStatistic.incEvent("a");
        }
        clock.setNow(clock.now().plus(30, ChronoUnit.MINUTES));
        assertEquals(0.5, eventsStatistic.getEventStatisticByName("a"));
    }
}
