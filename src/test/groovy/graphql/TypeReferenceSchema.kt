package graphql

import graphql.GarfieldSchema.CatType
import graphql.GarfieldSchema.DogType
import graphql.GarfieldSchema.NamedType
import graphql.schema.*

val PetType = newUnionType {
    name = "Pet"
    types += objectRef(CatType.name)
    types += objectRef(DogType.name)
    typeResolver = typeResolverProxy()
}

val PersonInputType = newInputObject {
    name ="Person_Input"
    field {
        name = "name"
        type = GraphQLString
    }
}

val PersonType = newObject {
    name = "Person"
    field<String> {
        name = "name"
    }
    field<Any> {
        name = "pet"
        type = typeRef(PetType.name)
    }
    interfaces += interfaceRef(NamedType.name)
}

val exists = newField<Any>{
    name = "exists"
    type = GraphQLBoolean
    argument {
        name = "person"
        type = inputRef("Person_Input")
    }
}

val find = newField<Any>{
    name = "find"
    type = typeRef("Person")
    argument {
        name ="name"
        type = GraphQLString
    }
}
val PersonService = newObject {
    name = "PersonService"
    fields += exists
    fields += find
}

val SchemaWithReferences = newSchema {
    query = PersonService
    dictionary = setOf(PersonType, PersonInputType, PetType, CatType, DogType, NamedType)
}
