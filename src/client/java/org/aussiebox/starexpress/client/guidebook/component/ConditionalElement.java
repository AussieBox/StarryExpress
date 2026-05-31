package org.aussiebox.starexpress.client.guidebook.component;

import com.google.gson.JsonObject;
import io.wispforest.owo.ui.core.Component;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.client.guidebook.GuidebookEntryCollector;
import org.aussiebox.starexpress.client.guidebook.variable.Variable;
import org.aussiebox.starexpress.client.guidebook.variable.VariableHandler;
import org.aussiebox.starexpress.exception.MissingJsonFieldException;
import org.jetbrains.annotations.Nullable;

public abstract class ConditionalElement extends GuidebookElement {
    public Identifier variable;
    @Nullable public JsonObject output1;
    @Nullable public JsonObject output2;

    public ConditionalElement(String id, JsonObject object) {
        super(id, object);
        if (!object.has("variable")) throw new MissingJsonFieldException("All conditionals require a variable input");
        this.variable = Identifier.of(object.get("variable").getAsString());
    }

    public abstract JsonObject evaluate();

    @Override
    public Component build() {
        JsonObject evaluated = evaluate();
        if (evaluated == null) return null;
        StarryExpress.LOGGER.info(evaluated.toString());
        GuidebookElement component = GuidebookEntryCollector.parseComponentFromJson(evaluated);
        if (component == null) return null;
        return component.build();
    }

    public Variable<?> getVariable() {
        if (!VariableHandler.variables.containsKey(variable)) throw new IllegalArgumentException("Variable identifier does not match with a registered variable");
        return VariableHandler.variables.get(variable);
    }
}
