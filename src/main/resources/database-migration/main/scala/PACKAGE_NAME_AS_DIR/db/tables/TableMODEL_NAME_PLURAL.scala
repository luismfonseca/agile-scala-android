package PACKAGE_DB.tables

import scala.concurrent._
import scala.language.implicitConversions
import ExecutionContext.Implicits.global

import slick.driver.SQLiteDriver.SchemaDescription
import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.jdbc.meta.MTable
import scala.slick.lifted.AbstractTable
IMPORT_TABLE_FIELDS_DEPENDENCIES
import PACKAGE_NAME.App
import PACKAGE_MODELS._

case class MODEL_NAME_AS_ISRow(TABLE_ROW_FIELDS)
{
TABLE_ROW_HELPERS}

class TableTABLE_NAME(tag: Tag) extends Table[MODEL_NAME_AS_ISRow](tag, "MODEL_NAME_AS_IS")
{
INJECT_IMPLICITS_IF_NEEDED
DEFS_OF_FIELDS
DEF_OF_ID
  def * = (TABLE_FIELDS_TUPLE) <> (MODEL_NAME_AS_ISRow.tupled, MODEL_NAME_AS_ISRow.unapply)
}
