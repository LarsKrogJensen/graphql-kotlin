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

    val NamedType = newInterface {
        name = "Named"
        field<String> {
            name = "name"
        }
        typeResolver {
            when (it) {
                is Dog    -> DogType
                is Person -> PersonType
                is Cat    -> CatType
                else      -> null
            }
        }
    }

    val DogType = newObject {
        name = "Dog"
        field<String> {
            name = "name"
        }
        field<Boolean> {
            name = "barks"
        }
        interfaces += interfaceRef("Named")
    }

    val CatType = newObject {
        name = "Cat"
        field<String> {
            name = "name"
        }
        field<Boolean> {
            name = "meows"
        }
        interfaces += interfaceRef("Named")
    }

    val PetType = newUnionType {
        name = "Pet"
        types += CatType
        types += DogType
        typeResolver = {
            when (it) {
                is Cat -> CatType
                is Dog -> DogType
                else   -> null
            }
        }
    }

    val PersonType = newObject {
        name = "Person"
        field<String> {
            name = "name"
        }
        field<List<Any>> {
            name = "pets"
            type = GraphQLList(PetType)
        }
        field<List<Named>> {
            name = "friends"
            type = GraphQLList(GraphQLTypeReference("Named"))
        }
        interfaces += interfaceRef("Named")
    }

    val GarfieldSchema = newSchema {
        query = PersonType
        dictionary += NamedType
    }
}
