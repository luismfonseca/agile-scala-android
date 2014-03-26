package PACKAGE_DB.tables

import scala.concurrent._
import scala.language.implicitConversions
import ExecutionContext.Implicits.global

import slick.driver.SQLiteDriver.SchemaDescription
import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.jdbc.meta.MTable
import scala.slick.lifted.AbstractTable

IMPORTS_TABLE
import PACKAGE_MODELS._

class TableMODEL_NAME_PLURAL(tag: Tag) extends Table[MODEL_NAME_AS_IS](tag, "MODEL_NAME_AS_IS") {

INJECT_IMPLICITS_IF_NEEDED
DEFS_OF_FIELDS

DEF_OF_ID

  def * = (TABLE_FIELDS_TUPLE) <> (MODEL_NAME_AS_IS.tupled, MODEL_NAME_AS_IS.unapply)
}
