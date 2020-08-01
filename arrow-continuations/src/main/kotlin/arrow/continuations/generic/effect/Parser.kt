package arrow.continuations.generic.effect

import arrow.continuations.generic.DelimitedScope
import arrow.continuations.generic.MultiShotDelimContScope
import arrow.core.Either
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.Tuple2
import arrow.core.identity
import arrow.core.left
import arrow.core.toT

typealias Parser<A> = suspend ParserCtx.() -> A

interface ParserCtx : NonDet, Error<ParseError> {
  suspend fun take1(): Char
  suspend fun take(n: Int): String
  suspend fun takeWhile(matcher: (Char) -> Boolean): String
  suspend fun takeAtLeastOneWhile(matcher: (Char) -> Boolean): String

  fun getOffset(): Int

  suspend fun <B> token(expected: Set<ErrorItem> = emptySet(), matcher: (Char) -> B?): B
  suspend fun string(str: String): String
  suspend fun eof(): Unit
  suspend fun <A> lookAhead(p: Parser<A>): A
  suspend fun <A> notFollowedBy(p: Parser<A>): Unit
  suspend fun <A> optional(p: Parser<A>): A?

  suspend fun satisfy(expected: Set<ErrorItem> = emptySet(), f: (Char) -> Boolean): Char = token(expected) { it.takeIf(f) }
  suspend fun char(c: Char): Char = satisfy(setOf(ErrorItem.fromChar(c))) { it == c }

  fun <A> attempt(p: Parser<A>): Either<ParseError, A>
}

data class ParseError(
  val offset: Int,
  val unexpectedToken: ErrorItem? = null,
  val expectedTokens: Set<ErrorItem> = emptySet()
) {
  operator fun plus(other: ParseError): ParseError =
    when {
      offset < other.offset -> other
      offset > other.offset -> this
      else -> ParseError(
        offset,
        unexpectedToken ?: other.unexpectedToken,
        expectedTokens.union(other.expectedTokens)
      )
    }

  // This could be a lot better, looking at megaparsec here!
  override fun toString(): String =
    "ParseError at offset $offset. Got \"$unexpectedToken\" but expected: \"${showExpected()}\""

  private fun showExpected(): String =
    expectedTokens.fold("") { acc, errorItem -> "$acc,$errorItem" }.drop(1)
}

sealed class ErrorItem {
  companion object {
    fun unsafeFromString(str: String): ErrorItem =
      Tokens(Nel.fromListUnsafe(str.toList()))

    fun fromChar(c: Char): ErrorItem =
      Tokens(Nel.of(c))
  }

  override fun toString(): String = when (this) {
    is Tokens -> ts.all.joinToString("")
    is EndOfInput -> "End of input"
  }
}

class Tokens(val ts: NonEmptyList<Char>) : ErrorItem()
object EndOfInput : ErrorItem()

data class ParseResult<out A>(val remaining: String, val result: Either<ParseError, A>)

//impl
class ParserCtxImpl<A>(val input: String, val scope: DelimitedScope<ParseResult<A>>) : ParserCtx, DelimitedScope<ParseResult<A>> by scope {
  private var offset = 0
  override fun getOffset(): Int = offset

  private fun take1_(): Char? = input.takeIf { it.length > offset }?.get(offset)
  private fun take_(n: Int): String? = input.takeIf { it.length >= offset + n }?.substring(offset, offset + n)
  fun takeRemaining(): String = input.takeIf { it.length > offset }?.substring(offset) ?: ""

  override suspend fun take1(): Char = take1_()?.also { offset++ } ?: raise(ParseError(offset, EndOfInput))
  override suspend fun take(n: Int): String = take_(n)?.also { offset += n } ?: raise(ParseError(offset, EndOfInput))
  override suspend fun takeWhile(matcher: (Char) -> Boolean): String =
    input.takeWhile(matcher).also { offset += it.length }

  override suspend fun takeAtLeastOneWhile(matcher: (Char) -> Boolean): String =
    input.takeWhile(matcher).takeIf { it.isNotEmpty() }
      ?: raise(
        ParseError(
          offset,
          input
            .takeIf { it.length > offset }
            ?.get(offset)?.let { ErrorItem.fromChar(it) } ?: EndOfInput
        )
      )

  override suspend fun string(str: String): String =
    (take_(str.length) ?: raise(ParseError(offset, EndOfInput, setOf(ErrorItem.unsafeFromString(str)))))
      .let {
        it.takeIf { it == str }
          ?.also { offset += str.length }
          ?: raise(ParseError(offset, ErrorItem.unsafeFromString(it), setOf(ErrorItem.unsafeFromString(str))))
      }

  override suspend fun <B> token(expected: Set<ErrorItem>, matcher: (Char) -> B?): B =
    (take1_() ?: raise(ParseError(offset, EndOfInput, expected)))
      .let { it.let(matcher)?.also { offset++ } ?: raise(ParseError(offset, ErrorItem.fromChar(it), expected)) }

  override suspend fun eof() = take1_().let { c ->
    if (c != null) raise(ParseError(offset, ErrorItem.fromChar(c), setOf(EndOfInput)))
  }

  override suspend fun <A> lookAhead(p: Parser<A>): A =
    p.runParser(takeRemaining()).result.fold(
      { e -> raise(e) },
      ::identity
    )

  override suspend fun <A> notFollowedBy(p: Parser<A>) =
    p.runParser(takeRemaining()).result.fold(
      {},
      {
        raise(
          ParseError(offset, input
            .takeIf { it.length > offset }
            ?.get(offset)
            ?.let { c -> ErrorItem.fromChar(c) }
            ?: EndOfInput)
        )
      }
    )

  override suspend fun <A> optional(p: Parser<A>): A? =
    p.runParser(takeRemaining()).result.fold({ null }, ::identity)

  override suspend fun raise(e: ParseError): Nothing =
    shift { ParseResult(takeRemaining(), e.left()) }

  /*
  This won't work without nesting support because f may call into parent scopes.
  Ideally we'd  have f: ParserCtx.() -> A here to avoid this by giving an explicit context
  override suspend fun <A> catch(f: suspend () -> A, hdl: suspend (ParseError) -> A): A {
    val p: Parser<Tuple2<A, Int>> = { f() toT getOffset() }
    return p.runParser(takeRemaining()).result.fold({ e ->
      hdl(e)
    }, { (result, off) ->
      offset = off
      result
    })
  }
   */
  override suspend fun <A> catch(f: suspend () -> A, hdl: suspend (ParseError) -> A): A = TODO("Not working. Use attempt for now. See note in code on commented part above")

  override fun <A> attempt(p: Parser<A>): Either<ParseError, A> =
    p.runParser(takeRemaining()).result

  override suspend fun choose(): Boolean {
    return shift { k ->
      val res = k(true)
      when (val lResult = res.result) {
        is Either.Left -> {
          val res2 = k(false)
          val nRem = if (res.remaining.length > res2.remaining.length) res2.remaining else res.remaining
          when (val rResult = res2.result) {
            is Either.Left -> ParseResult(nRem, Either.Left(lResult.a + rResult.a))
            is Either.Right -> res2
          }
        }
        is Either.Right -> res
      }
    }
  }

  override suspend fun empty(): Nothing = raise(ParseError(offset, null, emptySet()))
}

fun <A> Parser<A>.runParser(str: String): ParseResult<A> =
  MultiShotDelimContScope.reset {
    val ctx = ParserCtxImpl(str, this)
    val res = this@runParser.invoke(ctx)
    ParseResult(ctx.takeRemaining(), Either.right(res))
  }
