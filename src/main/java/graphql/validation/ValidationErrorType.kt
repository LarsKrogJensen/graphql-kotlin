package graphql.validation


enum class ValidationErrorType {
    DefaultForNonNullArgument,
    WrongType,
    UnknownType,
    SubSelectionRequired,
    SubSelectionNotAllowed,
    InvalidSyntax,
    BadValueForDefaultArg,
    FieldUndefined,
    InlineFragmentTypeConditionInvalid,
    FragmentTypeConditionInvalid,
    UnknownArgument,
    UndefinedFragment,
    NonInputTypeOnVariable,
    UnusedFragment,
    MissingFieldArgument,
    MissingDirectiveArgument,
    VariableTypeMismatch,
    UnknownDirective,
    MisplacedDirective,
    UndefinedVariable,
    UnusedVariable,
    FragmentCycle,
    FieldsConflict,
    InvalidFragmentType,
    LoneAnonymousOperationViolation

}
