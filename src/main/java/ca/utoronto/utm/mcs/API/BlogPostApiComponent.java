package ca.utoronto.utm.mcs.API;

import ca.utoronto.utm.mcs.DaggerModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = DaggerModule.class)
public interface BlogPostApiComponent {
    public BlogPostApi buildAPI();
}

