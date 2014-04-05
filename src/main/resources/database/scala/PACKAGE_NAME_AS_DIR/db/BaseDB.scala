package PACKAGE_DB

import scala.slick.driver.SQLiteDriver.simple.{Database, TableQuery}
import scala.slick.jdbc.meta.MTable

import PACKAGE_DB.dao._

trait BaseDB extends Migrations
{
  val applicationFilesDirectory: String
  val databaseName: String

  val DB: Database

  // Table representation instances
TABLE_REPRESENTATION_INSTANCES
}
