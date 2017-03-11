package graphql.schema

import graphql.ScalarsKt
import spock.lang.Specification

import graphql.AssertException

import static graphql.schema.GraphQLInputObjectType.newInputObject
import static graphql.schema.GraphQLInputObjectField.newInputObjectField



class GraphQLInputObjectTypeTest extends Specification {

    def "duplicate field definition fails"() {
        when:
        newInputObject().name("TestInputObjectType")
                .field(newInputObjectField().name("NAME").type(ScalarsKt.GraphQLString))
                .field(newInputObjectField().name("NAME").type(ScalarsKt.GraphQLString))
                .build()
        then:
        thrown(AssertException)
    }
}
