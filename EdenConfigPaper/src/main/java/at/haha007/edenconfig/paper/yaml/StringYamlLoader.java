package at.haha007.edenconfig.paper.yaml;

import at.haha007.edenconfig.core.ConfigurationLoader;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

@Builder
public class StringYamlLoader implements ConfigurationLoader<ConfigurationSection> {

    @NonNull
    @Getter
    @Setter
    private String string;

    @Override
    public ConfigurationSection read() {
        YamlConfiguration cfg = new YamlConfiguration();
        try {
            cfg.loadFromString(string);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        return cfg;
    }

    @Override
    public void write(ConfigurationSection config) {
        if (!(config instanceof YamlConfiguration yaml))
            throw new RuntimeException("Cannot save config, config must be of type YamlConfiguration");

        string = yaml.saveToString();
    }
}
