package io.github.shanerwu.message.format.parser.message;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.github.shanerwu.message.format.core.MessageFormatHelper;
import io.github.shanerwu.message.format.core.MessageFormatSupport;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractMessageSupport<S extends MessageFormatSupport, T extends MessageFormatSupport> {

    @Getter
    private S header;

    @Getter
    private T body;

    public AbstractMessageSupport(String message) {
        Class<S> headerClass = this.getHeaderClass();
        Class<T> bodyClass = this.getBodyClass();
        int headerLength = this.getHeaderLength(headerClass);
        this.header = MessageFormatHelper.parse(message.substring(0, headerLength), headerClass);
        this.body = MessageFormatHelper.parse(message.substring(headerLength), bodyClass);
    }

    @SuppressWarnings("unchecked")
    private Class<S> getHeaderClass() {
        Class<?> clazz = getClass();
        Type genType = clazz.getGenericSuperclass();
        Type[] actualTypes = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class<S>) actualTypes[0];
    }

    @SuppressWarnings("unchecked")
    private Class<T> getBodyClass() {
        Class<?> clazz = getClass();
        Type genType = clazz.getGenericSuperclass();
        Type[] actualTypes = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class<T>) actualTypes[1];
    }

    private int getHeaderLength(Class<?> header) {
        int length = 0;
        try {
            MessageFormatSupport messageFormatSupport = (MessageFormatSupport) header.newInstance();
            length = messageFormatSupport.getLength();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
        return length;
    }

}
