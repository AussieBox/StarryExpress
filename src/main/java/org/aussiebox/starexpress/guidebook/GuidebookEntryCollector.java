package org.aussiebox.starexpress.guidebook;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.StarryExpress;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class GuidebookEntryCollector implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return StarryExpress.id("guidebook_entry_collector");
    }

    @Override
    public void reload(ResourceManager manager) {
        for (Identifier id : manager.findResources("guidebook_entries", path -> path.getPath().endsWith(".json")).keySet()) {
            try (InputStream stream = manager.getResource(id).orElseThrow().getInputStream()) {
                JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
                if (jsonObject.has("")) {

                }
            } catch (Exception e) {
                StarryExpress.LOGGER.error("Error occurred while loading guidebook entry json {}", id.toString(), e);
            }
        }
    }
}
