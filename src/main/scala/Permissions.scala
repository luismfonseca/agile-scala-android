package agile.android

import java.io.{ByteArrayOutputStream, File, FileInputStream, PrintStream}
import java.net._
import java.nio.charset.Charset

import sbt.IO

object Permissions
{
  val mappedPermissions: Map[Int, Array[Permission]] =
  {
    
    Util.getResourceFiles("permissions/methods/").map({
      case (fileName, fileContent) =>
        {
          val version = fileName.split("\\.")(0).toInt

          val loadedPermissions = fileContent.split("\\r?\\n").foldLeft((Array[Permission](), ""))
          {
            (permissionsAndCurrentPermission, line) =>
            {
              val (permissions, currentPermission) = permissionsAndCurrentPermission
              line match
              {
                case newPermissionType if (newPermissionType.startsWith("Permission:")) => (permissions, newPermissionType.drop("Permission:".length))
                case callers if (callers.contains(" Callers:")) => (permissions, currentPermission)
                case newPermission => (permissions :+ new Permission(currentPermission, newPermission), currentPermission) 
              }
            }
          }
          (version, loadedPermissions._1)
        }
      })
  }

  val mappedIntentPermissions: Map[Int, Array[IntentPermission]] =
  {
    Util.getResourceFiles("permissions/intents/").map({
      case (fileName, fileContent) =>
      {
        val version = fileName.split("\\.")(0).toInt

        val loadedIntentPermissions = fileContent.split("\\r?\\n").map(
          line =>
          {
            val linePartitioned = line.split(" ")

            IntentPermission(linePartitioned(1), linePartitioned(0))
          }
        )
        
        (version, loadedIntentPermissions)
      }
    })
  }

  private val javaCallGraphJarBytes = Util.convertInputStreamToByteArray(getClass.getClassLoader().getResourceAsStream("libs/javacg-0.1-SNAPSHOT-static.jar"))

  def runJavaCallGraph(temporaryDirectory: File, jarPath: String): Array[String] =
  {
    val javacgJarFile = new File(temporaryDirectory.getPath() + "/javacg.jar")
    IO.write(javacgJarFile, javaCallGraphJarBytes)
    
    val loader = URLClassLoader.newInstance(
        Array[URL](javacgJarFile.toURL),
        getClass().getClassLoader()
    )
    val jcallGraphClass = Class.forName("gr.gousiosg.javacg.stat.JCallGraph", true, loader)

    val mainMethod = jcallGraphClass.getMethod("main", classOf[Array[String]])

    // output must be controlled here

    val callgraphOuputStream = new ByteArrayOutputStream()
    val originalSystemOuput = System.out
    System.setOut(new PrintStream(callgraphOuputStream))
    mainMethod.invoke(null, Array[String](jarPath))
    System.setOut(originalSystemOuput)

    val callgraphOutputString = callgraphOuputStream.toString(Charset.defaultCharset().toString)

    processCallGraph(callgraphOutputString.split("\\r?\\n"))
  }

  private def processCallGraph(lines: Array[String]): Array[String] =
  {
    val methodCalls = lines.filter(_.startsWith("M:"))

    val knownNeededPermissions = mappedPermissions(16).foldLeft(Array[String]())
    {
      (neededPermissions, pemission) =>
      {
        if (methodCalls exists(_.contains(pemission.classAndMethodString)))
        {
          neededPermissions :+ pemission.permissionType
        }
        else
        {
          neededPermissions
        }
      }
    }

    knownNeededPermissions.distinct
  }

  private def removeSourcecodeComments(sourceFile: String): String =
  {
    sourceFile.replaceAll("/\\*.*\\*/", "") // multi-line comments
              .replaceAll("//.*(?=\\n)", "") // single line comments
  }

  def analyseSourceCode(sourceFiles: Seq[File]): Array[String] =
  {
    val sourceFilesNoComments = sourceFiles.map(
      file => removeSourcecodeComments(Util.convertStreamToString(new FileInputStream(file)))
    ).toArray

    val intentPermissions = mappedIntentPermissions(16)

    sourceFilesNoComments.foldLeft(Array[String]())
    {
      (neededPermissions, fileContent) =>
      {
        neededPermissions ++
          intentPermissions.foldLeft(Array[String]())
          {
            (fileNeedPermissions, permission) =>
            {
              // minimalistic approach, could possibily yield false-positives

              if (fileContent.contains(permission.shortIntentTypeName) &&
                  fileContent.contains(permission.shortIntentTypeName + "_") == false) // checking if the name is not a part of other
              {
                fileNeedPermissions :+ permission.permissionType
              }
              else
              {
                fileNeedPermissions
              }
            }
          }
      }
    }
  }

  def analyseNeededPermissions(sourceFiles: Seq[File], temporaryDirectory: File, jarPath: String): Array[String] =
  {
    (analyseSourceCode(sourceFiles) ++ runJavaCallGraph(temporaryDirectory, jarPath)).distinct
  }

  def getMissingNeededPermissions(sourceDirectory: File, sourceFiles: Seq[File], temporaryDirectory: File, jarPath: String): Array[String] =
  {
    val alreadyExistingPermissions = Android.getManifestPermissions(sourceDirectory)
    val neededPermissions = analyseNeededPermissions(sourceFiles, temporaryDirectory, jarPath)

    neededPermissions diff alreadyExistingPermissions
  }

  case class Permission(permissionType: String, classAndMethod: (String, String))
  {
    def this(permissionType: String, classAndMethod: String) =
      this(permissionType, {
        val classOfPermission = classAndMethod.drop(1).takeWhile(_ != ':')

        val methodCallOfPermission = classAndMethod.drop(1 + classOfPermission.length + 2).dropWhile(_ != ' ').drop(1).takeWhile(_ != '(')

        (classOfPermission, methodCallOfPermission)
      })

    def classAndMethodString = classAndMethod._1 + ':' + classAndMethod._2
  }

  case class IntentPermission(permissionType: String, intentType: String)
  {
    def shortIntentTypeName = intentType.reverse.takeWhile(_ != '.').reverse
  }

}