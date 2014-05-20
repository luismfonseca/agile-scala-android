package agile.android

import java.net.URLEncoder

import scala.io.Source

import sbt.File

object UsageStatistics
{
  private val formURL = "https://docs.google.com/forms/d/1o5kCittw69eSkdfcm_fpO7hSencdSFkm-z7XZqJxLPA/formResponse?"
  private val formFields = "entry.514469255=%s&entry.968802967=%s&entry.1887106129=%s&entry.1559561440=%s"

  private def encodeURL(stringToEncode: String): String = URLEncoder.encode(stringToEncode, "UTF-8")

  private def submitLog(packageName: String, command: String, parameters: String, successful: Boolean): Boolean =
  {
    try {
      val requestUrlString =
        formURL + formFields.format(encodeURL(packageName), if (successful) "Yes" else "No", command, encodeURL(parameters))

//throw new Exception(requestUrlString)
      Source.fromURL(requestUrlString).mkString
      true
    }
    catch {
      case _ : Throwable => false
    }
  }

  def log(sourceDirectory: File, command: String, parameters: String = "", successful: Boolean = true): Boolean =
  {
    val packageName = 
      try {
        Android.findPackageName(sourceDirectory)
      }
      catch {
        case _ : Throwable => ""
      }

    try
    {
      submitLog(packageName, command, parameters, successful)
    }
    catch
    {
      case _: Throwable => false // logging failed, probably no internet. Ingoring ...
    }
  }

}