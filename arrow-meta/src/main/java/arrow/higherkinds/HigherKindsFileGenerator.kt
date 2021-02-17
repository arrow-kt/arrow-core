package arrow.higherkinds

import arrow.common.Package
import arrow.common.utils.knownError
import arrow.common.utils.typeConstraints
import arrow.common.utils.writeSafe
import org.jetbrains.kotlin.metadata.ProtoBuf
import javax.annotation.processing.Filer
import javax.lang.model.element.Name

const val KindPostFix = "Of"
const val KindPartialPostFix = "PartialOf"
const val KindedJPostFix = "KindedJ"
const val HKMarkerPreFix = "For"

data class HigherKind(
  val `package`: Package,
  val target: AnnotatedHigherKind
) {
  val tparams: List<ProtoBuf.TypeParameter> = target.classOrPackageProto.typeParameters
  val kindName: Name = target.classElement.simpleName
  val alias: String = if (tparams.size == 1) "arrow.Kind" else "arrow.Kind${tparams.size}"
  private val hkjsPackage = "arrow"
  val aliasJ: String = if (tparams.size == 1) "io.kindedj.Hk" else "$hkjsPackage.HkJ${tparams.size}"
  val typeArgs: List<String> = target.classOrPackageProto.typeParameters.map { target.classOrPackageProto.nameResolver.getString(it.name) }
  val expandedTypeArgs: String = target.classOrPackageProto.typeParameters.joinToString(
    separator = ", ",
    transform = { target.classOrPackageProto.nameResolver.getString(it.name) }
  )
  val typeConstraints = target.classOrPackageProto.typeConstraints()
  val name: String = "$kindName$KindPostFix"
  val nameJ: String = "$kindName$KindedJPostFix"
  val markerName = "$HKMarkerPreFix$kindName"
}

class HigherKindsFileGenerator(
  private val filer: Filer,
  annotatedList: List<AnnotatedHigherKind>
) {

  private val higherKinds: List<HigherKind> = annotatedList.map { HigherKind(it.classOrPackageProto.`package`, it) }

  /**
   * Main entry point for higher kinds instance generation.
   */
  fun generate(logger: (message: CharSequence) -> Unit) {
    higherKinds.forEachIndexed { _, hk ->
      val elementsToGenerate = listOf(genKindMarker(hk), genKindTypeAliases(hk), genKindedJTypeAliases(hk), genEv(hk))
      val source: String = elementsToGenerate.joinToString(
        prefix = "${if (hk.`package` != "unnamed package") "package ${hk.`package`}" else ""}\n\n",
        separator = "\n",
        postfix = "\n"
      )
      filer.writeSafe(
        if (hk.`package` != "unnamed package") hk.`package` else "",
        hk.target.classElement.qualifiedName,
        source,
        logger,
        hk.target.classElement
      )
    }
  }

  private fun genKindTypeAliases(hk: HigherKind): String = when {
    hk.tparams.isEmpty() -> knownError("Class must have at least one type param to derive HigherKinds")
    hk.tparams.size <= 22 -> {
      val kindAlias = "typealias ${hk.name}<${hk.expandedTypeArgs}> = ${hk.alias}<${hk.markerName}, ${hk.expandedTypeArgs}>"
      val acc = if (hk.tparams.size == 1) kindAlias else kindAlias + "\n" + genPartiallyAppliedKinds(hk)
      acc
    }
    else -> knownError("HigherKinds are currently only supported up to a max of 22 type args")
  }

  private fun genPartiallyAppliedKinds(hk: HigherKind): String {
    val appliedTypeArgs = hk.typeArgs.dropLast(1)
    val expandedAppliedTypeArgs = appliedTypeArgs.joinToString(", ")
    val hkimpl = if (appliedTypeArgs.size == 1) "arrow.Kind" else "arrow.Kind${appliedTypeArgs.size}"
    return "typealias ${hk.name.replace("Of$".toRegex()) { "PartialOf" }}<$expandedAppliedTypeArgs> = $hkimpl<${hk.markerName}, $expandedAppliedTypeArgs>"
  }

  private fun genKindedJTypeAliases(hk: HigherKind): String =
    if (hk.tparams.size <= 5 && allInvariantParams(hk.tparams)) {
      "typealias ${hk.nameJ}<${hk.expandedTypeArgs}> = ${hk.aliasJ}<${hk.markerName}, ${hk.expandedTypeArgs}>"
    } else {
      ""
    }

  private fun allInvariantParams(tparams: List<ProtoBuf.TypeParameter>): Boolean =
    tparams.all { it.variance == ProtoBuf.TypeParameter.Variance.INV }

  private fun genEv(hk: HigherKind): String =
    """
            |@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
            |inline fun <${hk.expandedTypeArgs}> ${hk.name}<${hk.expandedTypeArgs}>.fix(): ${hk.kindName}<${hk.expandedTypeArgs}>${hk.typeConstraints} =
            |  this as ${hk.kindName}<${hk.expandedTypeArgs}>
        """.trimMargin()

  private fun genKindMarker(hk: HigherKind): String =
    "class ${hk.markerName} private constructor() { companion object }"
}
