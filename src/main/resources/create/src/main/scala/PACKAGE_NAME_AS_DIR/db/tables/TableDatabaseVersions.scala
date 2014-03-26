package PACKAGE_DB.tables

import scala.concurrent._
import scala.language.implicitConversions
import ExecutionContext.Implicits.global

import slick.driver.SQLiteDriver.SchemaDescription
import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.jdbc.meta.MTable
import scala.slick.lifted.AbstractTable

import PACKAGE_MODELS._

class TableDatabaseVersions(tag: Tag) extends Table[DatabaseVersion](tag, "DatabaseVersion") {

  def version = column[Long]("VERSION")

  def appliedIn = column[Long]("APPLIED_IN")

  def * = (version, appliedIn) <> (DatabaseVersion.tupled, DatabaseVersion.unapply)
}
