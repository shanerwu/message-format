package io.github.shanerwu.message.format.core;

import io.github.shanerwu.message.format.core.annotation.MessageFormat;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/**
 * 定長字串格式支援類別
 * 需要序列化為定長字串，或從定長字串反序列化的類別必須繼承此類別
 */
public abstract class MessageFormatSupport {

    /**
     * 計算當前物件序列化為定長字串的總長度。
     *
     * <p>透過反射遍歷當前類別中所有標記了 {@link MessageFormat#length} 的欄位，
     * 並根據註解的設定計算每個欄位的長度，最後加總得到總長度。
     *
     * @return 序列化後的定長字串總字元數
     * @throws IllegalArgumentException 如果在計算過程中發生錯誤
     */
    public int getLength() {
        Class<?> clazz = this.getClass();
        int totalLength = 0;

        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                MessageFormat annotation = field.getDeclaredAnnotation(MessageFormat.class);

                if (annotation == null) {
                    continue;
                }

                totalLength += calculateFieldLength(field, annotation);
            } catch (Exception e) {
                String errorMsg = String.format("計算 %s.%s 長度時發生錯誤",
                                                 clazz.getSimpleName(), field.getName());
                throw new IllegalArgumentException(errorMsg);
            }
        }

        return totalLength;
    }

    /**
     * 根據欄位註解計算該欄位的長度。
     *
     * <p>處理三種情況：
     * <ul>
     *   <li>固定重複次數 {@link MessageFormat#repeat} 的集合</li>
     *   <li>參考重複次數欄位 {@link MessageFormat#reference} 決定重複次數的集合</li>
     *   <li>一般欄位</li>
     * </ul>
     *
     * @param field 要計算長度的欄位
     * @param annotation 欄位上的 MessageFormat 註解
     * @return 欄位的長度
     * @throws Exception 如果計算過程中發生錯誤
     */
    private int calculateFieldLength(Field field, MessageFormat annotation) throws Exception {
        boolean isCollection = Collection.class.isAssignableFrom(field.getType());
        boolean shouldAutoCalculate = annotation.length() == -1 && isCollection;

        // 1: 固定重複次數(repeat)的集合
        if (hasFixedRepeatCount(annotation)) {
            if (shouldAutoCalculate) {
                return calculateAutoFixedRepeatLength(field, annotation);
            }
            return calculateFixedRepeatLength(annotation);
        }

        // 2: 重複次數參考值(reference)決定重複次數的集合
        if (hasReferenceBasedRepeatCount(annotation)) {
            if (shouldAutoCalculate) {
                return calculateAutoReferenceBasedLength(field, annotation);
            }
            return calculateReferenceBasedLength(field, annotation);
        }

        // 3: 一般欄位
        return annotation.length();
    }

    /**
     * 計算固定重複次數 {@link MessageFormat#repeat} 的長度。
     *
     * <p>重複次數 × 每個元素的長度。
     *
     * @param annotation 欄位上的 MessageFormat 註解
     * @return 欄位的總長度
     */
    private int calculateFixedRepeatLength(MessageFormat annotation) {
        return annotation.repeat() * annotation.length();
    }

    /**
     * 自動計算固定重複次數 {@link MessageFormat#repeat} 之集合欄位的長度。
     *
     * <p>取得集合元素類型並創建實例來計算每個元素的長度，
     * 然後乘以重複次數得到總長度。
     *
     * @param field 要計算長度的欄位
     * @param annotation 欄位上的 MessageFormat 註解
     * @return 欄位的總長度
     * @throws Exception 如果計算過程中發生錯誤
     */
    private int calculateAutoFixedRepeatLength(Field field, MessageFormat annotation) throws Exception {
        Class<? extends MessageFormatSupport> elementType = getElementType(field);
        MessageFormatSupport elementInstance = elementType.newInstance();
        return annotation.repeat() * elementInstance.getLength();
    }

    /**
     * 計算參考重複次數 {@link MessageFormat#reference} 之集合欄位的長度。
     *
     * <p>根據參考重複次數欄位的值，以及集合元素的類型來計算總長度。
     * 如果是簡單集合（元素內不包含任何集合欄位），則長度為參考重複次數欄位值 × 元素長度；
     * 如果是複雜集合（元素包含其他集合欄位），則遞迴計算每個元素的長度並加總。
     *
     * @param field 要計算長度的欄位
     * @param annotation 欄位上的 MessageFormat 註解
     * @return 欄位的總長度
     * @throws Exception 如果計算過程中發生錯誤
     */
    @SuppressWarnings("unchecked")
    private int calculateReferenceBasedLength(Field field, MessageFormat annotation) throws Exception {
        Collection<MessageFormatSupport> details = (Collection<MessageFormatSupport>) field.get(this);

        Field referenceField = this.getClass().getDeclaredField(annotation.reference());
        referenceField.setAccessible(true);
        int referenceValue = (int) referenceField.get(this);

        if (details == null || details.isEmpty()) {
            return referenceValue * annotation.length();
        }

        boolean isSimpleCollection = isSimpleCollection(details);
        if (isSimpleCollection) {
            return referenceValue * annotation.length();
        } else {
            return details.stream()
                    .mapToInt(MessageFormatSupport::getLength)
                    .sum();
        }
    }

    /**
     * 自動計算參考重複次數 {@link MessageFormat#reference} 之集合欄位的長度。
     *
     * <p>先嘗試從現有集合元素中取得單個元素的長度；
     * 如果集合為空，則創建元素類別的實例來計算長度。
     * 然後乘以參考欄位的值得到總長度。
     *
     * @param field 要計算長度的欄位
     * @param annotation 欄位上的 MessageFormat 註解
     * @return 欄位的總長度
     * @throws Exception 如果計算過程中發生錯誤
     */
    @SuppressWarnings("unchecked")
    private int calculateAutoReferenceBasedLength(Field field, MessageFormat annotation) throws Exception {
        Collection<MessageFormatSupport> details = (Collection<MessageFormatSupport>) field.get(this);

        Field referenceField = this.getClass().getDeclaredField(annotation.reference());
        referenceField.setAccessible(true);
        int referenceValue = (int) referenceField.get(this);

        int elementLength;
        if (details != null && !details.isEmpty()) {
            elementLength = details.iterator().next().getLength();
        } else {
            Class<? extends MessageFormatSupport> elementType = getElementType(field);
            MessageFormatSupport elementInstance = elementType.newInstance();
            elementLength = elementInstance.getLength();
        }

        return referenceValue * elementLength;
    }

    /**
     * 判斷是否為簡單集合（集合元素內不包含任何集合欄位）。
     *
     * <p>如果集合中的元素類別沒有任何集合類型的欄位，則視為簡單集合。
     *
     * @param details 要檢查的集合
     * @return 如果是簡單集合則返回 true，否則返回 false
     */
    private boolean isSimpleCollection(Collection<MessageFormatSupport> details) {
        if (details.isEmpty()) {
            return true;
        }
        // 取第一個元素作為範例
        MessageFormatSupport sample = details.iterator().next();
        return !hasNestedCollection(sample.getClass());
    }

    /**
     * 檢查類別是否有任何集合類型的欄位。
     *
     * @param clazz 要檢查的類別
     * @return 如果有集合類型的欄位則返回 true，否則返回 false
     */
    private boolean hasNestedCollection(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (Collection.class.isAssignableFrom(field.getType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 檢查欄位是否有固定的重複次數。
     *
     * <p>如果 repeat 值大於 0 且沒有設定 reference，則視為固定重複次數。
     *
     * @param annotation 欄位上的 MessageFormat 註解
     * @return 如果有固定重複次數則返回 true，否則返回 false
     */
    private boolean hasFixedRepeatCount(MessageFormat annotation) {
        return annotation.repeat() > 0 && StringUtils.isBlank(annotation.reference());
    }

    /**
     * 檢查欄位是否有重複次數參考欄位。
     *
     * <p>如果 repeat 值為 -1（預設值）且有設定 reference，則視為參考重複次數。
     *
     * @param annotation 欄位上的 MessageFormat 註解
     * @return 如果有重複次數參考欄位則返回 true，否則返回 false
     */
    private boolean hasReferenceBasedRepeatCount(MessageFormat annotation) {
        return annotation.repeat() == -1 && StringUtils.isNotBlank(annotation.reference());
    }

    /**
     * 取得集合欄位的元素類型。
     *
     * @param field 集合類別的欄位
     * @return 集合元素的類別
     */
    @SuppressWarnings("unchecked")
    private Class<? extends MessageFormatSupport> getElementType(Field field) {
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        return (Class<? extends MessageFormatSupport>) genericType.getActualTypeArguments()[0];
    }
}