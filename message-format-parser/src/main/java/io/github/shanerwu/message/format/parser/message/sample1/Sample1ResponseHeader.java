package io.github.shanerwu.message.format.parser.message.sample1;

import io.github.shanerwu.message.format.core.MessageFormatSupport;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;

public class Sample1ResponseHeader extends MessageFormatSupport {

    @MessageFormat(length = 4, description = "流水號")
    private String serialNo;

    @MessageFormat(length = 14, description = "處理日期時間")
    private String systemDate;

    @MessageFormat(length = 5, description = "電文長度")
    private int responseLength;

}
