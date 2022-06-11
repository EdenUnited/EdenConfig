package at.haha007.edenconfig.paper.yaml;

import at.haha007.edenconfig.core.ConfigInjected;
import at.haha007.edenconfig.core.Configurator;
import at.haha007.edenconfig.core.InstanceCreatorMap;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Builder
@Accessors(fluent = true)
public class YamlConfigurator implements Configurator<ConfigurationSection> {
    @Getter
    @Setter
    private ConfigurationSection config;

    @NonNull
    private InstanceCreatorMap<ConfigurationSection> instanceCreators;


    public void inject(Object o) {
        streamAnnotatedFields(o.getClass()).forEach(field -> injectField(field, o));
    }

    private void injectField(Field field, Object o) {
        ConfigInjected annotation = field.getAnnotation(ConfigInjected.class);
        String key = annotation.name().isEmpty() ? field.getName() : annotation.name();
        Class<?> type = field.getType();
        if (instanceCreators.containsKey(type)) {
            field.setAccessible(true);
            try {
                field.set(o, instanceCreators.get(type).create(config, key));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            field.setAccessible(true);
            child(field.getName()).inject(field.get(o));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void save(Object o) {
        streamAnnotatedFields(o.getClass()).forEach(field -> saveField(field, o));
    }

    private <T> void saveField(Field field, Object o) {
        ConfigInjected annotation = field.getAnnotation(ConfigInjected.class);
        String key = annotation.name().isEmpty() ? field.getName() : annotation.name();
        //noinspection unchecked
        Class<T> type = (Class<T>) field.getType();
        if (instanceCreators.containsKey(type)) {
            field.setAccessible(true);
            try {
                //noinspection unchecked
                instanceCreators.get(type).save(config, (T) field.get(o), key);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            field.setAccessible(true);
            child(field.getName()).save(field.get(o));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Configurator<ConfigurationSection> child(String path) {
        ConfigurationSection cfg = config.contains(path) ? config.getConfigurationSection(path) : config.createSection(path);
        return builder()
                .config(cfg == null ? new YamlConfiguration() : cfg)
                .instanceCreators(instanceCreators)
                .build();
    }

    private Stream<Field> streamAnnotatedFields(Class<?> clazz) {
        return getSuperClasses(clazz).stream()
                .map(Class::getDeclaredFields)
                .flatMap(Arrays::stream)
                .filter(f -> f.isAnnotationPresent(ConfigInjected.class));
    }

    private List<Class<?>> getSuperClasses(Class<?> clazz) {
        List<Class<?>> classes = new ArrayList<>();
        while (clazz != null) {
            classes.add(clazz);
            clazz = clazz.getSuperclass();
        }
        return classes;
    }
}
