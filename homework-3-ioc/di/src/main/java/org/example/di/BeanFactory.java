package org.example.di;

import org.example.di.post_processors.PostProcessor;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class BeanFactory {
    private static final Logger log = LoggerFactory.getLogger(BeanFactory.class);
    private final Context context;
    private Set<PostProcessor> postProcessors;

    public BeanFactory(Context context) {
        postProcessors = scanPostProcessors("org.example");
        this.context = context;
    }

    public static Set<PostProcessor> scanPostProcessors(String packageName) {
        Set<PostProcessor> postProcessors = new HashSet<>();
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends PostProcessor>> processorClasses = reflections.getSubTypesOf(PostProcessor.class);

        for (Class<? extends PostProcessor> clazz : processorClasses) {
            if (!Modifier.isAbstract(clazz.getModifiers())) {
                try {
                    PostProcessor processor = clazz.getDeclaredConstructor().newInstance();
                    postProcessors.add(processor);
                } catch (Exception e) {
                    log.error("Error during scan post processors", e);

                }
            }
        }

        return postProcessors;
    }

    public <T> T createObject(Class<T> clazz) {
        try {
            final T definition = create(clazz, context);
            postProcessors.forEach(processor -> {
                try {
                    try {
                        processor.process(definition, context);
                    } catch (InvocationTargetException | InstantiationException e) {
                        throw new RuntimeException(e);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException();
                }
            });
            return definition;
        } catch (Exception e) {
            log.error("Error during create object", e);
        }
        return null;
    }

    private <T> T create(Class<T> clazz, Context context) throws NoSuchMethodException{
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                args[i] = context.getObject(parameterTypes[i]);
            }
            try {
                return (T) constructor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                continue;
            }
        }
        throw new NoSuchMethodException("Unable to find suitable constructor for class " + clazz.getName());
    }
}