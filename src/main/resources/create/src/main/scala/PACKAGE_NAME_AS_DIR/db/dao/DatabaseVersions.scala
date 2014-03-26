package PACKAGE_DB.dao

import scala.slick.driver.SQLiteDriver.simple._

import PACKAGE_DB.tables.TableDatabaseVersions

class DatabaseVersions(tag: Tag) extends TableDatabaseVersions(tag) {

  // Extra DAO logic here.
}
