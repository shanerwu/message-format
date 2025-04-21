package io.github.shanerwu.message.format.core.sample;

import io.github.shanerwu.message.format.core.MessageFormatSupport;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RepeatItem extends MessageFormatSupport {

    @MessageFormat(length = 2)
    private String id;

    @MessageFormat(length = 3)
    private int value;

}
