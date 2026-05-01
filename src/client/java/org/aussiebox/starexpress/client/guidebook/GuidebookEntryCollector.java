package org.aussiebox.starexpress.client.guidebook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.agmas.harpymodloader.modifiers.HMLModifiers;
import org.agmas.harpymodloader.modifiers.Modifier;
import org.apache.logging.log4j.util.Strings;
import org.aussiebox.starexpress.StarryExpress;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GuidebookEntryCollector implements SimpleSynchronousResourceReloadListener {
    public static final GuidebookEntryCollector INSTANCE = new GuidebookEntryCollector();

    public static Object2ObjectOpenHashMap<GuidebookEntry.ParentType, Object2ObjectOpenHashMap<Identifier, ScrollContainer<FlowLayout>>> guidebookEntryCategories = new Object2ObjectOpenHashMap<>();
    public static List<GuidebookEntry> guidebookEntries = new ArrayList<>();
    public static final List<String> allKeywords = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return StarryExpress.id("guidebook_entry_collector");
    }

    @Override
    public void reload(ResourceManager manager) {
        guidebookEntryCategories.clear();
        guidebookEntries.clear();
        allKeywords.clear();

        int fails = 0;
        entry:
        for (Identifier id : manager.findResources("guidebook_entries", path -> path.getPath().endsWith(".json")).keySet()) {
            try (InputStream stream = manager.getResource(id).orElseThrow().getInputStream()) {
                JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();

                /// Fetch Parent
                Object parent = null;
                String[] directories = id.getPath().split("/");
                if (Arrays.stream(directories).toList().get(1).equals("item")) {
                    Optional<Item> item = Registries.ITEM.getOrEmpty(Identifier.of(id.getNamespace(), Arrays.stream(directories).toList().getLast().replaceAll(".json", Strings.EMPTY)));
                    if (item.isPresent()) parent = item.get();
                    else {
                        StarryExpress.LOGGER.error("Guidebook entry {} filename does not match an item from parent namespace, ignoring", id);
                        fails++;
                        continue;
                    }
                } else if (Arrays.stream(directories).toList().get(1).equals("role")) {
                    for (Role checkRole : WatheRoles.ROLES)
                        if (id.getPath().endsWith(checkRole.identifier().getPath() + ".json")) parent = checkRole;
                    if (parent == null) {
                        StarryExpress.LOGGER.error("Guidebook entry {} filename does not match with any roles, ignoring", id);
                        fails++;
                        continue;
                    }
                } else if (Arrays.stream(directories).toList().get(1).equals("modifier")) {
                    for (Modifier checkMod : HMLModifiers.MODIFIERS)
                        if (id.getPath().endsWith(checkMod.identifier().getPath() + ".json")) parent = checkMod;
                    if (parent == null) {
                        StarryExpress.LOGGER.error("Guidebook entry {} filename does not match with any modifiers, ignoring", id);
                        fails++;
                        continue;
                    }
                } else if (!Arrays.stream(directories).toList().get(1).equals("misc")) {
                    StarryExpress.LOGGER.error("Guidebook entry {} is not categorised under a valid type, ignoring", id);
                    fails++;
                    continue;
                }
                // TODO: Ensure Misc type works

                if (parent == null) {
                    StarryExpress.LOGGER.error("Guidebook entry {} is missing a valid parent, ignoring", id);
                    fails++;
                    continue;
                }

                ///  Fetch Title & Subtitle
                String title;
                if (jsonObject.has("title"))
                    title = jsonObject.get("title").getAsString();
                else {
                    StarryExpress.LOGGER.error("Guidebook entry {} does not have a title, ignoring", id);
                    continue;
                }
                String subtitle = null;
                if (jsonObject.has("subtitle"))
                    subtitle = jsonObject.get("subtitle").getAsString();

                ///  Fetch Keywords
                List<String> keywords = new ArrayList<>();
                if (jsonObject.has("keywords")) {
                    List<JsonElement> list = jsonObject.get("keywords").getAsJsonArray().asList();
                    list.forEach(jsonElement -> {
                        String string = jsonElement.getAsString();
                        if (Language.getInstance().hasTranslation(string)) {
                            if (!allKeywords.contains(string)) {
                                keywords.add(string);
                                allKeywords.add(string);
                            } else StarryExpress.LOGGER.warn("Keyword {} registered in guidebook entry {} is already in use and will be ignored", string, id);
                        } else StarryExpress.LOGGER.warn("Keyword {} does not match with a translation key and will be ignored", string);
                    });
                } else StarryExpress.LOGGER.warn("Guidebook entry {} does not have any keywords, and will not be mentionable in other entries", id);

                ///  Fetch Description
                List<DescriptionComponent> description = new ArrayList<>();
                if (jsonObject.has("description")) {
                    if (!jsonObject.get("description").isJsonArray()) {
                        StarryExpress.LOGGER.error("Description of guidebook entry {} is not an array, ignoring", id);
                        fails++;
                        continue;
                    }
                    JsonArray descArray = jsonObject.getAsJsonArray("description");
                    for (JsonElement element : descArray.asList()) {
                        if (!element.isJsonObject()) {
                            StarryExpress.LOGGER.error("Element in description of guidebook entry {} is not an parent, ignoring", id);
                            fails++;
                            continue entry;
                        }
                        JsonObject object = element.getAsJsonObject();
                        if (!object.has("id")) {
                            StarryExpress.LOGGER.error("Element in description of guidebook entry {} does not have an id, ignoring", id);
                            fails++;
                            continue entry;
                        }
                        String objectID = object.get("id").getAsString();
                        if (!object.has("type")) {
                            StarryExpress.LOGGER.error("Element in description of guidebook entry {} does not have a type, ignoring", id);
                            fails++;
                            continue entry;
                        }
                        String type = object.get("type").getAsString();
                        if (!DescriptionComponentRegistry.getMap().containsKey(type)) {
                            StarryExpress.LOGGER.error("Type of element in description of guidebook entry {} is invalid, ignoring", id);
                            fails++;
                            continue entry;
                        }
                        Class<? extends DescriptionComponent> clazz = DescriptionComponentRegistry.getMap().get(type);
                        DescriptionComponent component = clazz.getConstructor(String.class, JsonObject.class).newInstance(objectID, object);
                        description.add(component);
                    }
                } else {
                    StarryExpress.LOGGER.error("Guidebook entry {} does not contain a description, ignoring", id);
                    fails++;
                    continue;
                }
                GuidebookEntry entry = new GuidebookEntry(parent, title, subtitle, keywords, description);
                guidebookEntries.add(entry);
            } catch (Exception e) {
                StarryExpress.LOGGER.error("Error occurred while loading guidebook entry {}", id, e);
            }
        }

        StarryExpress.LOGGER.info("Successfully loaded {} guidebook entries", guidebookEntries.size());
        if (fails > 0) StarryExpress.LOGGER.error("{} guidebook entries failed to load. If this is a development environment, please review associated JSON files.", fails);
    }
}
