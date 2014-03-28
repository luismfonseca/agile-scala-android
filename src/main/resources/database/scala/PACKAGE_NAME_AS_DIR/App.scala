package PACKAGE_NAME

import scala.slick.driver.SQLiteDriver.simple.Database

import android.content.Context
import android.app.Application

import java.io.File

import PACKAGE_DB.BaseDB

object App extends App
{
  private var filesDir: File = _
}

class App extends Application with BaseDB
{
  override val databaseName = "orm.db"
  override lazy val applicationFilesDirectory = App.filesDir + "/"

  override lazy val DB: Database =
    this.migrateToLatest(this,
      Database.forURL("jdbc:sqlite:" + applicationFilesDirectory + databaseName, driver = "org.sqldroid.SQLDroidDriver")
    )
    
  override def onCreate(): Unit =
  {
    super.onCreate()
    App.filesDir = getApplicationContext().getFilesDir()
  }
}
