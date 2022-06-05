package at.haha007.edenconfig.paper.yaml;

import at.haha007.edenconfig.core.ConfigurationLoader;
import lombok.Builder;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Builder
public class YamlConfigLoader implements ConfigurationLoader<ConfigurationSection> {

    @NonNull
    private final File file;

    @Override
    public ConfigurationSection read() {
        return YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void write(ConfigurationSection config) {
        if(!(config instanceof YamlConfiguration yaml))
            throw new RuntimeException("Cannot save config, config must be of type YamlConfiguration");
        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Cannot save config, IOException while saving");
        }
    }
}
