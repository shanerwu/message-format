package io.github.shanerwu.message.format.parser.message.sample1;

import io.github.shanerwu.message.format.core.annotation.MessageDetail;
import io.github.shanerwu.message.format.parser.message.AbstractMessageSupport;

@MessageDetail("Sample101 測試電文 Request")
public class Sample101Request extends AbstractMessageSupport<Sample1RequestHeader, Sample101RequestBody> {

    public Sample101Request(String message) {
        super(message);
    }

}
