import org.junit.Test;

import static org.junit.Assert.*;

public class DuplicateAssertTest {
    @Test
    public void duplicateAssertTrue1() {
        assertEquals(true, true);
        assertEquals(true, true);
    }

    @Test
    public void duplicateAssertTrue2() {
        assertNotEquals(true, true);
        assertNotEquals(true, true);
    }

    @Test
    public void duplicateAssertTrue3() {
        assertTrue(true);
        assertTrue(true);
    }
}
