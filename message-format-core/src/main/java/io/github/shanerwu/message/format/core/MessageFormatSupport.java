package io.github.shanerwu.message.format.core;

import io.github.shanerwu.message.format.core.annotation.MessageFormat;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/**
 * MessageFormatSupport，定長字串 format
 *
 * @since 2017-07-22
 * @author KBZ
 */
public abstract class MessageFormatSupport {

    @SuppressWarnings("unchecked")
    public int getLength() {
        Class<?> clazz = this.getClass();
        int length = 0;
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                MessageFormat annotation = field.getDeclaredAnnotation(MessageFormat.class);
                if (annotation == null) {
                    continue;
                }

                boolean isCollection = Collection.class.isAssignableFrom(field.getType());
                boolean autoCalculateLength = annotation.length() == -1 && isCollection;

                if (annotation.repeat() > 0 && StringUtils.isBlank(annotation.reference())) {
                    if (autoCalculateLength) {
                        Class<? extends MessageFormatSupport> elementType = this.getElementType(field);
                        MessageFormatSupport elementInstance = elementType.newInstance();
                        length += annotation.repeat() * elementInstance.getLength();
                    } else {
                        length += annotation.repeat() * annotation.length();
                    }
                }
                else if (annotation.repeat() == -1 && StringUtils.isNotBlank(annotation.reference())) {
                    Collection<MessageFormatSupport> details = (Collection<MessageFormatSupport>) field.get(this);
                    boolean isSimpleCollection = true;
                    if (details != null && details.size() != 0) {
                        for (Field detailField : details.iterator().next().getClass().getDeclaredFields()) {
                            if (Collection.class.isAssignableFrom(detailField.getType())) {
                                isSimpleCollection = false;
                                break;
                            }
                        }
                    }

                    Field referenceField = clazz.getDeclaredField(annotation.reference());
                    referenceField.setAccessible(true);

                    if (isSimpleCollection) {
                        if (autoCalculateLength) {
                            if (details != null && details.size() > 0) {
                                int elementLength = details.iterator().next().getLength();
                                length += (int) referenceField.get(this) * elementLength;
                            } else {
                                Class<? extends MessageFormatSupport> elementType = this.getElementType(field);
                                MessageFormatSupport elementInstance = elementType.newInstance();
                                length += (int) referenceField.get(this) * elementInstance.getLength();
                            }
                        } else {
                            length += (int) referenceField.get(this) * annotation.length();
                        }
                    } else {
                        if (details != null && !details.isEmpty()) {
                            length += details.stream()
                                    .mapToInt(MessageFormatSupport::getLength)
                                    .reduce((prev, next) -> prev + next)
                                    .getAsInt();
                        }
                    }
                }
                else {
                    length += annotation.length();
                }
            } catch (Exception e) {
                String errorMsg = new StringBuilder()
                        .append("解析 ").append(clazz).append(' ').append(field.getName()).append(" 發生錯誤")
                        .toString();
                throw new IllegalArgumentException(errorMsg, e);
            }
        }
        return length;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends MessageFormatSupport> getElementType(Field field) {
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        return (Class<? extends MessageFormatSupport>) genericType.getActualTypeArguments()[0];
    }
}