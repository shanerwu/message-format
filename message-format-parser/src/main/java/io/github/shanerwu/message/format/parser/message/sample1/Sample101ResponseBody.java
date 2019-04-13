package io.github.shanerwu.message.format.parser.message.sample1;

import io.github.shanerwu.message.format.core.MessageFormatSupport;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;

public class Sample101ResponseBody extends MessageFormatSupport {

    @MessageFormat(length = 1, description = "成功/失敗")
    private String result;

    @MessageFormat(length = 3, description = "失敗原因訊息代碼")
    private String reason;

}
