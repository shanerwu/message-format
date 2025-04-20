package io.github.shanerwu.message.format.parser.message.sample2;

import java.util.List;

import io.github.shanerwu.message.format.core.MessageFormatSupport;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;

public class Sample201ResponseBody extends MessageFormatSupport {

    @MessageFormat(length = 2, description = "清單數")
    private int listCount;

    @MessageFormat(length = 49, reference = "listCount", description = "清單")
    private List<Sample201ResponseBodyDetail> details;

}
