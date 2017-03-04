package graphql.parser


import graphql.ShouldNotHappenException
import graphql.language.*
import graphql.parser.antlr.GraphqlBaseVisitor
import graphql.parser.antlr.GraphqlParser
import org.antlr.v4.runtime.ParserRuleContext

import java.io.StringWriter
import java.math.BigDecimal
import java.math.BigInteger
import java.util.ArrayDeque


class GraphqlAntlrToLanguage : GraphqlBaseVisitor<Void>() {

    internal var result: Document = Document()

    enum class ContextProperty {
        OperationDefinition,
        FragmentDefinition,
        Field,
        InlineFragment,
        FragmentSpread,
        SelectionSet,
        VariableDefinition,
        ListType,
        NonNullType,
        Directive
    }

    internal class ContextEntry(var contextProperty: ContextProperty, var value: Any)

    private val contextStack = ArrayDeque<ContextEntry>()


    private fun addContextProperty(contextProperty: ContextProperty, value: Any) {

        when (contextProperty) {
            GraphqlAntlrToLanguage.ContextProperty.SelectionSet -> newSelectionSet(value as SelectionSet)
            GraphqlAntlrToLanguage.ContextProperty.Field        -> newField(value as Field)
        }
        contextStack.addFirst(ContextEntry(contextProperty, value))
    }

    private fun popContext() {
        contextStack.removeFirst()
    }

    private fun getFromContextStack(contextProperty: ContextProperty): Any? {
        return contextStack.filter { it.contextProperty == contextProperty }
                .first()?.value
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
        (getFromContextStack(ContextProperty.SelectionSet) as SelectionSet).selections().add(field)
    }


    override fun visitDocument(ctx: graphql.parser.antlr.GraphqlParser.DocumentContext): Void {
        newNode(result, ctx)
        return super.visitDocument(ctx)
    }

    override fun visitOperationDefinition(ctx: GraphqlParser.OperationDefinitionContext): Void? {
        val operationDefinition = OperationDefinition()
        newNode(operationDefinition, ctx)
        newNode(operationDefinition, ctx)
        if (ctx.operationType() == null) {
            operationDefinition.operation = OperationDefinition.Operation.QUERY
        } else {
            operationDefinition.operation = parseOperation(ctx.operationType())
        }
        if (ctx.NAME() != null) {
            operationDefinition.name = ctx.NAME().text
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
            throw RuntimeException()
        }
    }

    override fun visitFragmentSpread(ctx: GraphqlParser.FragmentSpreadContext): Void? {
        val fragmentSpread = FragmentSpread(ctx.fragmentName().text)
        newNode(fragmentSpread, ctx)
        (getFromContextStack(ContextProperty.SelectionSet) as SelectionSet).selections().add(fragmentSpread)
        addContextProperty(ContextProperty.FragmentSpread, fragmentSpread)
        super.visitFragmentSpread(ctx)
        popContext()
        return null
    }

    override fun visitVariableDefinition(ctx: GraphqlParser.VariableDefinitionContext): Void? {
        val variableDefinition = VariableDefinition()
        newNode(variableDefinition, ctx)
        variableDefinition.name = ctx.variable().NAME().text
        if (ctx.defaultValue() != null) {
            val value = getValue(ctx.defaultValue().value())
            variableDefinition.defaultValue = value
        }
        val operationDefinition = getFromContextStack(ContextProperty.OperationDefinition) as OperationDefinition?
        operationDefinition!!.add(variableDefinition)

        addContextProperty(ContextProperty.VariableDefinition, variableDefinition)
        super.visitVariableDefinition(ctx)
        popContext()
        return null
    }

    override fun visitFragmentDefinition(ctx: GraphqlParser.FragmentDefinitionContext): Void? {
        val fragmentDefinition = FragmentDefinition()
        newNode(fragmentDefinition, ctx)
        fragmentDefinition.name = ctx.fragmentName().text
        fragmentDefinition.typeCondition = TypeName(ctx.typeCondition().text)
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
        val newField = Field()
        newNode(newField, ctx)
        newField.name = ctx.NAME().text
        if (ctx.alias() != null) {
            newField.alias = ctx.alias().NAME().text
        }
        addContextProperty(ContextProperty.Field, newField)
        super.visitField(ctx)
        popContext()
        return null
    }

    override fun visitTypeName(ctx: GraphqlParser.TypeNameContext): Void {
        val typeName = TypeName(ctx.NAME().text)
        newNode(typeName, ctx)
        for (contextEntry in contextStack) {
            if (contextEntry.value is ListType) {
                (contextEntry.value as ListType).type = typeName
                break
            }
            if (contextEntry.value is NonNullType) {
                (contextEntry.value as NonNullType).type(typeName)
                break
            }
            if (contextEntry.value is VariableDefinition) {
                (contextEntry.value as VariableDefinition).type = typeName
                break
            }
        }
        return super.visitTypeName(ctx)
    }

    override fun visitNonNullType(ctx: GraphqlParser.NonNullTypeContext): Void? {
        val nonNullType = NonNullType()
        newNode(nonNullType, ctx)
        for (contextEntry in contextStack) {
            if (contextEntry.value is ListType) {
                (contextEntry.value as ListType).type = nonNullType
                break
            }
            if (contextEntry.value is VariableDefinition) {
                (contextEntry.value as VariableDefinition).type = nonNullType
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
            if (contextEntry.value is ListType) {
                (contextEntry.value as ListType).type = listType
                break
            }
            if (contextEntry.value is NonNullType) {
                (contextEntry.value as NonNullType).type(listType)
                break
            }
            if (contextEntry.value is VariableDefinition) {
                (contextEntry.value as VariableDefinition).type = listType
                break
            }
        }
        addContextProperty(ContextProperty.ListType, listType)
        super.visitListType(ctx)
        popContext()
        return null
    }

    override fun visitArgument(ctx: GraphqlParser.ArgumentContext): Void {
        val argument = Argument(ctx.NAME().text, getValue(ctx.valueWithVariable()))
        newNode(argument, ctx)
        if (getFromContextStack(ContextProperty.Directive) != null) {
            (getFromContextStack(ContextProperty.Directive) as Directive).add(argument)
        } else {
            val field = getFromContextStack(ContextProperty.Field) as Field?
            field!!.add(argument)
        }
        return super.visitArgument(ctx)
    }

    override fun visitInlineFragment(ctx: GraphqlParser.InlineFragmentContext): Void? {
        val inlineFragment = InlineFragment(TypeName(ctx.typeCondition().text))
        newNode(inlineFragment, ctx)
        (getFromContextStack(ContextProperty.SelectionSet) as SelectionSet).selections().add(inlineFragment)
        addContextProperty(ContextProperty.InlineFragment, inlineFragment)
        super.visitInlineFragment(ctx)
        popContext()
        return null
    }

    override fun visitDirective(ctx: GraphqlParser.DirectiveContext): Void? {
        val directive = Directive(ctx.NAME().text)
        newNode(directive, ctx)
        for (contextEntry in contextStack) {
            if (contextEntry.contextProperty == ContextProperty.Field) {
                (contextEntry.value as Field).add(directive)
                break
            } else if (contextEntry.contextProperty == ContextProperty.FragmentDefinition) {
                (contextEntry.value as FragmentDefinition).add(directive)
                break
            } else if (contextEntry.contextProperty == ContextProperty.FragmentSpread) {
                (contextEntry.value as FragmentSpread).add(directive)
                break
            } else if (contextEntry.contextProperty == ContextProperty.InlineFragment) {
                (contextEntry.value as InlineFragment).add(directive)
                break
            } else if (contextEntry.contextProperty == ContextProperty.OperationDefinition) {
                (contextEntry.value as OperationDefinition).add(directive)
                break
            }
        }
        addContextProperty(ContextProperty.Directive, directive)
        super.visitDirective(ctx)
        popContext()
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
                arrayValue.add(getValue(valueWithVariableContext))
            }
            return arrayValue
        } else if (ctx.objectValueWithVariable() != null) {
            val objectValue = ObjectValue()
            newNode(objectValue, ctx)
            for (objectFieldWithVariableContext in ctx.objectValueWithVariable().objectFieldWithVariable()) {
                val objectField = ObjectField(objectFieldWithVariableContext.NAME().text, getValue(objectFieldWithVariableContext.valueWithVariable()))
                objectValue.add(objectField)
            }
            return objectValue
        } else if (ctx.variable() != null) {
            val variableReference = VariableReference(ctx.variable().NAME().text)
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
                arrayValue.add(getValue(valueWithVariableContext))
            }
            return arrayValue
        } else if (ctx.objectValue() != null) {
            val objectValue = ObjectValue()
            newNode(objectValue, ctx)
            for (objectFieldContext in ctx.objectValue().objectField()) {
                val objectField = ObjectField(objectFieldContext.NAME().text, getValue(objectFieldContext.value()))
                objectValue.add(objectField)
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
        abstractNode.sourceLocation = getSourceLocation(parserRuleContext)
    }

    private fun getSourceLocation(parserRuleContext: ParserRuleContext): SourceLocation {
        return SourceLocation(parserRuleContext.getStart().line, parserRuleContext.getStart().charPositionInLine + 1)
    }

}
