package com.rutledgepaulv.github;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BaseIntegrationTest.TestApplication.class)
public abstract class BaseIntegrationTest<T> {

    protected static final ConversionService CONVERSION_SERVICE = new DefaultConversionService();

    protected final Class<T> CLAZZ = (Class<T>)(new TypeToken<T>(getClass()){}).getRawType();
    protected RsqlMongoAdapter adapter;

    @Autowired
    protected MongoMappingContext mongoMappingContext;

    @Before
    public void setUp() {
        ComparisonToCriteriaConverter converter = new ComparisonToCriteriaConverter(CONVERSION_SERVICE, mongoMappingContext);
        adapter = new RsqlMongoAdapter(converter);
    }

    protected Query query(String rsql) {
        return Query.query(adapter.getCriteria(rsql, CLAZZ));
    }

    protected void check(String rsql, String mongo) {
        assertEquals(mongo, query(rsql).getQueryObject().toJson());
    }

    @SpringBootApplication
    public static class TestApplication {
        public static void main(String[] args) {
            SpringApplication.run(TestApplication.class, args);
        }
    }

}
