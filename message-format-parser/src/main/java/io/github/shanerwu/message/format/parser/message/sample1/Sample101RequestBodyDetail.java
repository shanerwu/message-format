package io.github.shanerwu.message.format.parser.message.sample1;

import io.github.shanerwu.message.format.core.MessageFormatSupport;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;

public class Sample101RequestBodyDetail extends MessageFormatSupport {

    @MessageFormat(length = 36, description = "識別碼-1")
    private String uuid1;

    @MessageFormat(length = 36, description = "識別碼-2")
    private String uuid2;

    @MessageFormat(length = 36, description = "識別碼-3")
    private String uuid3;

}
