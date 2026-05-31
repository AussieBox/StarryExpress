package org.aussiebox.starexpress.client.guidebook;

import dev.doctor4t.wathe.api.Role;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.agmas.harpymodloader.modifiers.Modifier;
import org.aussiebox.starexpress.client.guidebook.component.GuidebookElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class GuidebookEntry {
    public final String title;
    public final Optional<String> subtitle;
    public final Optional<Role> parentRole;
    public final Optional<Modifier> parentModifier;
    public final Optional<Item> parentItem;
    public final Optional<String> parentMisc;
    public final ParentType parentType;
    public List<String> keywords;
    public List<? extends GuidebookElement> description;

    public GuidebookEntry(Object object, String title, @Nullable String subtitle, List<String> keywords, List<? extends GuidebookElement> description) {
        this.title = title;
        this.subtitle = subtitle != null ? subtitle.describeConstable() : Optional.empty();
        this.keywords = keywords;

        switch (object) {
            case Role role -> {
                this.parentRole = Optional.of(role);
                this.parentModifier = Optional.empty();
                this.parentItem = Optional.empty();
                this.parentMisc = Optional.empty();
                this.parentType = ParentType.ROLE;
            }
            case Modifier modifier -> {
                this.parentRole = Optional.empty();
                this.parentModifier = Optional.of(modifier);
                this.parentItem = Optional.empty();
                this.parentMisc = Optional.empty();
                this.parentType = ParentType.MODIFIER;
            }
            case Item item -> {
                this.parentRole = Optional.empty();
                this.parentModifier = Optional.empty();
                this.parentItem = Optional.of(item);
                this.parentMisc = Optional.empty();
                this.parentType = ParentType.ITEM;
            }
            case String string -> {
                this.parentRole = Optional.empty();
                this.parentModifier = Optional.empty();
                this.parentItem = Optional.empty();
                this.parentMisc = Optional.of(string);
                this.parentType = ParentType.MISC;
            }
            default -> throw new IllegalArgumentException("Parent object was not of an allowed type");
        }
        this.description = description;
    }

    public Identifier getId() {
        switch (parentType) {
            case ROLE -> {
                if (parentRole.isEmpty()) break;
                return parentRole.get().identifier();
            }
            case MODIFIER -> {
                if (parentModifier.isEmpty()) break;
                return parentModifier.get().identifier();
            }
            case ITEM -> {
                if (parentItem.isEmpty()) break;
                return Identifier.of(parentItem.get().toString());
            }
            case MISC -> {
                if (parentMisc.isEmpty()) break;
                return Identifier.of(parentMisc.get());
            }
        }
        return null;
    }

    public enum ParentType implements StringIdentifiable {
        ROLE("role"),
        MODIFIER("modifier"),
        ITEM("item"),
        MISC("misc");

        private final String name;

        ParentType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}
