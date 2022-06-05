package at.haha007.edenconfig.core;

/**
 * Used to create instances of configuration entries
 *
 * @param <C> Configuration
 * @param <T> Instantiable
 */
public interface InstanceCreator<C, T> {
    Class<T> getType();

    void save(C config, T type, String key);

    T create(C config, String key);
}
