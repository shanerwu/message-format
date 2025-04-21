package io.github.shanerwu.message.format.core;

import io.github.shanerwu.message.format.core.sample.RepeatSample;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RepeatSampleTest {

    @Test
    public void test_getLength() {
        RepeatSample sample = new RepeatSample();

        int length = sample.getLength();
        int expected = 10 + 8 + (5 * 3);
        assertEquals(expected, length);
    }

}
