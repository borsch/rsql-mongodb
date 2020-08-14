package com.rutledgepaulv.github;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Before;
import org.junit.Test;

import com.rutledgepaulv.github.argconverters.FieldSpecificConverter;
import com.rutledgepaulv.github.models.Person;

public class FieldSpecificConverterTest extends BaseIntegrationTest<Person> {

    @Before
    public void setUp() {
        FieldSpecificConverter dateFieldConverter = new FieldSpecificConverter(Person.class, "date", agr -> {
            try {
                return LocalDateTime.parse(agr).toEpochSecond(ZoneOffset.UTC);
            } catch (Exception e) {
                return Long.parseLong(agr);
            }
        });

        ComparisonToCriteriaConverter converter = new ComparisonToCriteriaConverter(CONVERSION_SERVICE, mongoMappingContext, dateFieldConverter);
        adapter = new RsqlMongoAdapter(converter);
    }

    @Test
    public void testWithFieldSpecificConverter() {
        check("date>=2020-02-02T03:04:05", "{\"date\": {\"$gte\": 1580612645}}");
    }

    @Test
    public void testWithFieldSpecificConverter_and() {
        check("date>=2020-02-02T03:04:05;date<=2020-02-10T03:04:05", "{\"$and\": [{\"date\": {\"$gte\": 1580612645}}, {\"date\": {\"$lte\": 1581303845}}]}");
    }

}
