package graphql;


import graphql.schema.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLUnionType.newUnionType;

public class GarfieldSchema {

    public interface Named {
        String getName();
    }

    public static class Dog implements Named {
        private final String name;


        private final boolean barks;

        public Dog(String name, boolean barks) {
            this.name = name;
            this.barks = barks;
        }

        public boolean isBarks() {
            return barks;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static class Cat implements Named {
        private final String name;

        private final boolean meows;

        public Cat(String name, boolean meows) {
            this.name = name;
            this.meows = meows;
        }

        public boolean isMeows() {
            return meows;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static class Person implements Named {
        private final String name;
        private List<Dog> dogs;
        private List<Cat> cats;
        private List<Named> friends;

        public Person(String name) {
            this(name, Collections.<Cat>emptyList(), Collections.<Dog>emptyList(), Collections.<Named>emptyList());
        }

        public Person(String name, List<Cat> cats, List<Dog> dogs, List<Named> friends) {
            this.name = name;
            this.dogs = dogs;
            this.cats = cats;
            this.friends = friends;
        }

        public List<Object> getPets() {
            List<Object> pets = new ArrayList<Object>();
            pets.addAll(cats);
            pets.addAll(dogs);
            return pets;
        }

        @Override
        public String getName() {
            return name;
        }

        public List<Named> getFriends() {
            return friends;
        }
    }

    public static Cat garfield = new Cat("Garfield", false);
    public static Dog odie = new Dog("Odie", true);
    public static Person liz = new Person("Liz");
    public static Person john = new Person("John", Arrays.asList(garfield), Arrays.asList(odie), Arrays.asList(liz, odie));

    public static GraphQLInterfaceType NamedType = Companion.newInterface()
                                                            .name("Named")
                                                            .field(Companion.newFieldDefinition()
                                                                            .name("name")
                                                                            .type(INSTANCE.getGraphQLString()))
                                                            .typeResolver(new TypeResolver() {
                @Override
                public GraphQLObjectType getType(Object object) {
                    if (object instanceof Dog) {
                        return DogType;
                    }
                    if (object instanceof Person) {
                        return PersonType;
                    }
                    if (object instanceof Cat) {
                        return CatType;
                    }
                    return null;
                }
            })
                                                            .build();

    public static GraphQLObjectType DogType = Companion.newObject()
                                                       .name("Dog")
                                                       .field(Companion.newFieldDefinition()
                                                                       .name("name")
                                                                       .type(INSTANCE.getGraphQLString()))
                                                       .field(Companion.newFieldDefinition()
                                                                       .name("barks")
                                                                       .type(INSTANCE.getGraphQLBoolean()))
                                                       .withInterface(NamedType)
                                                       .build();

    public static GraphQLObjectType CatType = Companion.newObject()
                                                       .name("Cat")
                                                       .field(Companion.newFieldDefinition()
                                                                       .name("name")
                                                                       .type(INSTANCE.getGraphQLString()))
                                                       .field(Companion.newFieldDefinition()
                                                                       .name("meows")
                                                                       .type(INSTANCE.getGraphQLBoolean()))
                                                       .withInterface(NamedType)
                                                       .build();

    public static GraphQLUnionType PetType = GraphQLUnionType.Companion.newUnionType()
                                                                       .name("Pet")
                                                                       .possibleType(CatType)
                                                                       .possibleType(DogType)
                                                                       .typeResolver(new TypeResolver() {
                @Override
                public GraphQLObjectType getType(Object object) {
                    if (object instanceof Cat) {
                        return CatType;
                    }
                    if (object instanceof Dog) {
                        return DogType;
                    }
                    return null;
                }
            })
                                                                       .build();

    public static GraphQLObjectType PersonType = Companion.newObject()
                                                          .name("Person")
                                                          .field(Companion.newFieldDefinition()
                                                                          .name("name")
                                                                          .type(INSTANCE.getGraphQLString()))
                                                          .field(Companion.newFieldDefinition()
                                                                          .name("pets")
                                                                          .type(new GraphQLList(PetType)))
                                                          .field(Companion.newFieldDefinition()
                                                                          .name("friends")
                                                                          .type(new GraphQLList(NamedType)))
                                                          .withInterface(NamedType)
                                                          .build();

    public static GraphQLSchema GarfieldSchema = GraphQLSchema.Companion.newSchema()
                                                                        .query(PersonType)
                                                                        .build();


}
