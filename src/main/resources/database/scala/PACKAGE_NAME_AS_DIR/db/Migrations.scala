package PACKAGE_DB

import slick.driver.SQLiteDriver.SchemaDescription
import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.jdbc.meta.MTable

import PACKAGE_NAME.App
import PACKAGE_MODELS._
import PACKAGE_DB.tables.DatabaseVersionRow
IMPORT_MIGRATIONS_IF_ANY
trait Migrations {

  val allMigrations: Seq[Migration] =
    Seq[Migration](
MIGRATIONS_LIST
    ).sortBy(_.version)
  
  def migrateToLatest(applicationDB: App, db: Database): Database = {
    db withSession { implicit session =>

      // Check if database exists
      if (MTable.getTables("DatabaseVersion").list.isEmpty)
      {
        val newDatabaseSchema =
          NEW_DATABASE_SCHEMA

        newDatabaseSchema.create
        
        val latestVersion =
          if (allMigrations.isEmpty)
          {
		    0
		  }
          else
		  {
		    allMigrations.last.version
		  }
        
        applicationDB.databaseVersions += DatabaseVersionRow(-1, latestVersion, System.currentTimeMillis())
      }
      else
      {
        val lastPerformedMigration = applicationDB.databaseVersions.list.maxBy(_.version)
        
        // Apply needed migrations
        for (migration <- allMigrations
             if (migration.version > lastPerformedMigration.version))
        {
          migration.up
          applicationDB.databaseVersions += DatabaseVersionRow(-1, migration.version, System.currentTimeMillis())
        }
      }
    }
    db
  }
}