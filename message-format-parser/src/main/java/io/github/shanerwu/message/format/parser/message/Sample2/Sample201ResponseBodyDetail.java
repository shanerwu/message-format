package io.github.shanerwu.message.format.parser.message.Sample2;

import java.util.List;

import io.github.shanerwu.message.format.core.MessageFormatSupport;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;

public class Sample201ResponseBodyDetail extends MessageFormatSupport {

    @MessageFormat(length = 8, description = "日期")
    private String date;

    @MessageFormat(length = 2, description = "識別碼清單數")
    private int uuidListCount;

    @MessageFormat(length = 38, reference = "uuidListCount", description = "識別碼清單")
    private List<Sample201UuidList> uuidList;

    @MessageFormat(length = 1, description = "狀態")
    private String status;

}
