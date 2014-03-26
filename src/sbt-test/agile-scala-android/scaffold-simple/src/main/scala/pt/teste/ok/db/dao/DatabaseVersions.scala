package pt.teste.ok.db.dao

import scala.slick.driver.SQLiteDriver.simple._

import pt.teste.ok.db.tables.TableDatabaseVersions

class DatabaseVersions(tag: Tag) extends TableDatabaseVersions(tag) {

  // Extra DAO logic here.
}
