package graphql.execution.instrumentation

/**
 * When a [Instrumentation].'beginXXX' method is called then it must return a non null InstrumentationContext
 * that will the be invoked as [.onEnd] or [.onEnd] when the step completes.

 * This pattern of construction of an object then call back is intended to allow "timers" to be created that can instrument what has
 * just happened or "loggers" to be called to record what has happened.
 */
interface InstrumentationContext<in T> {

    /**
     * This is invoked when the execution step is completed successfully
     * @param result the successful result of the step
     */
    fun onEnd(result: T?)

    /**
     * This is invoked when the execution step is completed unsuccessfully
     * @param e the exception that happened during the step
     */
    fun onEnd(e: Exception)
}

fun <T> InstrumentationContext<T>.onEnd(result: T?, ex: Throwable?) {
    if (ex != null)
        this.onEnd(ex as Exception)
    else
        this.onEnd(result)
}