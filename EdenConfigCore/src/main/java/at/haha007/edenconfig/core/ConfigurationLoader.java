package at.haha007.edenconfig.core;

public interface ConfigurationLoader<C>{
    C read();

    void write(C config);
}
