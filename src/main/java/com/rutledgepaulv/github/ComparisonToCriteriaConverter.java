package com.rutledgepaulv.github;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;

import com.rutledgepaulv.github.argconverters.EntityFieldTypeConverter;
import com.rutledgepaulv.github.argconverters.FieldSpecificConverter;
import com.rutledgepaulv.github.argconverters.NoOpConverter;
import com.rutledgepaulv.github.argconverters.OperatorSpecificConverter;
import com.rutledgepaulv.github.argconverters.StringToQueryValueConverter;
import com.rutledgepaulv.github.structs.ConversionInfo;
import com.rutledgepaulv.github.utils.LazyUtils;

import cz.jirutka.rsql.parser.ast.ComparisonNode;

public class ComparisonToCriteriaConverter {

    private final List<StringToQueryValueConverter> converters;

    public ComparisonToCriteriaConverter(ConversionService conversionService, MongoMappingContext mappingContext, FieldSpecificConverter... fieldSpecificConverters) {
        this.converters = new ArrayList<>();
        converters.add(new OperatorSpecificConverter());
        converters.addAll(Arrays.asList(fieldSpecificConverters));
        converters.add(new EntityFieldTypeConverter(conversionService, mappingContext));
        converters.add(new NoOpConverter());
    }

    public ComparisonToCriteriaConverter(List<StringToQueryValueConverter> converters) {
        this.converters = converters;
    }

    public Criteria asCriteria(ComparisonNode node, Class<?> targetEntityClass) {
        Operator operator = Operator.toOperator(node.getOperator());
        List<Object> arguments = mapArgumentsToAppropriateTypes(operator, node, targetEntityClass);
        return makeCriteria(node.getSelector(), operator, arguments);
    }

    private List<Object> mapArgumentsToAppropriateTypes(Operator operator, ComparisonNode node, Class<?> targetEntityClass) {
        return node.getArguments().stream()
            .map(arg -> convert(new ConversionInfo(node.getSelector(), arg, targetEntityClass, operator)))
            .collect(Collectors.toList());
    }

    private Object convert(ConversionInfo conversionInfo) {
        return LazyUtils.firstThatReturnsNonNull(converters.stream()
                .map(converter -> converter.convert(conversionInfo))
                .collect(Collectors.toList()));
    }

    private static Criteria makeCriteria(String selector, Operator operator, List<Object> arguments) {
        switch (operator) {
            case EQUAL:
                return Criteria.where(selector).is(getFirst(operator, arguments));
            case NOT_EQUAL:
                return Criteria.where(selector).ne(getFirst(operator, arguments));
            case GREATER_THAN:
                return Criteria.where(selector).gt(getFirst(operator, arguments));
            case GREATER_THAN_OR_EQUAL:
                return Criteria.where(selector).gte(getFirst(operator, arguments));
            case LESS_THAN:
                return Criteria.where(selector).lt(getFirst(operator, arguments));
            case LESS_THAN_OR_EQUAL:
                return Criteria.where(selector).lte(getFirst(operator, arguments));
            case REGEX:
                return Criteria.where(selector).regex((String)getFirst(operator, arguments));
            case EXISTS:
                return Criteria.where(selector).exists((Boolean)getFirst(operator, arguments));
            case IN:
                return Criteria.where(selector).in(arguments);
            case NOT_IN:
                return Criteria.where(selector).nin(arguments);
            default:
                // can't happen.
                return null;
        }
    }

    private static Object getFirst(Operator operator, List<Object> arguments) {
        if(arguments != null && arguments.size() == 1) {
            return arguments.iterator().next();
        } else {
            throw new UnsupportedOperationException("You cannot perform the query operation " + operator.name() + " with anything except a single value.");
        }
    }

}

