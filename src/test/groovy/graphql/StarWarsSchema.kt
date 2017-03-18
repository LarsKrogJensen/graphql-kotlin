package graphql


import graphql.schema.*

val episodeEnum = newEnum {
    name = "Episode"
    description = "One of the films in the Star Wars Trilogy"
    value {
        name = "NEWHOPE"
        description = "Released in 1977."
        value = 4
    }
    value {
        name = "EMPIRE"
        description = "Released in 1980."
        value = 5
    }
    value {
        name = "JEDI"
        description = "Released in 1983."
        value = 6
    }
}

val characterInterface = newInterface {
    name = "Character"
    description = "A character in the Star Wars Trilogy"
    field<String> {
        name = "id"
        description = "The id of the character."
        type = GraphQLStringNonNull
    }
    field<String> {
        name = "name"
        description = "The id of the character."
    }
    field<List<Any>> {
        name = "friends"
        description = "The friends of the character, or an empty list if they have none."
        type = GraphQLList(GraphQLTypeReference("Character"))

    }
    field<List<Any>> {
        name = "appearsIn"
        description = "Which movies they appear in."
        type = GraphQLList(GraphQLList(episodeEnum))
    }
    typeResolver = characterTypeResolver
}


val humanType = newObject {
    name = "Human"
    description = "A humanoid creature in the Star Wars universe."
    interfaces += characterInterface
    field<String> {
        name = "id"
        description = "The id of the human."
        type = GraphQLStringNonNull
    }
    field<String> {
        name = "name"
        description = "The id of the human."
    }
    field<List<Any?>> {
        name = "friends"
        description = "The friends of the human, or an empty list if they have none."
        type = GraphQLList(typeRef("Character"))
        fetcher = friendsDataFetcher

    }
    field<List<Any>> {
        name = "appearsIn"
        description = "Which movies they appear in."
        type = GraphQLList(episodeEnum)
    }
    field<String> {
        name = "homePlanet"
        description = "The home planet of the human, or null if unknown."
    }
}

val droidType = newObject {
    name = "Droid"
    description = "A mechanical creature in the Star Wars universe."
    interfaces += characterInterface
    field<String> {
        name = "id"
        description = "The id of the droid."
        type = GraphQLStringNonNull
    }
    field<String> {
        name = "name"
        description = "The name of the droid."
    }
    field<List<Any?>> {
        name = "friends"
        description = "The friends of the droid, or an empty list if they have none."
        type = GraphQLList(characterInterface)
        fetcher = friendsDataFetcher
    }
    field<List<Any>> {
        name = "appearsIn"
        description = "Which movies they appear in."
        type = GraphQLList(episodeEnum)
    }
    field<String> {
        name = "primaryFunction"
        description = "The primary function of the droid."
    }
}

val queryType = newObject {
    name = "QueryType"
    field<Any> {
        name = "hero"
        type = characterInterface
        argument {
            name = "episode"
            description = "If omitted, returns the hero of the whole saga. If provided, returns the hero of that particular episode."
            type = episodeEnum
        }
        fetcher = staticDataFetcher(artoo)
    }
    field<Any> {
        name = "human"
        type = humanType
        argument {
            name = "id"
            description = "id of the human"
            type = GraphQLStringNonNull
        }
        fetcher = humanDataFetcher
    }
    field<Any> {
        name = "droid"
        type = droidType
        argument {
            name = "id"
            description = "id of the droid"
            type = GraphQLStringNonNull
        }
        fetcher = droidDataFetcher
    }
}

val starWarsSchema = GraphQLSchema.newSchema()
        .query(queryType)
        .build()
