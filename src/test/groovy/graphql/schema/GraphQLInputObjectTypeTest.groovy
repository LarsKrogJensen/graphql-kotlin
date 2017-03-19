package graphql.schema

import graphql.AssertException
import graphql.ScalarsKt
import spock.lang.Specification

import static graphql.schema.GraphQLInputObjectField.newInputObjectField
import static graphql.schema.GraphQLInputObjectType.newInputObject

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
