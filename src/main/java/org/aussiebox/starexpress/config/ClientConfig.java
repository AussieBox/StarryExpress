package org.aussiebox.starexpress.config;

import blue.endless.jankson.Comment;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.SectionHeader;
import io.wispforest.owo.config.annotation.Sync;

@Sync(Option.SyncMode.INFORM_SERVER)
@Config(name = "starexpress-client", wrapperName = "StarryExpressClientConfig")
public class ClientConfig {

    @SectionHeader("role_config")

    @Comment("Config options related to the Allergic role.")
    @Nest public AllergicClientConfig allergicConfig = new AllergicClientConfig();

    public static class AllergicClientConfig {
        @Comment("Removes the chance of being poisoned with the Allergic modifier.")
        public boolean noPoison = false;
    }

}
