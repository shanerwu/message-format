package io.github.shanerwu.message.format.parser.message.sample1;

import java.util.List;

import io.github.shanerwu.message.format.core.MessageFormatSupport;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;

public class Sample101RequestBody extends MessageFormatSupport {

    @MessageFormat(length = 1, description = "來源")
    private String source;

    @MessageFormat(length = 3, description = "識別碼清單數")
    private int uuidListCount;

    @MessageFormat(length = 108, reference = "uuidListCount", description = "識別碼清單")
    private List<Sample101RequestBodyDetail> uuidList;

}
