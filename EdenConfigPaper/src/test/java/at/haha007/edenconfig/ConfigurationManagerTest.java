package at.haha007.edenconfig;

import at.haha007.edenconfig.core.ConfigInjected;
import at.haha007.edenconfig.core.Configurator;
import at.haha007.edenconfig.paper.PaperConfigurationManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigurationManagerTest {

    JavaPlugin plugin;
    PaperConfigurationManager manager;

    @BeforeAll
    void init() {
        plugin = Mockito.mock(JavaPlugin.class);
    }

    @BeforeEach
    void testCreation() {
        manager = PaperConfigurationManager.builder()
                .plugin(plugin)
                .build();
        Assertions.assertNotNull(manager);
    }

    @Test
    void testPluginNonNull() {
        try {
            PaperConfigurationManager.builder().build();
            Assertions.fail();
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    void simpleWrite() {
        class TestClass {
            @ConfigInjected
            final Object inner = new Object() {
                @ConfigInjected
                Integer i = 1;
                @ConfigInjected
                String s = "s";
                @ConfigInjected
                Double d = 1.0;
            };
        }
        TestClass instance = new TestClass();
        YamlConfiguration config = new YamlConfiguration();
        Configurator<ConfigurationSection> configurator = manager.createYamlConfigurator();
        configurator.config(config);
        configurator.save(instance);
        Assertions.assertEquals(1, config.getInt("inner.i"));
        Assertions.assertEquals("s", config.getString("inner.s"));
        Assertions.assertEquals(1.0, config.getDouble("inner.d"));
    }

    @Test
    void simpleIntInject() {
        class TestClass {
            @ConfigInjected
            final Object inner = new Object() {
                @ConfigInjected
                Integer i;
                @ConfigInjected
                String s;
                @ConfigInjected
                Double d;
            };
        }
        TestClass instance = new TestClass();
        ConfigurationSection config = new YamlConfiguration();
        config.set("inner.i", 1);
        config.set("inner.s", "s");
        config.set("inner.d", 1.0);
        Configurator<ConfigurationSection> configurator = manager.createYamlConfigurator();
        configurator.config(config);
        configurator.inject(instance);
        try {
            Assertions.assertEquals(1, instance.inner.getClass().getDeclaredField("i").get(instance.inner));
            Assertions.assertEquals("s", instance.inner.getClass().getDeclaredField("s").get(instance.inner));
            Assertions.assertEquals(1.0, instance.inner.getClass().getDeclaredField("d").get(instance.inner));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Assertions.fail(e);
        }
    }
}
