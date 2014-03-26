package PACKAGE_DB

import scala.slick.driver.SQLiteDriver.simple.Database

import android.content.Context

class ApplicationDB(private val applicationFilesDir: String, val name: String = "orm") extends BaseDB {

  def this(context: Context) = this(context.getFilesDir() + "/")

  override val db: Database =
    this.migrateToLatest(this,
      Database.forURL("jdbc:sqlite:" + applicationFilesDir + name + ".db", driver = "org.sqldroid.SQLDroidDriver")
    )
}
