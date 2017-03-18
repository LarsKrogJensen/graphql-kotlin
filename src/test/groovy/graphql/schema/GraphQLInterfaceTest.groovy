package graphql.schema

import spock.lang.Specification

import graphql.AssertException

import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition

import static graphql.ScalarsKt.GraphQLString


class GraphQLInterfaceTest extends Specification {

    def "duplicate field definition fails"() {
        when:
        newInterface().name("TestInterfaceType")
                .typeResolver(TypeResolverProxyKt.typeResolverProxy())
                .field(newFieldDefinition().name("NAME").type(GraphQLString))
                .field(newFieldDefinition().name("NAME").type(GraphQLString))
                .build();
        then:
        thrown(AssertException)
    }
}
