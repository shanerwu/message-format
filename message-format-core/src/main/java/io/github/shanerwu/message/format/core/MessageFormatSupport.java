package io.github.shanerwu.message.format.core;

import java.lang.reflect.Field;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import io.github.shanerwu.message.format.core.annotation.MessageFormat;

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

                if (annotation.repeat() > 0 && StringUtils.isBlank(annotation.reference())) {
                    length += annotation.repeat() * annotation.length();
                } else if (annotation.repeat() == -1 && StringUtils.isNotBlank(annotation.reference())) {
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
                        length += (int) referenceField.get(this) * annotation.length();
                    } else {
                        length += details.stream()
                                .mapToInt(MessageFormatSupport::getLength).reduce((prev, next) -> prev + next).getAsInt();
                    }
                } else {
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

}
