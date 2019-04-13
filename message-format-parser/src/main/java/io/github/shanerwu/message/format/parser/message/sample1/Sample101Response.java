package io.github.shanerwu.message.format.parser.message.sample1;

import io.github.shanerwu.message.format.core.annotation.MessageDetail;
import io.github.shanerwu.message.format.parser.message.AbstractMessageSupport;

@MessageDetail("Sample101 測試電文 Response")
public class Sample101Response extends AbstractMessageSupport<Sample1ResponseHeader, Sample101ResponseBody> {

    public Sample101Response(String message) {
        super(message);
    }

}
