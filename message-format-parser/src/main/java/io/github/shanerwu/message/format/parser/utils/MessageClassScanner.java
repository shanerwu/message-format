package io.github.shanerwu.message.format.parser.utils;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import io.github.shanerwu.message.format.core.annotation.MessageDetail;
import io.github.shanerwu.message.format.core.annotation.MessageType;

public final class MessageClassScanner {

    private static final String BASE_PACKAGE = "io.github.shanerwu.message.format.parser.message";

    private static final Map<String, Map<String, Class<?>>> MESSAGE_CLASS_MAP = new LinkedHashMap<>();

    static {
        initMessageClassMap();
    }

    private MessageClassScanner() {
    }

    private static void initMessageClassMap() {
        for (Class<?> type : getAnnotatedClasses(MessageType.class, BASE_PACKAGE)) {
            HashMap<String, Class<?>> detailMap = new LinkedHashMap<>();
            for (Class<?> detail : getAnnotatedClasses(MessageDetail.class, type.getPackage().getName())) {
                detailMap.put(detail.getAnnotation(MessageDetail.class).value(), detail);
            }
            MESSAGE_CLASS_MAP.put(type.getAnnotation(MessageType.class).value(), detailMap);
        }
    }

    public static Set<String> getTypes() {
        return new TreeSet<>(MESSAGE_CLASS_MAP.keySet());
    }

    public static Set<String> getDetails(String type) {
        return new TreeSet<>(MESSAGE_CLASS_MAP.get(type).keySet());
    }

    public static Class<?> getDetailClasses(String type, String name) {
        return MESSAGE_CLASS_MAP.get(type).get(name);
    }

    private static List<Class<?>> getAnnotatedClasses(Class<? extends Annotation> clazz, String packageName) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(clazz)
                .stream()
                .sorted(Comparator.comparing(Class::getSimpleName))
                .collect(Collectors.toList());
    }

}
