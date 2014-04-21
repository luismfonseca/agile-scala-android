package PACKAGE_DB.tables

import scala.concurrent._
import scala.language.implicitConversions
import ExecutionContext.Implicits.global

import slick.driver.SQLiteDriver.SchemaDescription
import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.jdbc.meta.MTable
import scala.slick.lifted.AbstractTable

import PACKAGE_NAME.App
import PACKAGE_MODELS._

case class DatabaseVersionRow(databaseVersionId: Int, version: Long, appliedIn: Long)
{
}

class TableDatabaseVersions(tag: Tag) extends Table[DatabaseVersionRow](tag, "DatabaseVersion")
{
  def version = column[Long]("VERSION")

  def appliedIn = column[Long]("APPLIED_IN")

  def databaseVersionId = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def * = (databaseVersionId, version, appliedIn) <> (DatabaseVersionRow.tupled, DatabaseVersionRow.unapply)
}
