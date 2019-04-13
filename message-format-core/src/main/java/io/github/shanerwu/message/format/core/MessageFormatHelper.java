package io.github.shanerwu.message.format.core;


import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.github.shanerwu.message.format.core.annotation.FormatAlign;
import io.github.shanerwu.message.format.core.annotation.MessageFormat;
import lombok.extern.slf4j.Slf4j;

/**
 * MessageFormatHelper
 *
 * @since 2017-07-22
 * @author KBZ
 */
@Slf4j
public final class MessageFormatHelper {

    private static final Map<Type, ValueHandler<?>> HANDLER_MAP = new HashMap<>();

    static {
        HANDLER_MAP.put(String.class, String::substring);
        HANDLER_MAP.put(Integer.class, (text, beginIndex, endIndex) -> Integer.valueOf(text.substring(beginIndex, endIndex)));
        HANDLER_MAP.put(int.class, HANDLER_MAP.get(Integer.class));
    }

    private MessageFormatHelper() {
    }

    public static String stringify(MessageFormatSupport format) {
        Class<?> clazz = format.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        for (Field f : fields) {
            f.setAccessible(true);
            MessageFormat annotation = f.getDeclaredAnnotation(MessageFormat.class);
            if (annotation == null) {
                continue;
            }
            sb.append(getFieldText(f, format));
        }
        return sb.toString();
    }

    private static String getFieldText(Field f, MessageFormatSupport messageFormat) {
        Type type = f.getGenericType();
        if (String.class.equals(type)) {
            return formatString(f, messageFormat);
        }
        if (Integer.class.equals(type) || int.class.equals(type)) {
            return formatNumber(f, messageFormat);
        }
        if (type instanceof ParameterizedType) {
            return formatParameterizedType(f, messageFormat, type);
        }
        throw new IllegalArgumentException("Unknown Type " + type);
    }

    @SuppressWarnings("unchecked")
    private static String formatParameterizedType(Field f, MessageFormatSupport messageFormat, Type type) {
        try {
            StringBuilder sb = new StringBuilder();
            int repeatNumber = getRepeatNumber(f, messageFormat);
            Collection<MessageFormatSupport> details = (Collection<MessageFormatSupport>) f.get(messageFormat);
            if (details == null) {
                details = new ArrayList<>(0);
                f.set(messageFormat, details);
            }
            details.forEach(detail -> sb.append(stringify(detail)));

            int emptyCount = repeatNumber - details.size();
            if (emptyCount > 0) {
                Class<MessageFormatSupport> detailClazz = (Class<MessageFormatSupport>) ((ParameterizedType) type).getActualTypeArguments()[0];
                MessageFormatSupport empty = newInstance(detailClazz);
                for (int i = 0; i < emptyCount; i++) {
                    sb.append(stringify(empty));
                }
            }
            return sb.toString();
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalArgumentException(f.getName() + "解析錯誤");
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends MessageFormatSupport> int getRepeatNumber(Field field, MessageFormatSupport format) {
        Class<T> clazz = (Class<T>) format.getClass();
        MessageFormat messageFormat = field.getDeclaredAnnotation(MessageFormat.class);

        if (messageFormat.repeat() != -1) {
            return messageFormat.repeat();
        } else if (StringUtils.isNotBlank(messageFormat.reference())) {
            try {
                Field referenceField = clazz.getDeclaredField(messageFormat.reference());
                referenceField.setAccessible(true);
                return (int) referenceField.get(format);
            } catch (NoSuchFieldException | SecurityException
                    | IllegalArgumentException | IllegalAccessException e) {
                String errorMsg = "Read field " + field.getName() + " referenceField failed.";
                throw new IllegalArgumentException(errorMsg);
            }
        } else {
            return 0;
        }
    }

    private static String formatNumber(Field f, Object o) {
        try {
            Object reflectValue = f.get(o);
            Integer value = reflectValue == null ? Integer.valueOf(0) : (Integer) reflectValue;
            MessageFormat annotation = f.getDeclaredAnnotation(MessageFormat.class);
            char paddingWord = annotation.paddingWord() == ' ' ? '0' : annotation.paddingWord();
            if (annotation.align() == FormatAlign.LEFT) {
                return StringUtils.rightPad(String.valueOf(value), annotation.length(), paddingWord);
            }
            return StringUtils.leftPad(String.valueOf(value), annotation.length(), paddingWord);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static String formatString(Field f, Object o) {
        try {
            Object reflectValue = f.get(o);
            String value = reflectValue == null ? StringUtils.EMPTY : (String) reflectValue;
            MessageFormat annotation = f.getDeclaredAnnotation(MessageFormat.class);
            if (annotation.align() == FormatAlign.RIGHT) {
                return StringUtils.leftPad(value, annotation.length(), annotation.paddingWord());
            }
            return StringUtils.rightPad(value, annotation.length(), annotation.paddingWord());
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends MessageFormatSupport> T parse(String text, Class<T> clazz) {
        T bean = newInstance(clazz);
        int current = 0;
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                MessageFormat messageFormat = field.getDeclaredAnnotation(MessageFormat.class);
                if (messageFormat == null) {
                    continue;
                }

                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    int repeatNumber = getRepeatNumber(field, bean);
                    List<T> list = new ArrayList<>(repeatNumber);
                    field.set(bean, list);
                    Class<T> detailClazz = (Class<T>) Class.forName(((ParameterizedType) type)
                            .getActualTypeArguments()[0].getTypeName());
                    // 檢查是否還有 detail
                    boolean hasDetail = hasDetail(detailClazz);
                    for (int i = 0; i < repeatNumber; i++) {
                        if (hasDetail) {
                            T detail = parse(text.substring(current), detailClazz);
                            list.add(detail);
                            current += stringify(detail).length();
                        } else {
                            list.add(parse(text.substring(current, current + messageFormat.length()), detailClazz));
                            current += messageFormat.length();
                        }
                    }
                } else {
                    Object value = HANDLER_MAP.get(type).doHandle(text, current, current + messageFormat.length());
                    if (value instanceof String && messageFormat.trimToEmpty()) {
                        value = ((String) value).trim();
                    }
                    field.set(bean, value);
                    current += messageFormat.length();
                }
            } catch (Exception e) {
                log.error("parse field {} occurred exception - {}", field, e.getMessage());
                throw new IllegalArgumentException(e);
            }
        }
        return bean;
    }

    private static boolean hasDetail(Class<?> clazz) {
        boolean hasDetail = false;
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            MessageFormat messageFormat = field.getDeclaredAnnotation(MessageFormat.class);
            if (messageFormat == null) {
                continue;
            }

            Type type = field.getGenericType();
            if (type instanceof ParameterizedType) {
                hasDetail = true;
                log.debug("Class {} has another detail - {}", clazz.getCanonicalName(), field);
                break;
            }
        }
        return hasDetail;
    }

    private static <T extends MessageFormatSupport> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @FunctionalInterface
    private interface ValueHandler<T> {
        T doHandle(String text, int beginIndex, int endIndex);
    }
}
