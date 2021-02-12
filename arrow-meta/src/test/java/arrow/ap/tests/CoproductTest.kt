package arrow.ap.tests

import arrow.generic.CoproductProcessor

class CoproductTest : APTest("arrow.ap.objects", enforcePackage = false) {

  init {
    testProcessor(
      AnnotationProcessor(
        name = "Coproducts are generated by coproduct annotation",
        sourceFiles = listOf("coproduct/Coproduct.java"),
        destFile = "coproduct2/Coproduct2.kt",
        processor = CoproductProcessor()
      ),
      actualFileLocation = {
        "${it.path}/arrow/generic/coproduct2"
      }
    )
  }
}
