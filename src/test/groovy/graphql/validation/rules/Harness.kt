package graphql.validation.rules

import graphql.GraphQLBoolean
import graphql.GraphQLInt
import graphql.schema.*


val dummyTypeResolve: TypeResolver = typeResolverProxy()

val Being = newInterface {
    name = "CatOrDog"
    field<String> { name = "name" }
    typeResolver = typeResolverProxy()
}

val Pet = newInterface {
    name = "Pet"
    field<String> { name = "name" }
    typeResolver = typeResolverProxy()
}

val DogCommand = newEnum {
    name = "DogCommand"
    value { name = "SIT" }
    value { name = "HEEL" }
    value { name = "DOWN" }
}

val Dog = newObject {
    name = "Dog"
    field<String> { name = "name" }
    field<String> { name = "nickName" }
    field<Int> { name = "barkVolume" }
    field<Boolean> { name = "barks" }
    field<Boolean> {
        name = "doesKnowCommand"
        argument {
            name = "dogCommand"
            type = DogCommand
        }
    }
    field<Boolean> {
        name = "isHouseTrained"
        argument {
            name = "atOtherHomes"
            type = GraphQLBoolean
            defaultValue = true
        }
    }
    field<Boolean> {
        name = "isAtLocation"
        argument {
            name = "x"
            type = GraphQLInt
        }
        argument {
            name = "y"
            type = GraphQLInt
        }
    }
    interfaces += Being
    interfaces += Pet
}

val FurColor = newEnum {
    name = "FurColor"
    value { name = "BROWN" }
    value { name = "BLACK" }
    value { name = "TAN" }
    value { name = "SPOTTED" }
}


val Cat = newObject {
    name = "Cat"
    field<String> {
        name = "name"
    }
    field<String> {
        name = "nickName"
    }
    field<Boolean> {
        name = "meows"
    }
    field<Int> {
        name = "meowVolume"
    }
    field<Any> {
        name = "furColor"
        type = FurColor
    }
    interfaces += Being
    interfaces += Pet
}

val CatOrDog = newUnionType {
    name = "CatOrDog"
    types += Dog
    types += Cat
    typeResolver = typeResolverProxy()
}

val Intelligent = newInterface {
    name = "Intelligent"
    field<Int> {
        name = "iq"
    }
    typeResolver = dummyTypeResolve
}

val Human = newObject {
    name = "Human"
    field<String> {
        name = "name"
        argument {
            name = "surname"
            type = GraphQLBoolean
        }
    }
    field<Any> {
        name = "pets"
        type = GraphQLList(Pet)
    }
    field<Any> {
        name = "relatives"
        type = GraphQLList(GraphQLTypeReference("Human"))
    }
    field<Int> {
        name = "iq"
    }
    interfaces += Being
    interfaces += Intelligent
}

val Alien = newObject {
    name = "Alien"
    field<Int> { name = "numEyes" }
    field<Int> { name = "iq" }
    interfaces += Being
    interfaces += Intelligent
}

val DogOrHuman = newUnionType {
    name = "DogOrHuman"
    types += Dog
    types += Human
    typeResolver = dummyTypeResolve

}

val HumanOrAlien = newUnionType {
    name = "HumanOrAlien"
    types += Alien
    types += Human
    typeResolver = dummyTypeResolve
}

//    public static GraphQLInputObjectType ComplexInput = newInputObject()
//            .field(newInputObjectField()
//                    .name("requiredField")
//                    .type(new GraphQLNonNull(GraphQLBoolean))
//                    .build())
//            .field(newInputObjectField()
//                    .name("intField")
//                    .type(GraphQLInt)
//                    .build())
//            .field(newInputObjectField()
//                    .name("stringField")
//                    .type(GraphQLString)
//                    .build())
//            .field(newInputObjectField()
//                    .name("booleanField")
//                    .type(GraphQLBoolean)
//                    .build())
//            .field(newInputObjectField()
//                    .name("stringListField")
//                    .type(new GraphQLList(GraphQLString))
//                    .build())
//            .build();


val QueryRoot = newObject {
    name = "QueryRoot"
    field<Any> {
        name = "alien"
        type = Alien
    }
    field<Any> {
        name = "dog"
        type = Dog
    }
    field<Any> {
        name = "cat"
        type = Cat
    }
    field<Any> {
        name = "pet"
        type = Pet
    }
    field<Any> {
        name = "catOrDog"
        type = CatOrDog
    }
    field<Any> {
        name = "dogOrHuman"
        type = DogOrHuman
    }
    field<Any> {
        name = "humanOrAlien"
        type = HumanOrAlien
    }
}

val Schema = newSchema {
    query = QueryRoot
}

