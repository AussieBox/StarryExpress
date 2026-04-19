package org.aussiebox.starexpress.guidebook;

import dev.doctor4t.wathe.api.Role;

import java.util.List;

public class GuidebookEntry {
    public final Role role;
    public List<? extends DescriptionComponent> description;

    public GuidebookEntry(Role role, List<? extends DescriptionComponent> description) {
        this.role = role;
        this.description = description;
    }
}
