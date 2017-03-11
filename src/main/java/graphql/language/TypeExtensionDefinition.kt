package graphql.language




class TypeExtensionDefinition(name: String) : ObjectTypeDefinition(name) {

    override fun toString(): String {
        return "TypeExtensionDefinition{" +
                "name='" + name + '\'' +
                ", implements=" + implements +
                ", directives=" + directives +
                ", fieldDefinitions=" + fieldDefinitions +
                '}'
    }
}
