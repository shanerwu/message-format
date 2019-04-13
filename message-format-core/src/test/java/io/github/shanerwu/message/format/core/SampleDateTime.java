package io.github.shanerwu.message.format.core;

import java.io.Serializable;
import java.util.List;

import io.github.shanerwu.message.format.core.annotation.MessageFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SampleDateTime extends MessageFormatSupport implements Serializable {

    @MessageFormat(length = 4)
    private String year;

    @MessageFormat(length = 4)
    private String date;

    @MessageFormat(length = 4)
    private String time;

    @MessageFormat(length = 2)
    private int listCount;

    @MessageFormat(length = 12, reference = "listCount")
    private List<SampleDateTime> list;

}
