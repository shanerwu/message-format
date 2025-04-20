package io.github.shanerwu.message.format.parser.message.sample2;

import io.github.shanerwu.message.format.core.MessageFormatSupport;
import io.github.shanerwu.message.format.core.annotation.FormatAlign;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;

public class Sample201UuidList extends MessageFormatSupport {

    @MessageFormat(length = 2, paddingWord = '0', align = FormatAlign.RIGHT, description = "類型")
    private String type;

    @MessageFormat(length = 36, description = "識別碼")
    private String uuid;

}
