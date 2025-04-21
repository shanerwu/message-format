package io.github.shanerwu.message.format.core.sample;

import io.github.shanerwu.message.format.core.MessageFormatSupport;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReferenceSample extends MessageFormatSupport {

    @MessageFormat(length = 10)
    private String referenceId;

    @MessageFormat(length = 2)
    private int referenceCount;

    // 不指定 length，由系統自動計算
    @MessageFormat(reference = "referenceCount")
    private List<ReferenceItem> items;

}
