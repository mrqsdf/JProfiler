package fr.mrqsdf.jprofiler.binding;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents a bindable property that can be linked to UI components
 */
public class BindableProperty<T> {
    
    private T value;
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final Class<T> type;
    private final String name;

    public BindableProperty(String name, T initialValue, Class<T> type) {
        this.name = name;
        this.value = initialValue;
        this.type = type;
        this.getter = () -> value;
        this.setter = v -> value = v;
    }

    public BindableProperty(String name, Supplier<T> getter, Consumer<T> setter, Class<T> type) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;
        this.type = type;
        this.value = getter.get();
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        if (getter != null) {
            value = getter.get();
        }
        return value;
    }

    public void setValue(T newValue) {
        this.value = newValue;
        if (setter != null) {
            setter.accept(newValue);
        }
    }

    public void setValueFromString(String stringValue) {
        if (stringValue == null) {
            setValue(null);
            return;
        }

        T convertedValue = convertFromString(stringValue);
        setValue(convertedValue);
    }

    @SuppressWarnings("unchecked")
    private T convertFromString(String stringValue) {
        if (type == String.class) {
            return (T) stringValue;
        } else if (type == Integer.class || type == int.class) {
            return (T) Integer.valueOf(stringValue);
        } else if (type == Long.class || type == long.class) {
            return (T) Long.valueOf(stringValue);
        } else if (type == Double.class || type == double.class) {
            return (T) Double.valueOf(stringValue);
        } else if (type == Float.class || type == float.class) {
            return (T) Float.valueOf(stringValue);
        } else if (type == Boolean.class || type == boolean.class) {
            return (T) Boolean.valueOf(stringValue);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("BindableProperty{name='%s', value=%s, type=%s}", name, getValue(), type.getSimpleName());
    }
}
