package org.aussiebox.starexpress.client.guidebook;

import joptsimple.internal.Strings;
import net.minecraft.text.Text;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.client.guidebook.description.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DescriptionComponentRegistry {
    private static final Map<String, Class<? extends DescriptionComponent>> registry = new LinkedHashMap<>();

    static {
        register("text", TextComponent.class);
        register("texture", TextureComponent.class);
        register("text_with_texture", TextWithTextureComponent.class);
        register("item", ItemComponent.class);
        register("text_with_item", TextWithItemComponent.class);
        register("block", BlockComponent.class);
        register("text_with_block", TextWithBlockComponent.class);
        register("hotbar", HotbarComponent.class);
        register("spacer", SpacerComponent.class);
    }

    public static void register(String id, Class<? extends DescriptionComponent> clazz) {
        registry.put(id, clazz);
    }

    public static Map<String, Class<? extends DescriptionComponent>> getMap() {
        return registry;
    }

    public static Text parseStringToContent(String string, boolean includeKeywords) {
        String translated = Text.translatable(string).getString();
        if (includeKeywords)
            for (GuidebookEntry entry : GuidebookEntryCollector.guidebookEntries) {
                for (String keyword : entry.keywords) {
                    StringBuilder parsed = new StringBuilder(Strings.EMPTY);
                    String translatedKeyword = Text.translatable(keyword).getString();
                    String regex = "(?i)" + Pattern.quote(translatedKeyword);
                    String[] split = translated.split("(?<=" + regex + ")|(?=" + regex + ")");
                    StarryExpress.LOGGER.info(Arrays.toString(split));
                    for (String part : split) {
                        if (part.equalsIgnoreCase(translatedKeyword)) parsed.append("<entry:\"").append(entry.getId()).append("\">").append(part).append("</entry>");
                        else parsed.append(part);
                    }
                    translated = parsed.toString();
                }
            }
        return Text.translatable(translated);
    }
}
