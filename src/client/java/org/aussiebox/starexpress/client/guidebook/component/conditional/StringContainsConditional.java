package org.aussiebox.starexpress.client.guidebook.component.conditional;

import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import org.aussiebox.starexpress.client.guidebook.component.ConditionalElement;
import org.aussiebox.starexpress.client.guidebook.variable.Variable;
import org.aussiebox.starexpress.exception.MissingJsonFieldException;

public class StringContainsConditional extends ConditionalElement {
    public String matchWith;

    public StringContainsConditional(String id, JsonObject object) {
        super(id, object);
        if (getVariable().getType() != Variable.VariableType.STRING && getVariable().getType() != Variable.VariableType.TEXT) throw new IllegalArgumentException("Variable is not applicable as a string or text");

        if (!object.has("regex")) throw new MissingJsonFieldException("String conditional requires regex to match with");
        this.matchWith = Text.translatable(object.get("regex").getAsString()).getString();

        JsonObject trueObject = null;
        JsonObject falseObject = null;
        try {
            trueObject = object.get("true").getAsJsonObject();
        } catch(Exception ignored) {}
        try {
            falseObject = object.get("false").getAsJsonObject();
        } catch(Exception ignored) {}

        if (trueObject == null && falseObject == null) throw new MissingJsonFieldException("String conditional requires at least one output");
        if (trueObject != null) this.output1 = trueObject;
        if (falseObject != null) this.output2 = falseObject;
    }

    @Override
    public JsonObject evaluate() {
        if (getVariable().get() instanceof Text text) return text.getString().contains(matchWith) ? this.output1 : this.output2;
        else return ((String) getVariable().get()).contains(matchWith) ? this.output1 : this.output2;
    }
}
