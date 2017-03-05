package graphql.validation.rules;

import graphql.schema.*;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLEnumType.newEnum;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;
import static graphql.schema.GraphQLUnionType.newUnionType;


public class Harness {

    private static TypeResolver dummyTypeResolve = new TypeResolver() {
        @Override
        public GraphQLObjectType getType(Object object) {
            return null;
        }
    };


    public static GraphQLInterfaceType Being = Companion.newInterface()
                                                        .name("Being")
                                                        .field(Companion.newFieldDefinition()
                                                                        .name("name")
                                                                        .type(INSTANCE.getGraphQLString()))
                                                        .typeResolver(dummyTypeResolve)
                                                        .build();

    public static GraphQLInterfaceType Pet = Companion.newInterface()
                                                      .name("Pet")
                                                      .field(Companion.newFieldDefinition()
                                                                      .name("name")
                                                                      .type(INSTANCE.getGraphQLString()))
                                                      .typeResolver(dummyTypeResolve)
                                                      .build();

    public static GraphQLEnumType DogCommand = Companion.newEnum()
                                                        .name("DogCommand")
                                                        .value("SIT")
                                                        .value("HEEL")
                                                        .value("DOWN")
                                                        .build();

    public static GraphQLObjectType Dog = Companion.newObject()
                                                   .name("Dog")
                                                   .field(Companion.newFieldDefinition()
                                                                   .name("name")
                                                                   .type(INSTANCE.getGraphQLString()))
                                                   .field(Companion.newFieldDefinition()
                                                                   .name("nickName")
                                                                   .type(INSTANCE.getGraphQLString()))
                                                   .field(Companion.newFieldDefinition()
                                                                   .name("barkVolume")
                                                                   .type(INSTANCE.getGraphQLInt()))
                                                   .field(Companion.newFieldDefinition()
                                                                   .name("barks")
                                                                   .type(INSTANCE.getGraphQLBoolean()))
                                                   .field(Companion.newFieldDefinition()
                                                                   .name("doesKnowCommand")
                                                                   .type(INSTANCE.getGraphQLBoolean())
                                                                   .argument(Companion.newArgument()
                                       .name("dogCommand")
                                       .type(DogCommand)))
                                                   .field(Companion.newFieldDefinition()
                                                                   .name("isHousetrained")
                                                                   .type(INSTANCE.getGraphQLBoolean())
                                                                   .argument(Companion.newArgument()
                                       .name("atOtherHomes")
                                       .type(INSTANCE.getGraphQLBoolean())
                                       .defaultValue(true)))
                                                   .field(Companion.newFieldDefinition()
                                                                   .name("isAtLocation")
                                                                   .type(INSTANCE.getGraphQLBoolean())
                                                                   .argument(Companion.newArgument()
                                       .name("x")
                                       .type(INSTANCE.getGraphQLInt()))
                                                                   .argument(Companion.newArgument()
                                       .name("y")
                                       .type(INSTANCE.getGraphQLInt())))
                                                   .withInterface(Being)
                                                   .withInterface(Pet)
                                                   .build();

    public static GraphQLEnumType FurColor = Companion.newEnum()
                                                      .name("FurColor")
                                                      .value("BROWN")
                                                      .value("BLACK")
                                                      .value("TAN")
                                                      .value("SPOTTED")
                                                      .build();


    public static GraphQLObjectType Cat = Companion.newObject()
                                                   .name("Cat")
                                                   .field(Companion.newFieldDefinition()
                                                                   .name("name")
                                                                   .type(INSTANCE.getGraphQLString()))
                                                   .field(Companion.newFieldDefinition()
                                                                   .name("nickName")
                                                                   .type(INSTANCE.getGraphQLString()))
                                                   .field(Companion.newFieldDefinition()
                                                                   .name("meows")
                                                                   .type(INSTANCE.getGraphQLBoolean()))
                                                   .field(Companion.newFieldDefinition()
                                                                   .name("meowVolume")
                                                                   .type(INSTANCE.getGraphQLInt()))
                                                   .field(Companion.newFieldDefinition()
                                                                   .name("furColor")
                                                                   .type(FurColor))
                                                   .withInterfaces(Being, Pet)
                                                   .build();

    public static GraphQLUnionType CatOrDog = Companion.newUnionType()
                                                       .name("CatOrDog")
                                                       .possibleTypes(Dog, Cat)
                                                       .typeResolver(new TypeResolver() {
                @Override
                public GraphQLObjectType getType(Object object) {
                    return null;
                }
            })
                                                       .build();

    public static GraphQLInterfaceType Intelligent = Companion.newInterface()
                                                              .name("Intelligent")
                                                              .field(Companion.newFieldDefinition()
                                                                              .name("iq")
                                                                              .type(INSTANCE.getGraphQLInt()))
                                                              .typeResolver(dummyTypeResolve)
                                                              .build();

    public static GraphQLObjectType Human = Companion.newObject()
                                                     .name("Human")
                                                     .field(Companion.newFieldDefinition()
                                                                     .name("name")
                                                                     .type(INSTANCE.getGraphQLString())
                                                                     .argument(Companion.newArgument()
                                       .name("surname")
                                       .type(INSTANCE.getGraphQLBoolean())))
                                                     .field(Companion.newFieldDefinition()
                                                                     .name("pets")
                                                                     .type(new GraphQLList(Pet)))
                                                     .field(Companion.newFieldDefinition()
                                                                     .name("relatives")
                                                                     .type(new GraphQLList(new GraphQLTypeReference("Human"))))
                                                     .field(Companion.newFieldDefinition()
                                                                     .name("iq")
                                                                     .type(INSTANCE.getGraphQLInt()))
                                                     .withInterfaces(Being, Intelligent)
                                                     .build();

    public static GraphQLObjectType Alien = Companion.newObject()
                                                     .name("Alien")
                                                     .field(Companion.newFieldDefinition()
                                                                     .name("numEyes")
                                                                     .type(INSTANCE.getGraphQLInt()))
                                                     .field(Companion.newFieldDefinition()
                                                                     .name("iq")
                                                                     .type(INSTANCE.getGraphQLInt()))
                                                     .withInterfaces(Being, Intelligent)
                                                     .build();

    public static GraphQLUnionType DogOrHuman = Companion.newUnionType()
                                                         .name("DogOrHuman")
                                                         .possibleTypes(Dog, Human)
                                                         .typeResolver(dummyTypeResolve)
                                                         .build();

    public static GraphQLUnionType HumanOrAlien = Companion.newUnionType()
                                                           .name("HumanOrAlien")
                                                           .possibleTypes(Alien, Human)
                                                           .typeResolver(dummyTypeResolve)
                                                           .build();
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


    public static GraphQLObjectType QueryRoot = Companion.newObject()
                                                         .name("QueryRoot")
                                                         .field(Companion.newFieldDefinition()
                                                                         .name("alien")
                                                                         .type(Alien))
                                                         .field(Companion.newFieldDefinition()
                                                                         .name("dog")
                                                                         .type(Dog))
                                                         .field(Companion.newFieldDefinition()
                                                                         .name("cat")
                                                                         .type(Cat))
                                                         .field(Companion.newFieldDefinition()
                                                                         .name("pet")
                                                                         .type(Pet))
                                                         .field(Companion.newFieldDefinition()
                                                                         .name("catOrDog")
                                                                         .type(CatOrDog))

                                                         .field(Companion.newFieldDefinition()
                                                                         .name("dogOrHuman")
                                                                         .type(DogOrHuman))
                                                         .field(Companion.newFieldDefinition()
                                                                         .name("humanOrAlien")
                                                                         .type(HumanOrAlien))
                                                         .build();

    public static GraphQLSchema Schema = Companion.newSchema()
                                                  .query(QueryRoot)
                                                  .build();


}

