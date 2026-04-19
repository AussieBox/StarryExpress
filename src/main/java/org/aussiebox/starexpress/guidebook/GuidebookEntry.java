package org.aussiebox.starexpress.guidebook;

import dev.doctor4t.wathe.api.Role;
import net.minecraft.item.Item;
import org.agmas.harpymodloader.modifiers.Modifier;

import java.util.List;
import java.util.Optional;

public class GuidebookEntry {
    public final Optional<Role> parentRole;
    public final Optional<Modifier> parentModifier;
    public final Optional<Item> parentItem;
    public final Optional<String> parentMisc;
    public List<? extends DescriptionComponent> description;

    public GuidebookEntry(Object object, List<? extends DescriptionComponent> description) {
        switch (object) {
            case Role role -> {
                this.parentRole = Optional.of(role);
                this.parentModifier = Optional.empty();
                this.parentItem = Optional.empty();
                this.parentMisc = Optional.empty();
            }
            case Modifier modifier -> {
                this.parentRole = Optional.empty();
                this.parentModifier = Optional.of(modifier);
                this.parentItem = Optional.empty();
                this.parentMisc = Optional.empty();
            }
            case Item item -> {
                this.parentRole = Optional.empty();
                this.parentModifier = Optional.empty();
                this.parentItem = Optional.of(item);
                this.parentMisc = Optional.empty();
            }
            case String string -> {
                this.parentRole = Optional.empty();
                this.parentModifier = Optional.empty();
                this.parentItem = Optional.empty();
                this.parentMisc = Optional.of(string);
            }
            default -> throw new IllegalArgumentException("Parent object was not of an allowed type");
        }
        this.description = description;
    }
}
