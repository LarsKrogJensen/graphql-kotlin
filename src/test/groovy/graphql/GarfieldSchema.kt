package graphql


import graphql.schema.GraphQLList
import graphql.schema.GraphQLTypeReference
import graphql.schema.interfaceRef
import graphql.schema.newSchema
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

    val NamedType = graphql.schema.newInterface {
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

    val DogType = graphql.schema.newObject {
        name = "Dog"
        field<String> {
            name = "name"
        }
        field<Boolean> {
            name = "barks"
        }
        interfaces += interfaceRef("Named")
    }

    val CatType = graphql.schema.newObject {
        name = "Cat"
        field<String> {
            name = "name"
        }
        field<Boolean> {
            name = "meows"
        }
        interfaces += interfaceRef("Named")
    }

    val PetType = graphql.schema.newUnionType {
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

    val PersonType = graphql.schema.newObject {
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
        additionalTypes += NamedType
    }
}
