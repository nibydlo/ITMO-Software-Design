import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class EventsStatisticImpl implements EventsStatistic {
    private Map<String, ArrayDeque<Instant>> stat = new HashMap<>();
    private final Clock clock;

    public EventsStatisticImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void incEvent(String name) {
        if (!stat.containsKey(name)) {
            stat.put(name, new ArrayDeque<>());
        }
        stat.get(name).add(clock.now());
    }

    @Override
    public Double getEventStatisticByName(String event) {
        if (!stat.containsKey(event)) {
            return 0.0;
        }
        removeOldStatByName(event);
        return stat.get(event).size() / 60.0;
    }

    private void removeOldStatByName(String event) {
        while (!stat.get(event).isEmpty() && stat.get(event).getFirst().compareTo(clock.now().minus(1, ChronoUnit.HOURS)) < 0) {
            stat.get(event).poll();
        }
    }

    @Override
    public Map<String, Double> getAllEventStatistic() {
        removeAllOldStat();
        Map<String, Double> res = new HashMap<>();
        stat.keySet().forEach(event -> res.put(event, getEventStatisticByName(event)));
        return res;
    }

    private void removeAllOldStat() {
        stat.keySet().forEach(this::removeOldStatByName);
    }

    @Override
    public void printStatistic() {
        removeAllOldStat();
        stat.keySet().forEach(event -> {
            System.out.println("Event name: " + event + " RPM: " + getEventStatisticByName(event));
        });
    }
}
