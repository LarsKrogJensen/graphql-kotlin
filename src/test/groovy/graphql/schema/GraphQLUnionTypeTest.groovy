package graphql.schema

import spock.lang.Specification

import graphql.AssertException

import static graphql.schema.GraphQLUnionType.newUnionType

import static graphql.ScalarsKt.GraphQLString


class GraphQLUnionTypeTest extends Specification {

    def "no possible types in union fails"() {
        when:
        newUnionType()
                .name("TestUnionType")
//                .typeResolver()
                .build();
        then:
        thrown(AssertException)
    }
}
