package org.aussiebox.starexpress.client.guidebook.variable;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class VariableHandler {
    public static Object2ObjectOpenHashMap<Identifier, Variable<?>> variables = new Object2ObjectOpenHashMap<>();

    public static <V> Variable<V> registerVariable(Identifier id, V value) {
        Variable<V> variable = new Variable<>(id, value);
        VariableHandler.variables.put(id, variable);
        return variable;
    }

    public static <V> Variable<V> registerVariable(Identifier id, V value, UnaryOperator<V> onGet) {
        Variable<V> variable = new Variable<>(id, value, onGet);
        VariableHandler.variables.put(id, variable);
        return variable;
    }
}
