package at.haha007.edenconfig.paper.yaml;

import at.haha007.edenconfig.core.Configurator;
import at.haha007.edenconfig.core.InstanceCreator;
import at.haha007.edenconfig.core.InstanceCreatorMap;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class NoArgsInstanceCreator<T> implements InstanceCreator<ConfigurationSection, T> {

    private final Class<T> clazz;
    private final Configurator<ConfigurationSection> configurator;

    public NoArgsInstanceCreator(Class<T> clazz, InstanceCreatorMap<ConfigurationSection> configurator) {
        this.clazz = clazz;
        this.configurator = YamlConfigurator.builder().instanceCreators(configurator).config(new YamlConfiguration()).build();
    }

    public Class<T> getType() {
        return clazz;
    }

    public void save(ConfigurationSection config, T type, String key) {
        config = config.createSection(key);
        configurator.config(config).save(type);
    }

    @SneakyThrows
    public T create(ConfigurationSection config, String key) {
        config = config.getConfigurationSection(key);
        if (config == null)
            return null;
        T t = clazz.getConstructor().newInstance();
        configurator.config(config).inject(t);
        return t;
    }
}
