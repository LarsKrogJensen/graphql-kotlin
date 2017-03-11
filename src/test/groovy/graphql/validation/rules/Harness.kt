package graphql.validation.rules

import graphql.GraphQLBoolean
import graphql.GraphQLInt
import graphql.GraphQLString
import graphql.schema.*

import graphql.schema.GraphQLEnumType.Companion.newEnum
import graphql.schema.GraphQLFieldDefinition.Companion.newFieldDefinition
import graphql.schema.GraphQLInterfaceType.Companion.newInterface
import graphql.schema.GraphQLObjectType.Companion.newObject
import graphql.schema.GraphQLSchema.Companion.newSchema
import graphql.schema.GraphQLUnionType.Companion.newUnionType


object Harness {

    //    private static TypeResolver dummyTypeResolve = new TypeResolver() {
    //        @Override
    //        public GraphQLObjectType getType(Object object) {
    //            return null;
    //        }
    //    };

    val dummyTypeResolve: TypeResolver = typeResolverProxy()

    val Being = newInterface()
            .name("Being")
            .field(newFieldDefinition<Any>()
                           .name("name")
                           .type(GraphQLString))
            .typeResolver(typeResolverProxy())
            .build()

    val Pet = newInterface()
            .name("Pet")
            .field(newFieldDefinition<Any>()
                           .name("name")
                           .type(GraphQLString))
            .typeResolver(dummyTypeResolve)
            .build()

    val DogCommand = newEnum()
            .name("DogCommand")
            .value("SIT")
            .value("HEEL")
            .value("DOWN")
            .build()

    val Dog = newObject()
            .name("Dog")
            .field(newFieldDefinition<Any>()
                           .name("name")
                           .type(GraphQLString))
            .field(newFieldDefinition<Any>()
                           .name("nickName")
                           .type(GraphQLString))
            .field(newFieldDefinition<Any>()
                           .name("barkVolume")
                           .type(GraphQLInt))
            .field(newFieldDefinition<Any>()
                           .name("barks")
                           .type(GraphQLBoolean))
            .field(newFieldDefinition<Any>()
                           .name("doesKnowCommand")
                           .type(GraphQLBoolean)
                           .argument(newArgument()
                                             .name("dogCommand")
                                             .type(DogCommand)))
            .field(newFieldDefinition<Any>()
                           .name("isHousetrained")
                           .type(GraphQLBoolean)
                           .argument(newArgument()
                                             .name("atOtherHomes")
                                             .type(GraphQLBoolean)
                                             .defaultValue(true)))
            .field(newFieldDefinition<Any>()
                           .name("isAtLocation")
                           .type(GraphQLBoolean)
                           .argument(newArgument()
                                             .name("x")
                                             .type(GraphQLInt))
                           .argument(newArgument()
                                             .name("y")
                                             .type(GraphQLInt)))
            .withInterface(Being)
            .withInterface(Pet)
            .build()

    val FurColor = newEnum()
            .name("FurColor")
            .value("BROWN")
            .value("BLACK")
            .value("TAN")
            .value("SPOTTED")
            .build()


    val Cat = newObject()
            .name("Cat")
            .field(newFieldDefinition<Any>()
                           .name("name")
                           .type(GraphQLString))
            .field(newFieldDefinition<Any>()
                           .name("nickName")
                           .type(GraphQLString))
            .field(newFieldDefinition<Any>()
                           .name("meows")
                           .type(GraphQLBoolean))
            .field(newFieldDefinition<Any>()
                           .name("meowVolume")
                           .type(GraphQLInt))
            .field(newFieldDefinition<Any>()
                           .name("furColor")
                           .type(FurColor))
            .withInterfaces(Being, Pet)
            .build()

    val CatOrDog = newUnionType()
            .name("CatOrDog")
            .possibleTypes(Dog, Cat)
            .typeResolver(typeResolverProxy())
            .build()

    val Intelligent = newInterface()
            .name("Intelligent")
            .field(newFieldDefinition<Any>()
                           .name("iq")
                           .type(GraphQLInt))
            .typeResolver(dummyTypeResolve)
            .build()

    val Human = newObject()
            .name("Human")
            .field(newFieldDefinition<Any>()
                           .name("name")
                           .type(GraphQLString)
                           .argument(newArgument()
                                             .name("surname")
                                             .type(GraphQLBoolean)))
            .field(newFieldDefinition<Any>()
                           .name("pets")
                           .type(GraphQLList(Pet)))
            .field(newFieldDefinition<Any>()
                           .name("relatives")
                           .type(GraphQLList(GraphQLTypeReference("Human"))))
            .field(newFieldDefinition<Any>()
                           .name("iq")
                           .type(GraphQLInt))
            .withInterfaces(Being, Intelligent)
            .build()

    val Alien = newObject()
            .name("Alien")
            .field(newFieldDefinition<Any>()
                           .name("numEyes")
                           .type(GraphQLInt))
            .field(newFieldDefinition<Any>()
                           .name("iq")
                           .type(GraphQLInt))
            .withInterfaces(Being, Intelligent)
            .build()

    val DogOrHuman = newUnionType()
            .name("DogOrHuman")
            .possibleTypes(Dog, Human)
            .typeResolver(dummyTypeResolve)
            .build()

    val HumanOrAlien = newUnionType()
            .name("HumanOrAlien")
            .possibleTypes(Alien, Human)
            .typeResolver(dummyTypeResolve)
            .build()
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


    val QueryRoot = newObject()
            .name("QueryRoot")
            .field(newFieldDefinition<Any>()
                           .name("alien")
                           .type(Alien))
            .field(newFieldDefinition<Any>()
                           .name("dog")
                           .type(Dog))
            .field(newFieldDefinition<Any>()
                           .name("cat")
                           .type(Cat))
            .field(newFieldDefinition<Any>()
                           .name("pet")
                           .type(Pet))
            .field(newFieldDefinition<Any>()
                           .name("catOrDog")
                           .type(CatOrDog))

            .field(newFieldDefinition<Any>()
                           .name("dogOrHuman")
                           .type(DogOrHuman))
            .field(newFieldDefinition<Any>()
                           .name("humanOrAlien")
                           .type(HumanOrAlien))
            .build()

    val Schema = newSchema{
        query = QueryRoot
    }

}

