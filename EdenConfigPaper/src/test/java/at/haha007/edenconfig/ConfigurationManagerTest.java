package at.haha007.edenconfig;

import at.haha007.edenconfig.core.ConfigInjected;
import at.haha007.edenconfig.core.ConfigurationPipeline;
import at.haha007.edenconfig.core.Configurator;
import at.haha007.edenconfig.core.InstanceCreator;
import at.haha007.edenconfig.paper.PaperConfigurationManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.File;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigurationManagerTest {

    JavaPlugin plugin;
    PaperConfigurationManager manager;
    File targetFolder = new File("target");

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
    void annotationNameTest() {
        class A {
            @ConfigInjected
            int i = 2;
        }
        class B {
            @ConfigInjected(name = "i")
            int j = 0;
        }
        A a = new A();
        B b = new B();

        YamlConfiguration config = new YamlConfiguration();
        Configurator<ConfigurationSection> configurator = manager.createYamlConfigurator();
        configurator.config(config);
        configurator.save(a);
        configurator.inject(b);

        Assertions.assertEquals(2, b.j);
    }

    @Test
    void inheritanceTest() {
        abstract class A {
            @ConfigInjected
            int i = 2;
        }
        class B extends A {
            @ConfigInjected
            int j = 2;
        }
        B b = new B();

        YamlConfiguration config = new YamlConfiguration();
        Configurator<ConfigurationSection> configurator = manager.createYamlConfigurator();
        configurator.config(config);
        configurator.inject(b);

        Assertions.assertEquals(0, b.j);
        Assertions.assertEquals(0, b.i);
    }


    @Test
    void injectNullTest() {
        class Test {
            @ConfigInjected
            List<String> list = List.of("a", "b");
            @ConfigInjected
            int i = 2;
        }
        Test test = new Test();
        YamlConfiguration config = new YamlConfiguration();
        Configurator<ConfigurationSection> configurator = manager.createYamlConfigurator();
        configurator.config(config);
        configurator.inject(test);

        Assertions.assertNull(test.list);
        Assertions.assertEquals(0, test.i);
    }

    @Test
    void listTest() {
        class Test {
            @ConfigInjected
            List<String> list = List.of("a", "b");
        }
        Test test = new Test();
        YamlConfiguration config = new YamlConfiguration();
        Configurator<ConfigurationSection> configurator = manager.createYamlConfigurator();
        configurator.config(config);
        configurator.save(test);

        Assertions.assertEquals("a", config.getStringList("list").get(0));

        test.list = null;
        configurator.inject(test);

        Assertions.assertEquals("a", test.list.get(0));
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
        //noinspection unused
        class TestClass {
            @ConfigInjected
            final Object inner = new Object() {
                @ConfigInjected
                final Integer i = 1;
                @ConfigInjected
                final String s = "s";
                @ConfigInjected
                final Double d = 1.0;
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

    @Test
    void customInstanceCreator() {
        class Test {
            String s = "s";
            int i = 1;
        }

        class Outer {
            @ConfigInjected
            final Test test = new Test();
        }

        Outer o = new Outer();

        InstanceCreator<ConfigurationSection, Test> testInstanceCreator = new InstanceCreator<>() {
            public Class<Test> getType() {
                return Test.class;
            }

            public void save(ConfigurationSection config, Test t, String key) {
                config.set(key + ".index", t.i);
                config.set(key + ".string", t.s);
            }

            public Test create(ConfigurationSection config, String key) {
                Test t = new Test();
                t.i = config.getInt(key + ".index");
                t.s = config.getString(key + ".string");
                return t;
            }
        };

        PaperConfigurationManager.GLOBAL_YAML_INSTANCE_CREATOR_MAP.add(testInstanceCreator);

        Configurator<ConfigurationSection> c = manager.createYamlConfigurator();
        c.config(new YamlConfiguration());
        c.save(o);

        //check saving
        Assertions.assertEquals(1, c.config().getInt("test.index"));
        Assertions.assertEquals("s", c.config().getString("test.string"));

        o = new Outer();
        o.test.i = -1;
        o.test.s = "-1";
        c.inject(o);

        //check loading
        Assertions.assertEquals(1, o.test.i);
        Assertions.assertEquals("s", o.test.s);
    }

    @Test
    void filePipelineTest() {
        class Test {
            String s = "s";
            int i = 1;
        }

        class Outer {
            @ConfigInjected
            final Test test = new Test();
        }

        Outer o = new Outer();

        InstanceCreator<ConfigurationSection, Test> testInstanceCreator = new InstanceCreator<>() {
            public Class<Test> getType() {
                return Test.class;
            }

            public void save(ConfigurationSection config, Test t, String key) {
                config.set(key + ".index", t.i);
                config.set(key + ".string", t.s);
            }

            public Test create(ConfigurationSection config, String key) {
                Test t = new Test();
                t.i = config.getInt(key + ".index");
                t.s = config.getString(key + ".string");
                return t;
            }
        };

        PaperConfigurationManager.GLOBAL_YAML_INSTANCE_CREATOR_MAP.add(testInstanceCreator);

        File file = new File(targetFolder, "tests.yml");
        ConfigurationPipeline<ConfigurationSection> pipeline = manager.createYamlPipeline(file);
        pipeline.save(o);

        //check saving
        Assertions.assertEquals(1, YamlConfiguration.loadConfiguration(file).getInt("test.index"));
        Assertions.assertEquals("s", YamlConfiguration.loadConfiguration(file).getString("test.string"));

        o = new Outer();
        o.test.i = -1;
        o.test.s = "-1";
        pipeline.inject(o);

        //check loading
        Assertions.assertEquals(1, o.test.i);
        Assertions.assertEquals("s", o.test.s);
    }
}
