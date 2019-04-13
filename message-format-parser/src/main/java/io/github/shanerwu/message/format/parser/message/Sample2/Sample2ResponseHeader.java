package io.github.shanerwu.message.format.parser.message.Sample2;

import io.github.shanerwu.message.format.core.MessageFormatSupport;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;

public class Sample2ResponseHeader extends MessageFormatSupport {

    @MessageFormat(length = 4, description = "流水號")
    private String serialNo;

    @MessageFormat(length = 14, description = "系統處理日期時間")
    private String systemDate;

    @MessageFormat(length = 4, description = "電文長度")
    private int responseLength;

}
