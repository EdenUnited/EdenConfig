package at.haha007.edenconfig.core;

/**
 * read values from objects and store them into a configuration
 * read values from config compound and inject them
 * @param <C> Configuration Compound
 */

public interface Configurator<C>{
    C config();
    Configurator<C> config(C t);
    void inject(Object o);
    void save(Object o);
    Configurator<C> child(String path);
}