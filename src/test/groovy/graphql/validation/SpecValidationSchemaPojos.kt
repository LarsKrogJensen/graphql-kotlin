package graphql.validation

/**
 * Sample schema pojos used in the spec for validation examples
 * http://facebook.github.io/graphql/#sec-Validation
 * @author dwinsor
 */
class SpecValidationSchemaPojos {
    inner class Human {
        var name: String? = null
    }

    inner class Alien {
        var name: String? = null
    }

    inner class Dog {
        var name: String? = null
        var nickname: String? = null
        var barkVolume: Int = 0
        var doesKnowCommand: Boolean = false
        var isHousetrained: Boolean = false
        var owner: Human? = null
    }

    inner class Cat {
        var name: String? = null
        var nickname: String? = null
        var meowVolume: Int = 0
        var doesKnowCommand: Boolean = false
    }

    inner class QueryRoot {
        var dog: Dog? = null
    }
}
