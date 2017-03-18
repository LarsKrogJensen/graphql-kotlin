package graphql

import graphql.schema.DataFetchingEnvironmentImpl
import graphql.schema.FieldDataFetcher
import graphql.schema.FieldDataFetcherKt
import graphql.schema.GraphQLList
import graphql.schema.PropertyDataFetcher
import graphql.schema.PropertyDataFetcherKt
import spock.lang.Specification

import static graphql.ScalarsKt.GraphQLBoolean
import static graphql.ScalarsKt.GraphQLString

class DataFetcherTest extends Specification {

    @SuppressWarnings("GroovyUnusedDeclaration")
    class DataHolder {

        private String privateField
        public String publicField
        private Boolean booleanField
        private Boolean booleanFieldWithGet

        String getProperty() {
            return privateField
        }

        void setProperty(String value) {
            privateField = value
        }

        Boolean isBooleanField() {
            return booleanField
        }

        void setBooleanField(Boolean value) {
            booleanField = value
        }

        Boolean getBooleanFieldWithGet() {
            return booleanFieldWithGet
        }

        Boolean setBooleanFieldWithGet(Boolean value) {
            booleanFieldWithGet = value
        }
    }

    DataHolder dataHolder

    def setup() {
        dataHolder = new DataHolder()
        dataHolder.publicField = "publicValue"
        dataHolder.setProperty("propertyValue")
        dataHolder.setBooleanField(true)
        dataHolder.setBooleanFieldWithGet(false)
    }

    def "get field value"() {
        given:
        def environment = new DataFetchingEnvironmentImpl(dataHolder,
                new HashMap<>(),
                new Object(), [], GraphQLString, GraphQLString, StarWarsSchemaKt.starWarsSchema)

        when:
        def result = FieldDataFetcherKt.fieldDataFetcher("publicField").invoke(environment)

        then:
        result.toCompletableFuture().get() == "publicValue"
    }

    def "get property value"() {
        given:
        def environment = new DataFetchingEnvironmentImpl(dataHolder,
                new HashMap<>(),
                new Object(), [], GraphQLString, GraphQLString, StarWarsSchemaKt.starWarsSchema)

        when:
        def result = PropertyDataFetcherKt.propertyDataFetcher("property").invoke(environment)
        then:
        result.toCompletableFuture().get() == "propertyValue"
    }

    def "get Boolean property value"() {
        given:
        def environment = new DataFetchingEnvironmentImpl(dataHolder,
                       new HashMap<>(),
                       new Object(), [], GraphQLBoolean, GraphQLString, StarWarsSchemaKt.starWarsSchema)

        when:
        def result = PropertyDataFetcherKt.propertyDataFetcher("booleanField").invoke(environment)
        then:
        result.toCompletableFuture().get() == true
    }

    def "get Boolean property value with get"() {
        given:
        def environment = new DataFetchingEnvironmentImpl(dataHolder,
                               new HashMap<>(),
                               new Object(), [], GraphQLBoolean, GraphQLString, StarWarsSchemaKt.starWarsSchema)
        when:
        def result = PropertyDataFetcherKt.propertyDataFetcher("booleanFieldWithGet").invoke(environment)
        then:
        result.toCompletableFuture().get() == false
    }

    def "get field value as property"() {
        given:
        def environment = new DataFetchingEnvironmentImpl(dataHolder,
                      new HashMap<>(),
                      new Object(), [], GraphQLString, GraphQLString, StarWarsSchemaKt.starWarsSchema)

        when:
        def result = PropertyDataFetcherKt.propertyDataFetcher("publicField").invoke(environment)
        then:
        result.toCompletableFuture().get() == "publicValue"
    }
}
