package graphql.validation

import graphql.GraphQLBoolean
import graphql.GraphQLInt
import graphql.GraphQLString
import graphql.GraphQLStringNonNull
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
val dogCommand = newEnum {
    name = "DogCommand"
    value { name = "SIT" }
    value { name = "DOWN" }
    value { name = "HEEL" }
}

val catCommand = newEnum {
    name = "CatCommand"
    value { name = "JUMP" }
}

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
        .withInterface(sentient)
        .build()

val alien = newObject {
    name = "Alien"
    field<String> {
        name = "name"
        type = GraphQLStringNonNull
        fetcher = fieldDataFetcher("name")
    }
    field<String> {
        name = "homePlanet"
        fetcher = fieldDataFetcher("name")
    }
    interfaces += sentient
}

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

val cat = newObject {
    name = "Cat"
    field<String> {
        name = "name"
        type = GraphQLStringNonNull
        fetcher = fieldDataFetcher("name")
    }
    field<String> {
        name = "nickname"
        type = GraphQLStringNonNull
        fetcher = fieldDataFetcher("nickname")
    }
    field<Int> {
        name = "meowVolume"
        fetcher = fieldDataFetcher("meowVolume")
    }
    field<Boolean> {
        name = "doesKnowCommand"
        arguments += catCommandArg
        fetcher = fieldDataFetcher("meowVolume")
    }
    interfaces += pet
}

val catOrDog = newUnionType {
    name = "CatOrDog"
    types += cat
    types += dog
    typeResolver = {
        when (it) {
            is Cat -> cat
            is Dog -> dog
            else   -> null
        }
    }
}

val dogOrHuman = newUnionType {
    name = "DogOrHuman"
    types += dog
    types += human
    typeResolver = {
        when (it) {
            is Human -> human
            is Dog   -> dog
            else     -> null
        }
    }
}

val humanOrAlien = newUnionType {
    name = "HumanOrAlien"
    types += human
    types += alien
    typeResolver = {
        when (it) {
            is Human -> human
            is Alien -> alien
            else     -> null
        }
    }
}

val queryRoot = newObject {
    name = "QueryRoot"
    field<String> {
        name = "dog"
        type = dog
        fetcher = fieldDataFetcher("dog")
    }
}

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
    dictionary = specValidationDictionary
    query = queryRoot
}
