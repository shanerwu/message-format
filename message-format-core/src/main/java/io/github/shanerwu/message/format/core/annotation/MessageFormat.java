package io.github.shanerwu.message.format.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * MessageFormat
 *
 * @since 2017-07-22
 * @author KBZ
 */
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageFormat {

	/**
	 * 定長文字長度, 若標註型態為 {@link List} 上則代表一筆 detail 的總長度
	 */
	int length() default -1;

	/**
	 * 文字所在位置
	 */
	FormatAlign align() default FormatAlign.DEFAULT;

	/**
	 * 重複次數參考欄位, ex: POS003 票號(tktNo) 需根據 退票張數(cancelCount) 重複出現
	 */
	String reference() default StringUtils.EMPTY;

	/**
	 * 重複次數，即使設定 {@link MessageFormat#reference()} 仍會以此欄位為基準重複出現
	 */
	int repeat() default -1;

	/**
	 * 補齊文字
	 */
	char paddingWord() default ' ';

	/**
	 * 中文欄位名稱
	 */
	String description() default StringUtils.EMPTY;

	/**
	 * 是否執行 {@link String#trim()}，僅 {@link String} 型態有效
	 */
	boolean trimToEmpty() default true;
}
