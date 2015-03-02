package de.frosner.dds.core

import java.io._

case class Helper[T](classWithHelp: Class[T]) {

  type Name = String
  type ShortDescription = String
  type LongDescription = String

  val methods = {
    val methodsThatOfferHelp = classWithHelp.getMethods.filter(method => method.getAnnotations.exists(
      annotation => annotation.isInstanceOf[Help]
    ))
    val methodsAndHelp = methodsThatOfferHelp.map(method => {
      val helpAnnotation = method.getAnnotations.find(annotation =>
        annotation.isInstanceOf[Help]
      ).get.asInstanceOf[Help]
      (method.getName, helpAnnotation)
    })
    methodsAndHelp.groupBy {
      case (name, help) => help.category()
    }.toList.sortBy {
      case (category, methods) => category.toLowerCase
    }
  }

  def printMethods(out: PrintStream) = methods.foreach {
    case (category, methods) => {
      out.println(s"\033[1m${category}\033[0m")
      methods.sortBy{ case (name, help) => name }.foreach {
        case (name, help) =>
          out.println(s"- $name(${help.parameters})" +
            (if (help.parameters2() != "") { "(" + help.parameters2() + ")" } else "") +
            (if (help.parameters3() != "") { "(" + help.parameters3() + ")" } else "") +
            (if (help.parameters4() != "") { "(" + help.parameters4() + ")" } else "") +
            (if (help.parameters5() != "") { "(" + help.parameters5() + ")" } else "") +
            (if (help.parameters6() != "") { "(" + help.parameters6() + ")" } else "") +
            (if (help.parameters7() != "") { "(" + help.parameters7() + ")" } else "") +
            (if (help.parameters8() != "") { "(" + help.parameters8() + ")" } else "") +
            (if (help.parameters9() != "") { "(" + help.parameters9() + ")" } else "") +
            s": ${help.shortDescription}")
      }
      out.println()
    }
  }

}
