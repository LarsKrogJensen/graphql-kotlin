package graphql

import graphql.GarfieldSchema.CatType
import graphql.GarfieldSchema.DogType
import graphql.GarfieldSchema.NamedType
import graphql.schema.*
import graphql.schema.GraphQLFieldDefinition.Companion.newFieldDefinition
import graphql.schema.GraphQLInputObjectField.Companion.newInputObjectField
import graphql.schema.GraphQLInputObjectType.Companion.newInputObject
import graphql.schema.GraphQLInputObjectType.Companion.reference
import graphql.schema.GraphQLObjectType.Companion.newObject
import graphql.schema.GraphQLUnionType.Companion.newUnionType
import java.util.*

object TypeReferenceSchema {

    var PetType = newUnionType()
            .name("Pet")
            .possibleType(GraphQLObjectType.reference(CatType.name))
            .possibleType(GraphQLObjectType.reference(DogType.name))
            .typeResolver(typeResolverProxy())
            .build()

    var PersonInputType = newInputObject()
            .name("Person_Input")
            .field(newInputObjectField()
                           .name("name")
                           .type(GraphQLString))
            .build()

    var PersonType = newObject()
            .name("Person")
            .field(newFieldDefinition<Any>()
                           .name("name")
                           .type(GraphQLString))
            .field(newFieldDefinition<Any>()
                           .name("pet")
                           .type(GraphQLTypeReference(PetType.name)))
            .withInterface(GraphQLInterfaceType.reference(NamedType.name))
            .build()

    var exists: GraphQLFieldDefinition<*> = newFieldDefinition<Any>()
            .name("exists")
            .type(GraphQLBoolean)
            .argument(newArgument()
                              .name("person")
                              .type(reference("Person_Input")))
            .build()

    var find: GraphQLFieldDefinition<*> = newFieldDefinition<Any>()
            .name("find")
            .type(GraphQLTypeReference("Person"))
            .argument(newArgument()
                              .name("name")
                              .type(GraphQLString))
            .build()

    var PersonService = newObject()
            .name("PersonService")
            .field(exists)
            .field(find)
            .build()

    var SchemaWithReferences = GraphQLSchema(PersonService, null,
                                             HashSet<GraphQLType>(Arrays.asList<GraphQLUnmodifiedType>(PersonType, PersonInputType, PetType, CatType, DogType, NamedType)))
}
