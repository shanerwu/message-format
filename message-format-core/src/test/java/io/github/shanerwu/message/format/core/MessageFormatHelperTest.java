package io.github.shanerwu.message.format.core;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

public class MessageFormatHelperTest {

    @Test
    public void parse_to_object_if_given_class_and_string_are_valid() {
        String dateTimeString = "20190413153000";
        SampleDateTime sample = MessageFormatHelper.parse(dateTimeString, SampleDateTime.class);
        assertEquals("201904131530", getDateTimeString(sample));
    }

    @Test
    public void stringify_from_object_if_given_object_is_valid() {
        SampleDateTime sampleObject = mockSampleDateTimeObject();
        String dateTimeString = MessageFormatHelper.stringify(sampleObject);
        assertEquals("20190413153000", dateTimeString);
    }

    @Test
    public void parse_to_object_if_given_string_has_details() {
        String dateTimeString = "201904131530012019041315300120190413153000";
        SampleDateTime sample = MessageFormatHelper.parse(dateTimeString, SampleDateTime.class);
        for (SampleDateTime dateTime : sample.getList()) {
            assertEquals("201904131530", getDateTimeString(dateTime));
        }
    }

    @Test
    public void stringify_from_object_if_given_object_has_details() {
        SampleDateTime sample = mockSampleDateTimeObject();
        sample.setListCount(1);
        sample.setList(Collections.singletonList(mockSampleDateTimeObject()));

        String dateTimeString = MessageFormatHelper.stringify(sample);
        assertEquals("2019041315300120190413153000", dateTimeString);
    }

    private String getDateTimeString(SampleDateTime sample) {
        return sample.getYear() +
                sample.getDate() +
                sample.getTime();
    }

    private SampleDateTime mockSampleDateTimeObject() {
        SampleDateTime sample = new SampleDateTime();
        sample.setYear("2019");
        sample.setDate("0413");
        sample.setTime("1530");
        return sample;
    }

}