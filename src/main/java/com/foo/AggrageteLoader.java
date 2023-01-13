package com.foo;

import dev.morphia.annotations.Entity;
import org.reflections.Reflections;

import java.util.Set;

public class AggrageteLoader {

    public static Set<Class<?>> loadAnnotatedWithEntity() {
        Reflections reflections = new Reflections("com.foo");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Entity.class);
        return classes;
    }
}
