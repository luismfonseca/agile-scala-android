package PACKAGE_DB.migrations

import PACKAGE_DB._
import PACKAGE_DB.tables._
import PACKAGE_NAME._

import java.util.Date

import scala.slick.driver.MySQLDriver.simple._

class Seed extends Migration {

  override def version = "00:00:00 01/01/2001"

  override def up =
    {
      App.DB withSession {
        implicit session =>
      }
    }

  override def down = ???
}