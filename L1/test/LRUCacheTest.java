import static org.junit.jupiter.api.Assertions.assertEquals;

import com.krylov.software_design.lab_1.LRUCache;
import org.junit.jupiter.api.Test;


class LRUCacheTest {
    private final static int NONEXISTENT_ID = 123;
    private final static int ID_1 = 1;
    private final static int VALUE_1 = 11;
    private final static int ID_2 = 2;
    private final static int VALUE_2 = 22;
    private final static int ID_3 = 3;
    private final static int VALUE_3 = 33;
    private final static int CAPACITY_1 = 1;
    private final static int CAPACITY_2 = 2;
    private final static int NO_SUCH_KEY_RESPONSE = -1;

    @Test
    void initTest() {
        LRUCache cache = new LRUCache();
        assertEquals(NO_SUCH_KEY_RESPONSE, cache.get(NONEXISTENT_ID), "get for nonexistent id should return -1");
    }

    @Test
    void getExistentTest() {
        LRUCache cache = new LRUCache();
        cache.put(ID_1, VALUE_1);
        assertEquals(VALUE_1, cache.get(ID_1), "get for existent id without overflow should return existent value");
    }

    @Test
    void overFlowTest() {
        LRUCache cache = new LRUCache(CAPACITY_1);
        cache.put(ID_1, VALUE_1);
        cache.put(ID_2, VALUE_2);
        assertEquals(
                NO_SUCH_KEY_RESPONSE,
                cache.get(ID_1),
                "values more than capacity, so get for least recent element should return -1"
        );
        assertEquals(VALUE_2, cache.get(ID_2), "the most recent element should exist and value should be correct");
    }

    @Test
    void getInfluenceTest() {
        LRUCache cache = new LRUCache(CAPACITY_2);
        cache.put(ID_1, VALUE_1);
        cache.put(ID_2, VALUE_2);
        cache.get(ID_1);
        cache.put(ID_3, VALUE_3);
        assertEquals(VALUE_1, cache.get(ID_1), "id 1 should exist because of recent get request");
        assertEquals(
                NO_SUCH_KEY_RESPONSE,
                cache.get(ID_2),
                "there should be no value 2 because of lack of capacity and because id 2 was least recent when id 3 was added"
        );
        assertEquals(VALUE_3, cache.get(ID_3), "id 3 should exist and value should be correct");
    }
}
