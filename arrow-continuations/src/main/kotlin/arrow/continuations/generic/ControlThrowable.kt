package arrow.continuations.generic

open class ControlThrowable : Throwable() {
  override fun fillInStackTrace(): Throwable =
    this
}
