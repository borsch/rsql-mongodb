package com.rutledgepaulv.github.argconverters;

import java.util.function.Function;

import com.rutledgepaulv.github.structs.ConversionInfo;
import com.rutledgepaulv.github.structs.Lazy;

public class FieldSpecificConverter implements StringToQueryValueConverter {

    private final Class<?> objectClass;
    private final String pathToField;
    private final Function<String, Object> converter;

    public FieldSpecificConverter(final Class<?> objectClass, final String pathToField, final Function<String, Object> converter) {
        this.objectClass = objectClass;
        this.pathToField = pathToField;
        this.converter = converter;
    }

    @Override
    public Lazy<Object> convert(final ConversionInfo info) {
        return Lazy.fromFunc(() -> {
            if (info.getTargetEntityClass().equals(objectClass) && info.getPathToField().equals(pathToField)) {
                return converter.apply(info.getArgument());
            }

            return null;
        });
    }


}
