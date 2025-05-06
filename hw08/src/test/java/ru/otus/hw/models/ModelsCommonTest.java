package ru.otus.hw.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ModelsCommonTest {

    private static Set<Class<?>> documentClasses;

    @BeforeAll
    static void setUpAll() {
        var reflections = new Reflections("ru.otus.hw.models");
        documentClasses = reflections.getTypesAnnotatedWith(Document.class);
    }

    @ParameterizedTest
    @MethodSource("getDocuments")
    void shouldBeNoDBRefInModelClasses(Class<?> documentClass) {
        var dbRefExists = Arrays.stream(documentClass.getDeclaredFields())
                .anyMatch(f -> f.isAnnotationPresent(DBRef.class));
        assertThat(dbRefExists)
                .withFailMessage("В моделях MongoDB не должны использоваться связи DBRef")
                .isFalse();
    }

    @ParameterizedTest
    @MethodSource("getDocuments")
    void shouldAvoidNestedDocumentsInModelClasses(Class<?> documentClass) {
        boolean nestedDocumentExists = Arrays.stream(documentClass.getDeclaredFields())
                .filter(f -> !f.getType().isPrimitive())
                .anyMatch(f -> documentClasses.contains(f.getType()));
        assertThat(nestedDocumentExists)
                .withFailMessage("В моделях MongoDB не должны использоваться вложенные документы (nested documents)")
                .isFalse();
    }

    @ParameterizedTest
    @MethodSource("getDocuments")
    void shouldUseEmbeddedRelationshipsCorrectly(Class<?> documentClass) {
        var relationsEntries = findAllRelationsEntry(documentClass);
        var hasIncorrectEmbeddedRelationships = relationsEntries.entrySet().stream()
                .anyMatch(relationEntry -> {
                    var relatedClass = relationEntry.getKey();
                    var field = relationEntry.getValue();
                    var isEmbedded = !field.isAnnotationPresent(DBRef.class) && !Collection.class.isAssignableFrom(field.getType());
                    return !isEmbedded;
                });
        assertThat(hasIncorrectEmbeddedRelationships)
                .withFailMessage("В моделях MongoDB связи должны быть правильно настроены в виде embedded")
                .isFalse();
    }

    private static Stream<Arguments> getDocuments() {
        return documentClasses.stream().map(Arguments::of);
    }

    private <T> T getAnnotationArgumentValue(Field field, String argumentName, Class<T> returnType) {
        return Arrays.stream(field.getAnnotations())
                .flatMap(a -> Arrays.stream(a.getClass().getDeclaredMethods()).map(m -> Map.entry(m, a)))
                .filter(e -> e.getKey().getName().equals(argumentName))
                .map(e -> ReflectionUtils.invoke(e.getKey(), e.getValue()))
                .map(returnType::cast)
                .findFirst().orElse(null);
    }

    private Map<? extends Class<?>, Field> findAllRelationsEntry(Class<?> documentClass) {
        return Arrays.stream(documentClass.getDeclaredFields())
                .filter(f -> !f.getType().isPrimitive())
                .map(f -> Map.entry(f, fieldToClass(f)))
                .filter(e -> documentClasses.contains(e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    private Class<?> fieldToClass(Field field) {
        var className = field.getType().getName();
        if (Collection.class.isAssignableFrom(field.getType())) {
            className = field.getGenericType().getTypeName()
                    .split("<")[1].split(">")[0];
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}