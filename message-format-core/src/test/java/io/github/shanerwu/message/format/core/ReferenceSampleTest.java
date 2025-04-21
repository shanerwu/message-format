package io.github.shanerwu.message.format.core;

import io.github.shanerwu.message.format.core.sample.ReferenceSample;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReferenceSampleTest {

    @Test
    public void test_getLength() {
        ReferenceSample sample = new ReferenceSample();
        int referenceCount = 3;
        sample.setReferenceCount(referenceCount);

        int length = sample.getLength();
        int expected = 10 + 2 + referenceCount * (2 + 3);
        assertEquals(expected, length);
    }

}
