package com.rutledgepaulv.github.argconverters;

import java.util.Objects;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

import com.rutledgepaulv.github.structs.ConversionInfo;
import com.rutledgepaulv.github.structs.Lazy;

public class EntityFieldTypeConverter implements StringToQueryValueConverter {

    private final ConversionService conversionService;
    private final MongoMappingContext mongoMappingContext;

    public EntityFieldTypeConverter(ConversionService conversionService, MongoMappingContext mappingContext) {
        this.conversionService = conversionService;
        this.mongoMappingContext = mappingContext;
    }

    @Override
    public Lazy<Object> convert(ConversionInfo info) {
        return Lazy.fromFunc(() -> {

            PersistentPropertyPath<MongoPersistentProperty> property
                    = mongoMappingContext.getPersistentPropertyPath(info.getPathToField(), info.getTargetEntityClass());

            MongoPersistentProperty leaf = Objects.requireNonNull(property.getLeafProperty(), "Leaf can't be null");

            Class<?> targetTypeDeterminedFromEntityField;

            if (leaf.isCollectionLike()) {
                targetTypeDeterminedFromEntityField = leaf.getComponentType();
            } else {
                targetTypeDeterminedFromEntityField = leaf.getType();
            }

            return convert(info, targetTypeDeterminedFromEntityField);
        });
    }


    private Object convert(ConversionInfo info, Class<?> targetType) {
        if(!conversionService.canConvert(info.getArgument().getClass(), targetType)) {
            throw new UnsupportedOperationException("Cannot convert " + info.getArgument() + "into type " + targetType.getSimpleName());
        }
        try {
            return conversionService.convert(info.getArgument(), targetType);
        }catch(Exception e) {
            throw new UnsupportedOperationException("Cannot convert " + info.getArgument() + "into type " + targetType.getSimpleName());
        }
    }

}
