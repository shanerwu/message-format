package io.github.shanerwu.message.format.parser.message.sample1;

import io.github.shanerwu.message.format.core.MessageFormatSupport;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;

public class Sample1RequestHeader extends MessageFormatSupport {

    @MessageFormat(length = 8, description = "電文代碼")
    private String msgCode;

    @MessageFormat(length = 14, description = "時間戳記")
    private String systemDate;

    @MessageFormat(length = 5, description = "電文長度")
    private int requestLength;

}
