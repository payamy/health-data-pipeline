package com.payamy.etl.execute;

import com.payamy.etl.annotation.After;
import com.payamy.etl.annotation.Before;
import com.payamy.etl.annotation.ETL;
import com.payamy.etl.annotation.Component;
import org.reflections.Reflections;
import static org.reflections.scanners.Scanners.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Executor {

    public static void run(String packageName) throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            InstantiationException {
        for (Iterator<Class<?>> it = getClassesIterator(packageName);
             it.hasNext(); ) {

            Class<?> cl = it.next();
            Method[] methods = cl.getMethods();

            Set<Method> etlMethods = new HashSet<>();
            Set<Method> beforeMethods = new HashSet<>();
            Set<Method> afterMethods = new HashSet<>();

            for (Method m: methods) {
                int annotations = 0;
                if (m.isAnnotationPresent(ETL.class)) {
                    etlMethods.add(m);
                    annotations++;
                }
                if (m.isAnnotationPresent(Before.class)) {
                    beforeMethods.add(m);
                    annotations++;
                }
                if (m.isAnnotationPresent(After.class)) {
                    afterMethods.add(m);
                    annotations++;
                }
                if (annotations > 1) {
                    throw new RuntimeException(
                            "A method has more than one annotation"
                    );
                }
            }

            Object obj = cl.getConstructor().newInstance();

            if (beforeMethods.size() > 1) {
                throw new RuntimeException(
                        "Cannot have more than one method annotated with 'Before' within a class"
                );
            }
            if (afterMethods.size() > 1) {
                throw new RuntimeException(
                        "Cannot have more than one method annotated with 'After' within a class"
                );
            }
            for (Method m: beforeMethods) {
                m.invoke(obj);
            }
            for (Method m: etlMethods) {
                m.invoke(obj);
            }
            for (Method m: afterMethods) {
                m.invoke(obj);
            }
        }
    }

    private static Iterator<Class<?>> getClassesIterator( String packageName ) {
        Reflections reflections = new Reflections(packageName);
        return reflections.get(TypesAnnotated.with(Component.class).asClass()).iterator();
    }
}
