package io.github.shanerwu.message.format.parser.message.sample2;

import io.github.shanerwu.message.format.core.annotation.MessageDetail;
import io.github.shanerwu.message.format.parser.message.AbstractMessageSupport;

@MessageDetail("Sample201 測試電文")
public class Sample201Response extends AbstractMessageSupport<Sample2ResponseHeader, Sample201ResponseBody> {

    public Sample201Response(String message) {
        super(message);
    }

}
