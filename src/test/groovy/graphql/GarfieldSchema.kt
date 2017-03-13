package graphql


import graphql.schema.*
import graphql.schema.GraphQLFieldDefinition.Companion.newFieldDefinition
import graphql.schema.GraphQLInterfaceType.Companion.newInterface
import graphql.schema.GraphQLObjectType.Companion.newObject
import graphql.schema.GraphQLUnionType.Companion.newUnionType
import java.util.*

object GarfieldSchema {

    interface Named {
        val name: String
    }

    class Dog(override val name: String, val isBarks: Boolean) : Named

    class Cat(override val name: String, val isMeows: Boolean) : Named

    class Person(override val name: String,
                 private val cats: List<Cat> = emptyList<Cat>(),
                 private val dogs: List<Dog> = emptyList<Dog>(),
                 val friends: List<Named> = emptyList<Named>()) : Named {

        val pets: List<Any>
            get() {
                val pets = ArrayList<Any>()
                pets.addAll(cats)
                pets.addAll(dogs)
                return pets
            }
    }

    val garfield = Cat("Garfield", false)
    val odie = Dog("Odie", true)
    val liz = Person("Liz")
    val john = Person("John", Arrays.asList(garfield), Arrays.asList(odie), Arrays.asList(liz, odie))

    val NamedType = newInterface()
            .name("Named")
            .field(newFieldDefinition<String>()
                           .name("name")
                           .type(GraphQLString))
            .typeResolver {
                when (it) {
                    is Dog    -> DogType
                    is Person -> PersonType
                    is Cat    -> CatType
                    else      -> null
                }

            }.build()

    val DogType = newObject()
            .name("Dog")
            .field(newFieldDefinition<String>()
                           .name("name")
                           .type(GraphQLString))
            .field(newFieldDefinition<String>()
                           .name("barks")
                           .type(GraphQLBoolean))
            .withInterface(GraphQLInterfaceType.reference("Named"))
            .build()

    val CatType = newObject()
            .name("Cat")
            .field(newFieldDefinition<String>()
                           .name("name")
                           .type(GraphQLString))
            .field(newFieldDefinition<String>()
                           .name("meows")
                           .type(GraphQLBoolean))
            .withInterface(GraphQLInterfaceType.reference("Named"))
            .build()

    val PetType = newUnionType()
            .name("Pet")
            .possibleType(CatType)
            .possibleType(DogType)
            .typeResolver {
                when (it) {
                    is Cat -> CatType
                    is Dog -> DogType
                    else   -> null
                }
            }
            .build()

    val PersonType = newObject()
            .name("Person")
            .field(newFieldDefinition<String>()
                           .name("name")
                           .type(GraphQLString))
            .field(newFieldDefinition<List<Any>>()
                           .name("pets")
                           .type(GraphQLList(PetType)))
            .field(newFieldDefinition<List<Named>>()
                           .name("friends")
                           .type(GraphQLList(GraphQLTypeReference("Named"))))
            .withInterface(GraphQLInterfaceType.reference("Named"))
            .build()

    val GarfieldSchema = GraphQLSchema.newSchema()
            .query(PersonType)
            .build(setOf(NamedType))


}
