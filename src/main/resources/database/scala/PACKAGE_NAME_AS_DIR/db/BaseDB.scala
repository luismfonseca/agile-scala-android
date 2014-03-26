package PACKAGE_DB

import scala.slick.driver.SQLiteDriver.simple.{Database, TableQuery}
import scala.slick.jdbc.meta.MTable

import PACKAGE_DB.dao._

abstract class BaseDB extends Migrations {

  val db: Database

  // Table representation instances
TABLE_REPRESENTATION_INSTANCES
}
