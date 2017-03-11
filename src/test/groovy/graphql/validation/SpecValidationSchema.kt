package graphql.validation

import graphql.GraphQLBoolean
import graphql.GraphQLInt
import graphql.GraphQLString
import graphql.schema.*
import graphql.schema.GraphQLInterfaceType.Companion.newInterface
import graphql.schema.GraphQLObjectType.Companion.newObject
import graphql.schema.GraphQLSchema.Companion.newSchema
import graphql.schema.GraphQLUnionType.Companion.newUnionType
import graphql.validation.SpecValidationSchemaPojos.*
import java.util.*

/**
 * Sample schema used in the spec for validation examples
 * http://facebook.github.io/graphql/#sec-Validation
 * @author dwinsor
 */
object SpecValidationSchema {
    val dogCommand = GraphQLEnumType.newEnum()
            .name("DogCommand")
            .value("SIT")
            .value("DOWN")
            .value("HEEL")
            .build()

    val catCommand = GraphQLEnumType.newEnum()
            .name("CatCommand")
            .value("JUMP")
            .build()

    val sentient: GraphQLInterfaceType = GraphQLInterfaceType.newInterface()
            .name("Sentient")
            .field(GraphQLFieldDefinition<String>(
                    "name", null, GraphQLNonNull(GraphQLString), fieldDataFetcher("name"), listOf(), null))
            .typeResolver { obj: Any ->
                if (obj is Human) human
                else if (obj is Alien) alien
                else null
            }
            .build()

    val pet: GraphQLInterfaceType = newInterface()
            .name("Pet")
            .field(GraphQLFieldDefinition(
                    "name", null, GraphQLNonNull(GraphQLString), fieldDataFetcher<String>("name"), listOf(), null))
            .typeResolver({
                              if (it is Dog) dog
                              else if (it is Cat) cat
                              else null
                          })
            .build()

    val human: GraphQLObjectType = newObject()
            .name("Human")
            .field(GraphQLFieldDefinition(
                    "name", null, GraphQLNonNull(GraphQLString), fieldDataFetcher<String>("name"), listOf(), null))
            .withInterface(SpecValidationSchema.sentient)
            .build()

    val alien: GraphQLObjectType = newObject()
            .name("Alien")
            .field(GraphQLFieldDefinition(
                    "name", null, GraphQLNonNull(GraphQLString), fieldDataFetcher<String>("name"), emptyList(), null))
            .field(GraphQLFieldDefinition(
                    "homePlanet", null, GraphQLString, fieldDataFetcher<String>("homePlanet"), emptyList(), null))
            .withInterface(SpecValidationSchema.sentient)
            .build()

    val dogCommandArg = newArgument()
            .name("dogCommand")
            .type(GraphQLNonNull(dogCommand))
            .build()

    val atOtherHomesArg = newArgument()
            .name("atOtherHomes")
            .type(GraphQLBoolean)
            .build()

    val catCommandArg = newArgument()
            .name("catCommand")
            .type(GraphQLNonNull(catCommand))
            .build()

    val dog = newObject()
            .name("Dog")
            .field(GraphQLFieldDefinition(
                    "name", null, GraphQLNonNull(GraphQLString), fieldDataFetcher<String>("name"), emptyList(), null))
            .field(GraphQLFieldDefinition(
                    "nickname", null, GraphQLString, fieldDataFetcher<String>("nickname"), emptyList(), null))
            .field(GraphQLFieldDefinition(
                    "barkVolume", null, GraphQLInt, fieldDataFetcher<Int>("barkVolume"), emptyList(), null))
            .field(GraphQLFieldDefinition(
                    "doesKnowCommand", null, GraphQLNonNull(GraphQLBoolean), fieldDataFetcher<Boolean>("doesKnowCommand"),
                    listOf(dogCommandArg), null))
            .field(GraphQLFieldDefinition<Boolean>(
                    "isHousetrained", null, GraphQLBoolean, fieldDataFetcher("isHousetrained"),
                    listOf(atOtherHomesArg), null))
            .field(GraphQLFieldDefinition<Human>(
                    "owner", null, human, fieldDataFetcher("owner"), emptyList(), null))
            .withInterface(pet)
            .build()

    val cat = newObject()
            .name("Cat")
            .field(GraphQLFieldDefinition<String>(
                    "name", null, GraphQLNonNull(GraphQLString), fieldDataFetcher("name"), emptyList(), null))
            .field(GraphQLFieldDefinition<String>(
                    "nickname", null, GraphQLString, fieldDataFetcher("nickname"), emptyList(), null))
            .field(GraphQLFieldDefinition<Int>(
                    "meowVolume", null, GraphQLInt, fieldDataFetcher("meowVolume"), emptyList(), null))
            .field(GraphQLFieldDefinition<Boolean>(
                    "doesKnowCommand", null, GraphQLNonNull(GraphQLBoolean), fieldDataFetcher("doesKnowCommand"),
                    Arrays.asList(catCommandArg), null))
            .withInterface(SpecValidationSchema.pet)
            .build()

    val catOrDog = newUnionType()
            .name("CatOrDog")
            .possibleTypes(cat, dog)
            .typeResolver {
                when (it) {
                    is Cat -> cat
                    is Dog -> dog
                    else   -> null
                }
            }
            .build()

    val dogOrHuman = newUnionType()
            .name("DogOrHuman")
            .possibleTypes(dog, human)
            .typeResolver {
                when (it) {
                    is Human -> human
                    is Dog   -> dog
                    else     -> null
                }
            }
            .build()

    val humanOrAlien = newUnionType()
            .name("HumanOrAlien")
            .possibleTypes(human, alien)
            .typeResolver {
                when (it) {
                    is Human -> human
                    is Alien -> alien
                    else     -> null
                }
            }
            .build()

    val queryRoot = newObject()
            .name("QueryRoot")
            .field(GraphQLFieldDefinition(
                    "dog", null, dog, fieldDataFetcher<String>("dog"), emptyList(), null))
            .build()

    @SuppressWarnings("serial")
    val specValidationDictionary = setOf(
            dogCommand,
            catCommand,
            sentient,
            pet,
            human,
            alien,
            dog,
            cat,
            catOrDog,
            dogOrHuman,
            humanOrAlien)


    val specValidationSchema = newSchema {
        query = queryRoot
    }
}
