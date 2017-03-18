package graphql


import graphql.schema.*


import graphql.schema.GraphQLFieldDefinition.Companion.newFieldDefinition
import graphql.schema.GraphQLInterfaceType.Companion.newInterface
import graphql.schema.GraphQLObjectType.Companion.newObject

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

val characterInterface = newInterface()
        .name("Character")
        .description("A character in the Star Wars Trilogy")
        .field(newFieldDefinition<Any>()
                       .name("id")
                       .description("The id of the character.")
                       .type(GraphQLNonNull(GraphQLString)))
        .field(newFieldDefinition<Any>()
                       .name("name")
                       .description("The name of the character.")
                       .type(GraphQLString))
        .field(newFieldDefinition<Any>()
                       .name("friends")
                       .description("The friends of the character, or an empty list if they have none.")
                       .type(GraphQLList(GraphQLTypeReference("Character"))))
        .field(newFieldDefinition<Any>()
                       .name("appearsIn")
                       .description("Which movies they appear in.")
                       .type(GraphQLList(episodeEnum)))
        .typeResolver(characterTypeResolver)
        .build()

val humanType = newObject()
        .name("Human")
        .description("A humanoid creature in the Star Wars universe.")
        .withInterface(characterInterface)
        .field(newFieldDefinition<Any>()
                       .name("id")
                       .description("The id of the human.")
                       .type(GraphQLNonNull(GraphQLString)))
        .field(newFieldDefinition<Any>()
                       .name("name")
                       .description("The name of the human.")
                       .type(GraphQLString))
        .field(newFieldDefinition<Any?>()
                       .name("friends")
                       .description("The friends of the human, or an empty list if they have none.")
                       .type(GraphQLList(characterInterface))
                       .fetcher(friendsDataFetcher))
        .field(newFieldDefinition<Any>()
                       .name("appearsIn")
                       .description("Which movies they appear in.")
                       .type(GraphQLList(episodeEnum)))
        .field(newFieldDefinition<Any>()
                       .name("homePlanet")
                       .description("The home planet of the human, or null if unknown.")
                       .type(GraphQLString))
        .build()

val droidType = newObject()
        .name("Droid")
        .description("A mechanical creature in the Star Wars universe.")
        .withInterface(characterInterface)
        .field(newFieldDefinition<Any>()
                       .name("id")
                       .description("The id of the droid.")
                       .type(GraphQLNonNull(GraphQLString)))
        .field(newFieldDefinition<Any>()
                       .name("name")
                       .description("The name of the droid.")
                       .type(GraphQLString))
        .field(newFieldDefinition<Any?>()
                       .name("friends")
                       .description("The friends of the droid, or an empty list if they have none.")
                       .type(GraphQLList(characterInterface))
                       .fetcher(friendsDataFetcher)
        )
        .field(newFieldDefinition<Any>()
                       .name("appearsIn")
                       .description("Which movies they appear in.")
                       .type(GraphQLList(episodeEnum)))
        .field(newFieldDefinition<Any>()
                       .name("primaryFunction")
                       .description("The primary function of the droid.")
                       .type(GraphQLString))
        .build()


val queryType = newObject()
        .name("QueryType")
        .field(newFieldDefinition<Any>()
                       .name("hero")
                       .type(characterInterface)
                       .argument(newArgument()
                                         .name("episode")
                                         .description("If omitted, returns the hero of the whole saga. If provided, returns the hero of that particular episode.")
                                         .type(episodeEnum))
                       .fetcher(staticDataFetcher(artoo))
        )
        .field(newFieldDefinition<Any?>()
                       .name("human")
                       .type(humanType)
                       .argument(newArgument()
                                         .name("id")
                                         .description("id of the human")
                                         .type(GraphQLNonNull(GraphQLString)))
                       .fetcher(humanDataFetcher)
        )
        .field(newFieldDefinition<Any?>()
                       .name("droid")
                       .type(droidType)
                       .argument(newArgument()
                                         .name("id")
                                         .description("id of the droid")
                                         .type(GraphQLNonNull(GraphQLString)))
                       .fetcher(droidDataFetcher)
        )
        .build()


val starWarsSchema = GraphQLSchema.newSchema()
        .query(queryType)
        .build()
