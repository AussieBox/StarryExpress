package org.aussiebox.starexpress.client.guidebook.variable;

import lombok.Getter;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.UnaryOperator;

public class Variable<V> {
    @Getter private final Identifier id;
    private V value;

    private UnaryOperator<V> onGet = val -> val;

    Variable(Identifier id, V value) {
        this.id = id;
        this.value = value;
    }

    Variable(Identifier id, V value, UnaryOperator<V> onGet) {
        this.id = id;
        this.value = value;
        this.onGet = onGet;
    }

    public void set(V value) {
        this.value = value;
    }

    public V get() {
        set(onGet.apply(this.value));
        return this.value;
    }

    public VariableType getType() {
        for (VariableType type : VariableType.values()) {
            if (type.clazz == null) continue;
            if (type.clazz.isAssignableFrom(value.getClass())) return type;
        }
        return VariableType.UNSUPPORTED;
    }

    public boolean isBoolean() {
        return value.getClass() == Boolean.class;
    }

    /**
     * Enum used to tell the Variable system how to handle specific variables when inside conditionals.
     * <br><br>
     * Note that if a Variable is UNSUPPORTED, it only means it cannot be used in conditionals safely.
     */
    public enum VariableType implements StringIdentifiable {
        UNSUPPORTED("unsupported", null),
        STRING("string", String.class),
        INTEGER("integer", Integer.class),
        DOUBLE("double", Double.class),
        FLOAT("float", Float.class),
        BYTE("byte", Byte.class),
        BOOLEAN("boolean", Boolean.class),
        LONG("long", Long.class),
        SHORT("short", Short.class),
        BIG_INTEGER("big_integer", BigInteger.class),
        BIG_DECIMAL("big_decimal", BigDecimal.class),
        STRINGABLE_ENUM("stringable_enum", StringIdentifiable.class);

        private final String id;
        @Getter private final Class<?> clazz;

        VariableType(String id, Class<?> clazz) {
            this.id = id;
            this.clazz = clazz;
        }

        @Override
        public String asString() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }
    }
}
