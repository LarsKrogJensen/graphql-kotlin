package graphql.parser


import graphql.ShouldNotHappenException
import graphql.language.*
import graphql.parser.antlr.GraphqlBaseVisitor
import graphql.parser.antlr.GraphqlParser
import org.antlr.v4.runtime.ParserRuleContext
import java.io.StringWriter
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

class GraphqlAntlrToLanguage : GraphqlBaseVisitor<Void>() {

    internal var result: Document = Document()

    private enum class ContextProperty {
        OperationDefinition,
        FragmentDefinition,
        Field,
        InlineFragment,
        FragmentSpread,
        SelectionSet,
        VariableDefinition,
        ListType,
        NonNullType,
        Directive,
        EnumTypeDefinition,
        ObjectTypeDefinition,
        InputObjectTypeDefinition,
        ScalarTypeDefinition,
        UnionTypeDefinition,
        InterfaceTypeDefinition,
        EnumValueDefinition,
        FieldDefinition,
        InputValueDefinition,
        TypeExtensionDefinition,
        SchemaDefinition,
        OperationTypeDefinition,
        DirectiveDefinition
    }

    private class ContextEntry(var contextProperty: ContextProperty, var value: Any)


    private val contextStack = ArrayDeque<ContextEntry>()


    private fun addContextProperty(contextProperty: ContextProperty, value: Any) {

        when (contextProperty) {
            ContextProperty.SelectionSet -> newSelectionSet(value as SelectionSet)
            ContextProperty.Field        -> newField(value as Field)
        }
        contextStack.addFirst(ContextEntry(contextProperty, value))
    }

    private fun popContext() {
        contextStack.removeFirst()
    }

    private fun <T> fromContextStack(contextProperty: ContextProperty): T? {
        for (contextEntry in contextStack) {
            if (contextEntry.contextProperty == contextProperty) {
                return contextEntry.value as T
            }
        }
        return null
    }

    private fun newSelectionSet(selectionSet: SelectionSet) {
        for (contextEntry in contextStack) {
            if (contextEntry.contextProperty == ContextProperty.Field) {
                (contextEntry.value as Field).selectionSet = selectionSet
                break
            } else if (contextEntry.contextProperty == ContextProperty.OperationDefinition) {
                (contextEntry.value as OperationDefinition).selectionSet = selectionSet
                break
            } else if (contextEntry.contextProperty == ContextProperty.FragmentDefinition) {
                (contextEntry.value as FragmentDefinition).selectionSet = selectionSet
                break
            } else if (contextEntry.contextProperty == ContextProperty.InlineFragment) {
                (contextEntry.value as InlineFragment).selectionSet = selectionSet
                break
            }
        }
    }

    private fun newField(field: Field) {
        fromContextStack<SelectionSet>(ContextProperty.SelectionSet)?.selections?.add(field)
    }


    override fun visitDocument(ctx: GraphqlParser.DocumentContext): Void? {
        result = Document()
        newNode(result, ctx)
        return super.visitDocument(ctx)
    }

    override fun visitOperationDefinition(ctx: GraphqlParser.OperationDefinitionContext): Void? {
        val op = if (ctx.operationType() == null) {
            OperationDefinition.Operation.QUERY
        } else {
            parseOperation(ctx.operationType())
        }
        val operationDefinition = OperationDefinition(op)
        newNode(operationDefinition, ctx)
        if (ctx.name() != null) {
            operationDefinition.name = ctx.name().text
        }
        result.add(operationDefinition)
        addContextProperty(ContextProperty.OperationDefinition, operationDefinition)
        super.visitOperationDefinition(ctx)
        popContext()

        return null
    }

    private fun parseOperation(operationTypeContext: GraphqlParser.OperationTypeContext): OperationDefinition.Operation {
        if (operationTypeContext.text == "query") {
            return OperationDefinition.Operation.QUERY
        } else if (operationTypeContext.text == "mutation") {
            return OperationDefinition.Operation.MUTATION
        } else {
            throw RuntimeException("InternalError: unknown operationTypeContext=" + operationTypeContext.text)
        }
    }

    override fun visitFragmentSpread(ctx: GraphqlParser.FragmentSpreadContext): Void? {
        val fragmentSpread = FragmentSpread(ctx.fragmentName().text)
        newNode(fragmentSpread, ctx)
        fromContextStack<SelectionSet>(ContextProperty.SelectionSet)?.selections?.add(fragmentSpread)
        addContextProperty(ContextProperty.FragmentSpread, fragmentSpread)
        super.visitFragmentSpread(ctx)
        popContext()
        return null
    }

    override fun visitVariableDefinition(ctx: GraphqlParser.VariableDefinitionContext): Void? {
        val variableDefinition = VariableDefinition(ctx.variable().name().text)
        newNode(variableDefinition, ctx)
        if (ctx.defaultValue() != null) {
            val value = getValue(ctx.defaultValue().value())
            variableDefinition.defaultValue = value
        }
        val operationDefinition = fromContextStack<OperationDefinition>(ContextProperty.OperationDefinition)
        operationDefinition?.add(variableDefinition)

        addContextProperty(ContextProperty.VariableDefinition, variableDefinition)
        super.visitVariableDefinition(ctx)
        popContext()
        return null
    }

    override fun visitFragmentDefinition(ctx: GraphqlParser.FragmentDefinitionContext): Void? {
        val fragmentDefinition = FragmentDefinition(ctx.fragmentName().text, TypeName(ctx.typeCondition().typeName().text))
        newNode(fragmentDefinition, ctx)
        addContextProperty(ContextProperty.FragmentDefinition, fragmentDefinition)
        result.add(fragmentDefinition)
        super.visitFragmentDefinition(ctx)
        popContext()
        return null
    }


    override fun visitSelectionSet(ctx: GraphqlParser.SelectionSetContext): Void? {
        val newSelectionSet = SelectionSet()
        newNode(newSelectionSet, ctx)
        addContextProperty(ContextProperty.SelectionSet, newSelectionSet)
        super.visitSelectionSet(ctx)
        popContext()
        return null
    }


    override fun visitField(ctx: GraphqlParser.FieldContext): Void? {
        val newField = Field(ctx.name().text)
        newNode(newField, ctx)
        if (ctx.alias() != null) {
            newField.alias = ctx.alias().name().text
        }
        addContextProperty(ContextProperty.Field, newField)
        super.visitField(ctx)
        popContext()
        return null
    }

    override fun visitTypeName(ctx: GraphqlParser.TypeNameContext): Void? {
        val typeName = TypeName(ctx.name().text)
        newNode(typeName, ctx)
        for (contextEntry in contextStack) {
            val value = contextEntry.value
            if (value is ListType) {
                value.type = typeName
                break
            }
            if (value is NonNullType) {
                value.type = typeName
                break
            }
            if (value is VariableDefinition) {
                value.type = typeName
                break
            }
            if (value is FieldDefinition) {
                value.type = typeName
                break
            }
            if (value is InputValueDefinition) {
                value.type = typeName
                break
            }
            if (contextEntry.contextProperty == ContextProperty.ObjectTypeDefinition) {
                (value as ObjectTypeDefinition).implements.add(typeName)
                break
            }
            if (contextEntry.contextProperty == ContextProperty.UnionTypeDefinition) {
                (value as UnionTypeDefinition).memberTypes.add(typeName)
                break
            }
            if (contextEntry.contextProperty == ContextProperty.OperationTypeDefinition) {
                (value as OperationTypeDefinition).type = typeName
                break
            }
        }
        return super.visitTypeName(ctx)
    }

    override fun visitNonNullType(ctx: GraphqlParser.NonNullTypeContext): Void? {
        val nonNullType = NonNullType()
        newNode(nonNullType, ctx)
        for (contextEntry in contextStack) {
            val value = contextEntry.value
            if (value is ListType) {
                value.type = nonNullType
                break
            }
            if (value is VariableDefinition) {
                value.type = nonNullType
                break
            }
            if (value is FieldDefinition) {
                value.type = nonNullType
                break
            }
            if (value is InputValueDefinition) {
                value.type = nonNullType
                break
            }
        }
        addContextProperty(ContextProperty.NonNullType, nonNullType)
        super.visitNonNullType(ctx)
        popContext()
        return null
    }

    override fun visitListType(ctx: GraphqlParser.ListTypeContext): Void? {
        val listType = ListType()
        newNode(listType, ctx)
        for (contextEntry in contextStack) {
            val value = contextEntry.value
            if (value is ListType) {
                value.type = listType
                break
            }
            if (value is NonNullType) {
                value.type = listType
                break
            }
            if (value is VariableDefinition) {
                value.type = listType
                break
            }
            if (value is FieldDefinition) {
                value.type = listType
                break
            }
            if (value is InputValueDefinition) {
                value.type = listType
                break
            }
        }
        addContextProperty(ContextProperty.ListType, listType)
        super.visitListType(ctx)
        popContext()
        return null
    }

    override fun visitArgument(ctx: GraphqlParser.ArgumentContext): Void? {
        val argument = Argument(ctx.name().text, getValue(ctx.valueWithVariable()))
        newNode(argument, ctx)
        val directive = fromContextStack<Directive>(ContextProperty.Directive)
        if (directive != null) {
            directive.arguments.add(argument)
        } else {
            val field = fromContextStack<Field>(ContextProperty.Field)
            field?.arguments?.add(argument)
        }
        return super.visitArgument(ctx)
    }

    override fun visitInlineFragment(ctx: GraphqlParser.InlineFragmentContext): Void? {
        val typeName = if (ctx.typeCondition() != null) TypeName(ctx.typeCondition().typeName().text) else null
        val inlineFragment = InlineFragment(typeName)
        newNode(inlineFragment, ctx)
        fromContextStack<SelectionSet>(ContextProperty.SelectionSet)?.selections?.add(inlineFragment)
        addContextProperty(ContextProperty.InlineFragment, inlineFragment)
        super.visitInlineFragment(ctx)
        popContext()
        return null
    }

    override fun visitDirective(ctx: GraphqlParser.DirectiveContext): Void? {
        val directive = Directive(ctx.name().text)
        newNode(directive, ctx)
        for (contextEntry in contextStack) {
            val contextProperty = contextEntry.contextProperty
            if (contextProperty == ContextProperty.Field) {
                (contextEntry.value as Field).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.FragmentDefinition) {
                (contextEntry.value as FragmentDefinition).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.FragmentSpread) {
                (contextEntry.value as FragmentSpread).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.InlineFragment) {
                (contextEntry.value as InlineFragment).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.OperationDefinition) {
                (contextEntry.value as OperationDefinition).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.EnumValueDefinition) {
                (contextEntry.value as EnumValueDefinition).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.FieldDefinition) {
                (contextEntry.value as FieldDefinition).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.InputValueDefinition) {
                (contextEntry.value as InputValueDefinition).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.InterfaceTypeDefinition) {
                (contextEntry.value as InterfaceTypeDefinition).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.EnumTypeDefinition) {
                (contextEntry.value as EnumTypeDefinition).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.ObjectTypeDefinition) {
                (contextEntry.value as ObjectTypeDefinition).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.ScalarTypeDefinition) {
                (contextEntry.value as ScalarTypeDefinition).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.UnionTypeDefinition) {
                (contextEntry.value as UnionTypeDefinition).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.InputObjectTypeDefinition) {
                (contextEntry.value as InputObjectTypeDefinition).directives.add(directive)
                break
            } else if (contextProperty == ContextProperty.SchemaDefinition) {
                (contextEntry.value as SchemaDefinition).directives.add(directive)
                break
            }
        }
        addContextProperty(ContextProperty.Directive, directive)
        super.visitDirective(ctx)
        popContext()
        return null
    }

    override fun visitSchemaDefinition(ctx: GraphqlParser.SchemaDefinitionContext): Void? {
        val def = SchemaDefinition()
        newNode(def, ctx)
        result.definitions.add(def)
        addContextProperty(ContextProperty.SchemaDefinition, def)
        super.visitChildren(ctx)
        popContext()
        return null
    }

    override fun visitOperationTypeDefinition(ctx: GraphqlParser.OperationTypeDefinitionContext): Void? {
        val def = OperationTypeDefinition(ctx.operationType().text)
        newNode(def, ctx)
        for (contextEntry in contextStack) {
            if (contextEntry.contextProperty == ContextProperty.SchemaDefinition) {
                (contextEntry.value as SchemaDefinition).operationTypeDefinitions.add(def)
                break
            }
        }
        addContextProperty(ContextProperty.OperationTypeDefinition, def)
        super.visitChildren(ctx)
        popContext()
        return null
    }

    override fun visitScalarTypeDefinition(ctx: GraphqlParser.ScalarTypeDefinitionContext): Void? {
        val def = ScalarTypeDefinition(ctx.name().text)
        newNode(def, ctx)
        result.definitions.add(def)
        addContextProperty(ContextProperty.ScalarTypeDefinition, def)
        super.visitChildren(ctx)
        popContext()
        return null
    }

    override fun visitObjectTypeDefinition(ctx: GraphqlParser.ObjectTypeDefinitionContext): Void? {
        var def: ObjectTypeDefinition? = null
        for (contextEntry in contextStack) {
            if (contextEntry.contextProperty == ContextProperty.TypeExtensionDefinition) {
                (contextEntry.value as TypeExtensionDefinition).name = ctx.name().text
                def = contextEntry.value as ObjectTypeDefinition
                break
            }
        }
        if (null == def) {
            def = ObjectTypeDefinition(ctx.name().text)
            newNode(def, ctx)
            result.definitions.add(def)
        }
        addContextProperty(ContextProperty.ObjectTypeDefinition, def)
        super.visitChildren(ctx)
        popContext()
        return null
    }

    override fun visitFieldDefinition(ctx: GraphqlParser.FieldDefinitionContext): Void? {
        val def = FieldDefinition(ctx.name().text)
        newNode(def, ctx)
        for (contextEntry in contextStack) {
            if (contextEntry.contextProperty == ContextProperty.InterfaceTypeDefinition) {
                (contextEntry.value as InterfaceTypeDefinition).fieldDefinitions.add(def)
                break
            }
            if (contextEntry.contextProperty == ContextProperty.ObjectTypeDefinition) {
                (contextEntry.value as ObjectTypeDefinition).fieldDefinitions.add(def)
                break
            }
        }
        addContextProperty(ContextProperty.FieldDefinition, def)
        super.visitChildren(ctx)
        popContext()
        return null
    }

    override fun visitInputValueDefinition(ctx: GraphqlParser.InputValueDefinitionContext): Void? {
        val def = InputValueDefinition(ctx.name().text)
        newNode(def, ctx)
        if (ctx.defaultValue() != null) {
            def.setValue(getValue(ctx.defaultValue().value()))
        }
        for (contextEntry in contextStack) {
            if (contextEntry.contextProperty == ContextProperty.FieldDefinition) {
                (contextEntry.value as FieldDefinition).inputValueDefinitions.add(def)
                break
            }
            if (contextEntry.contextProperty == ContextProperty.InputObjectTypeDefinition) {
                (contextEntry.value as InputObjectTypeDefinition).inputValueDefinitions.add(def)
                break
            }
            if (contextEntry.contextProperty == ContextProperty.DirectiveDefinition) {
                (contextEntry.value as DirectiveDefinition).inputValueDefinitions.add(def)
                break
            }
        }
        addContextProperty(ContextProperty.InputValueDefinition, def)
        super.visitChildren(ctx)
        popContext()
        return null
    }

    override fun visitInterfaceTypeDefinition(ctx: GraphqlParser.InterfaceTypeDefinitionContext): Void? {
        val def = InterfaceTypeDefinition(ctx.name().text)
        newNode(def, ctx)
        result.definitions.add(def)
        addContextProperty(ContextProperty.InterfaceTypeDefinition, def)
        super.visitChildren(ctx)
        popContext()
        return null
    }

    override fun visitUnionTypeDefinition(ctx: GraphqlParser.UnionTypeDefinitionContext): Void? {
        val def = UnionTypeDefinition(ctx.name().text)
        newNode(def, ctx)
        result.definitions.add(def)
        addContextProperty(ContextProperty.UnionTypeDefinition, def)
        super.visitChildren(ctx)
        popContext()
        return null
    }

    override fun visitEnumTypeDefinition(ctx: GraphqlParser.EnumTypeDefinitionContext): Void? {
        val def = EnumTypeDefinition(ctx.name().text)
        newNode(def, ctx)
        result.definitions.add(def)
        addContextProperty(ContextProperty.EnumTypeDefinition, def)
        super.visitChildren(ctx)
        popContext()
        return null
    }

    override fun visitEnumValueDefinition(ctx: GraphqlParser.EnumValueDefinitionContext): Void? {
        val enumValue = EnumValueDefinition(ctx.enumValue().text)
        newNode(enumValue, ctx)
        for (contextEntry in contextStack) {
            if (contextEntry.contextProperty == ContextProperty.EnumTypeDefinition) {
                (contextEntry.value as EnumTypeDefinition).enumValueDefinitions.add(enumValue)
                break
            }
        }
        addContextProperty(ContextProperty.EnumValueDefinition, enumValue)
        super.visitChildren(ctx)
        popContext()
        return null
    }

    override fun visitInputObjectTypeDefinition(ctx: GraphqlParser.InputObjectTypeDefinitionContext): Void? {
        val def = InputObjectTypeDefinition(ctx.name().text)
        newNode(def, ctx)
        result.definitions.add(def)
        addContextProperty(ContextProperty.InputObjectTypeDefinition, def)
        super.visitChildren(ctx)
        popContext()
        return null
    }

    override fun visitTypeExtensionDefinition(ctx: GraphqlParser.TypeExtensionDefinitionContext): Void? {
        val def = TypeExtensionDefinition("?")
        newNode(def, ctx)
        result.definitions.add(def)
        addContextProperty(ContextProperty.TypeExtensionDefinition, def)
        super.visitChildren(ctx)
        popContext()
        return null
    }

    override fun visitDirectiveDefinition(ctx: GraphqlParser.DirectiveDefinitionContext): Void? {
        val def = DirectiveDefinition(ctx.name().text)
        newNode(def, ctx)
        result.definitions.add(def)
        addContextProperty(ContextProperty.DirectiveDefinition, def)
        super.visitChildren(ctx)
        popContext()
        return null
    }

    override fun visitDirectiveLocation(ctx: GraphqlParser.DirectiveLocationContext): Void? {
        val def = DirectiveLocation(ctx.name().text)
        newNode(def, ctx)
        for (contextEntry in contextStack) {
            if (contextEntry.contextProperty == ContextProperty.DirectiveDefinition) {
                (contextEntry.value as DirectiveDefinition).directiveLocations.add(def)
                break
            }
        }
        super.visitChildren(ctx)
        return null
    }

    private fun getValue(ctx: GraphqlParser.ValueWithVariableContext): Value {
        if (ctx.IntValue() != null) {
            val intValue = IntValue(BigInteger(ctx.IntValue().text))
            newNode(intValue, ctx)
            return intValue
        } else if (ctx.FloatValue() != null) {
            val floatValue = FloatValue(BigDecimal(ctx.FloatValue().text))
            newNode(floatValue, ctx)
            return floatValue
        } else if (ctx.BooleanValue() != null) {
            val booleanValue = BooleanValue(java.lang.Boolean.parseBoolean(ctx.BooleanValue().text))
            newNode(booleanValue, ctx)
            return booleanValue
        } else if (ctx.StringValue() != null) {
            val stringValue = StringValue(parseString(ctx.StringValue().text))
            newNode(stringValue, ctx)
            return stringValue
        } else if (ctx.enumValue() != null) {
            val enumValue = EnumValue(ctx.enumValue().text)
            newNode(enumValue, ctx)
            return enumValue
        } else if (ctx.arrayValueWithVariable() != null) {
            val arrayValue = ArrayValue()
            newNode(arrayValue, ctx)
            for (valueWithVariableContext in ctx.arrayValueWithVariable().valueWithVariable()) {
                arrayValue.values.add(getValue(valueWithVariableContext))
            }
            return arrayValue
        } else if (ctx.objectValueWithVariable() != null) {
            val objectValue = ObjectValue()
            newNode(objectValue, ctx)
            for (objectFieldWithVariableContext in ctx.objectValueWithVariable().objectFieldWithVariable()) {
                val objectField = ObjectField(objectFieldWithVariableContext.name().text,
                                              getValue(objectFieldWithVariableContext.valueWithVariable()))
                objectValue.objectFields.add(objectField)
            }
            return objectValue
        } else if (ctx.variable() != null) {
            val variableReference = VariableReference(ctx.variable().name().text)
            newNode(variableReference, ctx)
            return variableReference
        }
        throw ShouldNotHappenException()
    }

    private fun getValue(ctx: GraphqlParser.ValueContext): Value {
        if (ctx.IntValue() != null) {
            val intValue = IntValue(BigInteger(ctx.IntValue().text))
            newNode(intValue, ctx)
            return intValue
        } else if (ctx.FloatValue() != null) {
            val floatValue = FloatValue(BigDecimal(ctx.FloatValue().text))
            newNode(floatValue, ctx)
            return floatValue
        } else if (ctx.BooleanValue() != null) {
            val booleanValue = BooleanValue(java.lang.Boolean.parseBoolean(ctx.BooleanValue().text))
            newNode(booleanValue, ctx)
            return booleanValue
        } else if (ctx.StringValue() != null) {
            val stringValue = StringValue(parseString(ctx.StringValue().text))
            newNode(stringValue, ctx)
            return stringValue
        } else if (ctx.enumValue() != null) {
            val enumValue = EnumValue(ctx.enumValue().text)
            newNode(enumValue, ctx)
            return enumValue
        } else if (ctx.arrayValue() != null) {
            val arrayValue = ArrayValue()
            newNode(arrayValue, ctx)
            for (valueWithVariableContext in ctx.arrayValue().value()) {
                arrayValue.values.add(getValue(valueWithVariableContext))
            }
            return arrayValue
        } else if (ctx.objectValue() != null) {
            val objectValue = ObjectValue()
            newNode(objectValue, ctx)
            for (objectFieldContext in ctx.objectValue().objectField()) {
                val objectField = ObjectField(objectFieldContext.name().text, getValue(objectFieldContext.value()))
                objectValue.objectFields.add(objectField)
            }
            return objectValue
        }
        throw ShouldNotHappenException()
    }

    private fun parseString(string: String): String {
        val writer = StringWriter(string.length - 2)
        val end = string.length - 1
        var i = 1
        while (i < end) {
            val c = string[i]
            if (c != '\\') {
                writer.write(c.toInt())
                i++
                continue
            }
            val escaped = string[i + 1]
            i += 1
            when (escaped) {
                '"'  -> {
                    writer.append('"')
                }
                '/'  -> {
                    writer.append('/')
                }
                '\\' -> {
                    writer.append('\\')
                }
                'b'  -> {
                    writer.append('\b')
                }
                'f'  -> {
                    writer.write(12)
//                    writer.append('\f')
                }
                'n'  -> {
                    writer.append('\n')
                }
                'r'  -> {
                    writer.append('\r')
                }
                't'  -> {
                    writer.append('\t')
                }
                'u'  -> {
                    val codepoint = Integer.parseInt(string.substring(i + 1, i + 5), 16)
                    i += 4
                    writer.write(codepoint)
                }
                else -> throw ShouldNotHappenException()
            }
            i++
        }
        return writer.toString()
    }

    private fun newNode(abstractNode: AbstractNode, parserRuleContext: ParserRuleContext) {
        abstractNode.sourceLocation = sourceLocation(parserRuleContext)
    }

    private fun sourceLocation(parserRuleContext: ParserRuleContext): SourceLocation {
        return SourceLocation(parserRuleContext.getStart().line, parserRuleContext.getStart().charPositionInLine + 1)
    }

}

