package pt.testing.whaa.db.dao

import scala.slick.driver.SQLiteDriver.simple._

import pt.testing.whaa.db.tables.TableDatabaseVersions

class DatabaseVersions(tag: Tag) extends TableDatabaseVersions(tag) {

  // Extra DAO logic here.
}
