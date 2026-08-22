package org.aussiebox.starexpress.client.guidebook;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import joptsimple.internal.Strings;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.aussiebox.starexpress.client.guidebook.component.ConditionalElement;
import org.aussiebox.starexpress.client.guidebook.component.DescriptionElement;
import org.aussiebox.starexpress.client.guidebook.component.conditional.BooleanConditional;
import org.aussiebox.starexpress.client.guidebook.component.conditional.StringContainsConditional;
import org.aussiebox.starexpress.client.guidebook.component.conditional.StringMatchesConditional;
import org.aussiebox.starexpress.client.guidebook.component.description.*;

import java.util.Map;
import java.util.regex.Pattern;

public class ElementRegistry {
    private static final Object2ObjectOpenHashMap<String, Class<? extends DescriptionElement>> descriptionRegistry = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, Class<? extends ConditionalElement>> conditionalRegistry = new Object2ObjectOpenHashMap<>();

    static {
        registerDescription("text", TextElement.class);
        registerDescription("texture", TextureElement.class);
        registerDescription("text_with_texture", TextWithTextureElement.class);
        registerDescription("item", ItemElement.class);
        registerDescription("text_with_item", TextWithItemElement.class);
        registerDescription("block", BlockElement.class);
        registerDescription("text_with_block", TextWithBlockElement.class);
        registerDescription("hotbar", HotbarElement.class);
        registerDescription("spacer", SpacerElement.class);

        registerConditional("conditional_boolean", BooleanConditional.class);
        registerConditional("conditional_string_matches", StringMatchesConditional.class);
        registerConditional("conditional_string_contains", StringContainsConditional.class);
    }

    public static void registerDescription(String id, Class<? extends DescriptionElement> clazz) {
        descriptionRegistry.put(id, clazz);
    }

    public static void registerConditional(String id, Class<? extends ConditionalElement> clazz) {
        conditionalRegistry.put(id, clazz);
    }

    public static Map<String, Class<? extends DescriptionElement>> getDescriptionMap() {
        return descriptionRegistry;
    }

    public static Map<String, Class<? extends ConditionalElement>> getConditionalMap() {
        return conditionalRegistry;
    }

    public static Text parseStringToContent(String key, boolean includeKeywords) {
        String translated = I18n.translate(key);

        if (includeKeywords) {
            for (GuidebookEntry entry : GuidebookEntryCollector.guidebookEntries) {
                for (String keyword : entry.keywords) {
                    StringBuilder parsed = new StringBuilder();
                    String translatedKeyword = net.minecraft.client.resource.language.I18n.translate(keyword);
                    String regex = "(?i)" + Pattern.quote(translatedKeyword);
                    String[] split = translated.split("(?<=" + regex + ")|(?=" + regex + ")");
                    for (String part : split) {
                        if (part.equalsIgnoreCase(translatedKeyword)) {
                            parsed.append("<entry:\"").append(entry.getId()).append("\">").append(part).append("</entry>");
                        } else {
                            parsed.append(part);
                        }
                    }
                    translated = parsed.toString();
                }
            }
        }
        return Text.literal(translated);
    }

}
