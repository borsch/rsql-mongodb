package com.rutledgepaulv.github.structs;

import com.rutledgepaulv.github.Operator;

public class ConversionInfo {

    private final String pathToField;
    private final String argument;
    private final Class<?> targetEntityClass;
    private final Operator operator;

    public ConversionInfo(final String pathToField, final String argument, final Class<?> targetEntityClass, final Operator operator) {
        this.pathToField = pathToField;
        this.argument = argument;
        this.targetEntityClass = targetEntityClass;
        this.operator = operator;
    }

    public String getPathToField() {
        return pathToField;
    }

    public String getArgument() {
        return argument;
    }

    public Class<?> getTargetEntityClass() {
        return targetEntityClass;
    }

    public Operator getOperator() {
        return operator;
    }

}
