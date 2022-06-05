package at.haha007.edenconfig.core;

import lombok.Builder;
import lombok.NonNull;

@Builder
public class ConfigurationPipeline<C> {
    @NonNull
    private final Configurator<C> configurator;
    @NonNull
    private final ConfigurationLoader<C> loader;

    public void inject(Object root){
        C config = loader.read();
        configurator.config(config);
        configurator.inject(root);
    }

    public void save(Object root){
        configurator.save(root);
        C config = configurator.config();
        loader.write(config);
    }
}
