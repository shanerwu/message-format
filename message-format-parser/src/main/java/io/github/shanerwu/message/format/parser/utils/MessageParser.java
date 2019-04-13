package io.github.shanerwu.message.format.parser.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.github.shanerwu.message.format.core.MessageFormatSupport;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;
import io.github.shanerwu.message.format.parser.message.AbstractMessageSupport;
import io.github.shanerwu.message.format.parser.message.MessageData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MessageParser {

    private MessageParser() {
    }

    public static List<MessageData> parse(Class<? extends AbstractMessageSupport> clazz, String message) throws Exception {
        try {
            Constructor<? extends AbstractMessageSupport> constructor = clazz.getConstructor(String.class);
            AbstractMessageSupport messageSupport = constructor.newInstance(message);
            return parse(messageSupport);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    private static List<MessageData> parse(AbstractMessageSupport messageSupport) throws IllegalAccessException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.putAll(invokeField(messageSupport.getHeader()));
        map.putAll(invokeField(messageSupport.getBody()));
        return getMessageDataList(map);
    }

    @SuppressWarnings("unchecked")
    private static List<MessageData> getMessageDataList(Map<String, Object> map) {
        List<MessageData> messageDataList = new ArrayList<>();
        map.forEach((description, message) -> {
            if (message instanceof Collection<?>) {
                MessageData messageData = new MessageData(description, StringUtils.EMPTY);
                doGetDetail(messageData, (List<? extends MessageFormatSupport>) message);
                messageDataList.add(messageData);
            } else {
                messageDataList.add(new MessageData(description, message.toString()));
            }
        });
        return messageDataList;
    }

    @SuppressWarnings("unchecked")
    private static void doGetDetail(MessageData messageData, List<? extends MessageFormatSupport> details) {
        List<MessageData> detailListRoot = new ArrayList<>();
        int detailCount = 0;
        for (MessageFormatSupport detail : details) {
            List<MessageData> detailList = new ArrayList<>();
            MessageData detailCountList = new MessageData(messageData.getDescription() +
                    "（" + ++detailCount + "）", StringUtils.EMPTY);
            invokeField(detail).forEach((description, message) -> {
                if (message instanceof Collection<?>) {
                    MessageData msgDataDetail = new MessageData(description, StringUtils.EMPTY);
                    doGetDetail(msgDataDetail, (List<? extends MessageFormatSupport>) message);
                    detailList.add(msgDataDetail);
                } else {
                    detailList.add(new MessageData(description, message.toString()));
                }
            });
            detailCountList.setDetails(detailList);
            detailListRoot.add(detailCountList);
        }
        messageData.setDetails(detailListRoot);
    }

    private static Map<String, Object> invokeField(MessageFormatSupport obj) {
        Map<String, Object> messageFieldMap = new LinkedHashMap<>();
        try {
            Class<? extends MessageFormatSupport> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String description = field.getAnnotation(MessageFormat.class).description();
                Object value = field.get(obj);
                if (!StringUtils.isBlank(description)) {
                    messageFieldMap.put(description, value);
                } else {
                    messageFieldMap.put(field.getName(), value);
                }
            }
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
        return messageFieldMap;
    }

}
