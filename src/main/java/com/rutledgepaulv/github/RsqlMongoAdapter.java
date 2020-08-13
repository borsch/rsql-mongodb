package com.rutledgepaulv.github;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.query.Criteria;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

public class RsqlMongoAdapter {

    private final ComparisonToCriteriaConverter converter;
    private final RSQLParser parser;

    public RsqlMongoAdapter(ComparisonToCriteriaConverter converter) {
        this.converter = converter;
        this.parser = new RSQLParser(Arrays.stream(Operator.values())
                .map(op -> op.operator)
                .collect(Collectors.toSet()));
    }

    public Criteria getCriteria(String rsql, Class<?> targetEntityType) {
        Node node = parser.parse(rsql);
        CriteriaBuildingVisitor visitor = new CriteriaBuildingVisitor(converter, targetEntityType);
        return node.accept(visitor);
    }

}
