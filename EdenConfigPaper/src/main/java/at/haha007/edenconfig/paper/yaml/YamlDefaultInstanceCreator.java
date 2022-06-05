package at.haha007.edenconfig.paper.yaml;

import at.haha007.edenconfig.core.InstanceCreator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

@AllArgsConstructor
public class YamlDefaultInstanceCreator<T> implements InstanceCreator<ConfigurationSection, T> {
    @NonNull
    private BiFunction<ConfigurationSection, String, T> loader;
    @NonNull
    Class<T> type;

    @NotNull
    public Class<T> getType() {
        return type;
    }

    public void save(ConfigurationSection config, T type, String key) {
        config.set(key, type);
    }

    public T create(ConfigurationSection config, String key) {
        return loader.apply(config, key);
    }
}
