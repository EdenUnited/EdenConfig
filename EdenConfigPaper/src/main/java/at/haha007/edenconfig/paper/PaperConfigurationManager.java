package at.haha007.edenconfig.paper;

import at.haha007.edenconfig.core.ConfigurationLoader;
import at.haha007.edenconfig.core.ConfigurationPipeline;
import at.haha007.edenconfig.core.Configurator;
import at.haha007.edenconfig.core.InstanceCreatorMap;
import at.haha007.edenconfig.paper.yaml.FileYamlLoader;
import at.haha007.edenconfig.paper.yaml.YamlConfigurator;
import at.haha007.edenconfig.paper.yaml.YamlDefaultInstanceCreator;
import lombok.Builder;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;

@Builder
public class PaperConfigurationManager {

    @NonNull
    public static final InstanceCreatorMap<ConfigurationSection> GLOBAL_YAML_INSTANCE_CREATOR_MAP;

    static {
        InstanceCreatorMap<ConfigurationSection> map = new InstanceCreatorMap<>();
        map.add(new YamlDefaultInstanceCreator<>(ConfigurationSection::getString, String.class));
        map.add(new YamlDefaultInstanceCreator<>(ConfigurationSection::getInt, Integer.class));
        map.add(new YamlDefaultInstanceCreator<>(ConfigurationSection::getItemStack, ItemStack.class));
        map.add(new YamlDefaultInstanceCreator<>(ConfigurationSection::getBoolean, Boolean.class));
        map.add(new YamlDefaultInstanceCreator<>(ConfigurationSection::getColor, Color.class));
        map.add(new YamlDefaultInstanceCreator<>(ConfigurationSection::getDouble, Double.class));
        map.add(new YamlDefaultInstanceCreator<>(ConfigurationSection::getLocation, Location.class));
        map.add(new YamlDefaultInstanceCreator<>(ConfigurationSection::getVector, Vector.class));
        map.add(new YamlDefaultInstanceCreator<>(ConfigurationSection::getOfflinePlayer, OfflinePlayer.class));
        GLOBAL_YAML_INSTANCE_CREATOR_MAP = map;
    }

    @NonNull
    private final JavaPlugin plugin;
    @NonNull
    @Builder.Default
    private final InstanceCreatorMap<ConfigurationSection> yamlInstanceCreatorMap = GLOBAL_YAML_INSTANCE_CREATOR_MAP;


    public Configurator<ConfigurationSection> createYamlConfigurator() {
        return YamlConfigurator.builder()
                .instanceCreators(yamlInstanceCreatorMap)
                .config(new YamlConfiguration())
                .build();
    }


    public ConfigurationPipeline<ConfigurationSection> createYamlPipeline(File file) {
        ConfigurationLoader<ConfigurationSection> loader = FileYamlLoader.builder()
                .file(file)
                .build();
        Configurator<ConfigurationSection> configurator = createYamlConfigurator();

        return ConfigurationPipeline.<ConfigurationSection>builder()
                .loader(loader)
                .configurator(configurator)
                .build();
    }
}
