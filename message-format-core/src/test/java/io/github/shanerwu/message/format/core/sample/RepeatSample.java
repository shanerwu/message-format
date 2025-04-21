package io.github.shanerwu.message.format.core.sample;

import io.github.shanerwu.message.format.core.MessageFormatSupport;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RepeatSample extends MessageFormatSupport {

    @MessageFormat(length = 10)
    private String code;

    @MessageFormat(length = 8)
    private String date;

    // 固定重複3次，無論實際有幾個項目
    @MessageFormat(repeat = 3)
    private List<RepeatItem> items;
}
