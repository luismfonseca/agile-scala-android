import java.io.File

object Create {

  val sbtBuildPropertiesFile = new File("project\\build.properties")
  val sbtBuildFile = new File("build.sbt")
  val androidManifestFile = new File("src\\main\\AndroidManifest.xml")
  val valuesStringFile = new File("src\\main\\res\\values\\string.xml")
  val layoutMainFile = new File("src\\main\\res\\layout\\main.xml")
  val gitignoreFile = new File(".gitignore")

  
  def directoriesWith(packageName: String, minApiLevel: Int) = {
    
      val commonDirectories = Seq[String](
        "src\\main\\res\\values",
        "src\\main\\res\\layout",
        "src\\main\\res\\menu",
        "src\\test\\scala",
        "src\\main\\scala\\" + packageName.replace('.', '\\'),
        "src\\main\\scala\\" + packageName.replace('.', '\\') + "\\models",
        "src\\main\\scala\\" + packageName.replace('.', '\\') + "\\activities"
      )

      val allDirectories =
        if (minApiLevel < 14)
          commonDirectories
        else
          commonDirectories ++ Seq[String](
            "src\\main\\res\\drawable-hdpi",
            "src\\main\\res\\drawable-ldpi",
            "src\\main\\res\\drawable-mdpi",
            "src\\main\\res\\drawable-xhdpi"
          )

      allDirectories map(new File(_))
  }

  def sbtBuildPropertiesContent(sbtVersion: String) =
    "sbt.version=%s\n" format sbtVersion

  def sbtBuildContent =
    """|import AgileAndroidKeys._
       |
       |defaultAgileAndroidSettings""".stripMargin


  def manifestXML(packageName: String, minApiLevel: Int) =
    """<?xml version="1.0" encoding="utf-8"?>
       |<manifest xmlns:android="http://schemas.android.com/apk/res/android"
       |      package="%s"
       |      android:versionCode="1"
       |      android:versionName="1.0">
	   |    <uses-sdk android:minSdkVersion="%d" />
       |    <application android:label="@string/app_name" android:icon="@drawable/ic_launcher">
       |        <activity android:name="MainActivity"
       |                  android:label="@string/app_name">
       |            <intent-filter>
       |                <action android:name="android.intent.action.MAIN" />
       |                <category android:name="android.intent.category.LAUNCHER" />
       |            </intent-filter>
       |        </activity>
       |    </application>
       |</manifest>
       |""".stripMargin.format(packageName, minApiLevel)

  def valuesStringXML =
    """<?xml version="1.0" encoding="utf-8"?>
       |<resources>"
       |    <string name="app_name">MainActivity</string>
       |""".stripMargin


  def layoutMainXML =
    """<?xml version="1.0" encoding="utf-8"?>
       |<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
       |    android:orientation="vertical"
       |    android:layout_width="fill_parent"
       |    android:layout_height="fill_parent"
	   |    >
       |<TextView
       |    android:layout_width="fill_parent"
       |    android:layout_height="wrap_content"
       |    android:text="Hello World, from MainActivity :)"
       |    />
       |</LinearLayout>
       |""".stripMargin


  def defaultGitIgnore =
    """|# built application files
       |*.apk
       |*.ap_
       |*.dex
	   |*.class
       |*.o
       |*.so
       |
       |# generated files
       |target/
       |
	   |# Mac OS X clutter
	   |*.DS_Store
	   |""".stripMargin
}