package at.haha007.edenconfig.paper.yaml;

import at.haha007.edenconfig.core.InstanceCreator;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;

public class EnumInstanceCreator<T extends Enum<T>> implements InstanceCreator<ConfigurationSection, T> {

    private final Class<T> clazz;

    public EnumInstanceCreator(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Class<T> getType() {
        return clazz;
    }

    @SneakyThrows
    public void save(ConfigurationSection config, T type, String key) {

        config.set(key, type.name());
    }

    @SneakyThrows
    public T create(ConfigurationSection config, String key) {
        key = config.getString(key);
        if (key == null)
            return null;
        return T.valueOf(clazz, key.toUpperCase());
    }
}
