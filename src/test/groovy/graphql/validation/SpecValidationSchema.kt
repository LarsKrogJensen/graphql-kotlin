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

val sentient = newInterface {
    name = "Sentient"
    field<String> {
        name = "name"
        type = GraphQLStringNonNull
        fetcher = fieldDataFetcher("name")
    }
    typeResolver = {
        when (it) {
            is Human -> human
            is Alien -> alien
            else     -> null
        }
    }
}

val pet = newInterface {
    name = "Pet"
    field<String> {
        name = "name"
        type = GraphQLStringNonNull
        fetcher = fieldDataFetcher("name")
    }
    typeResolver = {
        when (it) {
            is Dog -> dog
            is Cat -> cat
            else   -> null
        }
    }
}

val human: GraphQLObjectType = newObject {
    name = "Human"
    field<String> {
        name = "name"
        type = GraphQLStringNonNull
        fetcher = fieldDataFetcher("name")
    }
    interfaces += interfaceRef("Sentient")
}

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
    interfaces += interfaceRef("Sentient")
}

val dog = newObject {
    name = "Dog"
    field<String> {
        name = "name"
        type = GraphQLStringNonNull
        fetcher = fieldDataFetcher<String>("name")
    }
    field<String> {
        name = "nickname"
        type = GraphQLStringNonNull
        fetcher = fieldDataFetcher<String>("nickname")
    }
    field<Int> {
        name = "barkVolume"
        fetcher = fieldDataFetcher<Int>("barkVolume")
    }
    field<Boolean> {
        name = "doesKnowCommand"
        type = GraphQLNonNull(GraphQLBoolean)
        fetcher = fieldDataFetcher<Boolean>("doesKnowCommand")
        argument {
            name = "dogCommand"
            type = GraphQLNonNull(dogCommand)
        }
    }
    field<Boolean> {
        name = "isHousetrained"
        type = GraphQLNonNull(GraphQLBoolean)
        fetcher = fieldDataFetcher<Boolean>("isHousetrained")
        argument {
            name = "atOtherHomes"
            type = GraphQLBoolean
        }
    }
    field<Human> {
        name = "owner"
        type = human
        fetcher = fieldDataFetcher("owner")
    }
    interfaces += interfaceRef("Pet")
}

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
        argument {
            name = "catCommand"
            type = GraphQLNonNull(catCommand)
        }
        fetcher = fieldDataFetcher("meowVolume")
    }
    interfaces += interfaceRef("Pet")
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
