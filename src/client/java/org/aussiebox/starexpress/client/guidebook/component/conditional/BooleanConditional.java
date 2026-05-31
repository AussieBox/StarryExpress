package org.aussiebox.starexpress.client.guidebook.component.conditional;

import com.google.gson.JsonObject;
import org.aussiebox.starexpress.client.guidebook.component.ConditionalElement;
import org.aussiebox.starexpress.exception.MissingJsonFieldException;

public class BooleanConditional extends ConditionalElement {
    public BooleanConditional(String id, JsonObject object) {
        super(id, object);
        if (!getVariable().isBoolean()) throw new IllegalArgumentException("Variable is not applicable as a boolean");

        JsonObject trueObject = null;
        JsonObject falseObject = null;
        try {
            trueObject = object.get("true").getAsJsonObject();
        } catch(Exception ignored) {}
        try {
            falseObject = object.get("false").getAsJsonObject();
        } catch(Exception ignored) {}

        if (trueObject == null && falseObject == null) throw new MissingJsonFieldException("Boolean conditional requires at least one output");
        if (trueObject != null) this.output1 = trueObject;
        if (falseObject != null) this.output2 = falseObject;
    }

    @Override
    public JsonObject evaluate() {
        return ((boolean) getVariable().get()) ? this.output1 : this.output2;
    }
}
