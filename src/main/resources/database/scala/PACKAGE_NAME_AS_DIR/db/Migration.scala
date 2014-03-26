package PACKAGE_DB

import java.text.SimpleDateFormat

trait Migration {

  implicit def stringToDateLong(stringValue: String): Long =
    new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse(stringValue).getTime()

  def version: Long = ???

  def up: Unit = ???

  def down: Unit = ???
}
