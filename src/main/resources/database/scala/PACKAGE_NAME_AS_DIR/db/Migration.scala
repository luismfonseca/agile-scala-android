package PACKAGE_DB

import scala.language.implicitConversions
import java.text.SimpleDateFormat

trait Migration {

  implicit def stringToDateLong(stringValue: String): Long =
    new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse(stringValue).getTime()

  def version: Long = ???

  def up()

  def down()
}
