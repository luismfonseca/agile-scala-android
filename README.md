# Towards agile Android development with Scala
[![Build Status](https://secure.travis-ci.org/luismfonseca/agile-scala-android.png?branch=master)](http://travis-ci.org/luismfonseca/agile-scala-android) [![Stories in Ready](https://badge.waffle.io/luismfonseca/agile-scala-android.png?label=ready&title=Ready)](https://waffle.io/luismfonseca/agile-scala-android)

agile-scala-android is an extension for the Scala build tool [sbt][] which aims to make it as simple as possible to get started with Scala on Android.

Together with several technologies, you can create and build a simple Android Scala project in a matter of seconds. The sbt plugin [pfn/android-sdk-plugin][] is used to handle the Android building aspect, and this plugin brings more features that are added ontop of it.

## Getting started

In most cases, a global installation will make the most sense. This is strongly suggested as the plugin can be used to create entirely new Android projects from scratch.

If you don't already have one, create an `~/.sbt/0.13/plugins` directory. And inside of it, create an `agile-scala-android.sbt` (it doesn't matter what you call it) file containing the line:

    addSbtPlugin("pt.pimentelfonseca" % "agile-scala-android" % "0.3")
    
Next, you can add the settings `agileAndroidNewProjectTask` globally, which will add just a single task: `npa`. To do this, create a file under `~/.sbt/0.13` called `npa.sbt` (it doesn't matter what you call this either) containing the line:

    seq(AgileAndroidKeys.agileAndroidNewProjectTask: _*)

If this sounds familiar to the [np][] project - great. This task is remarkably similiar to np's, only for android projects.

## Using the plugin
The following tasks are implemented:

**New Android Project**

This task serves to create a new android project in the current folder that sbt is running on. The signature is as follows:

    $ npa <package> <minSdkVersion>

As an example, the following command is valid:

    $ npa pt.test.ok 19

This creates an Android project, targetting the version 19 of the API, with pt.test.ok as the main package. Sbt build files are included and generated to add dependecies on other projects - this requires you to type `reload`, to allow sbt to load the new build definitions and resolve the external dependencies.

A `.gitignore` file is also included to simplifiy git versioning.

**Model generation**

This task creates a model and places it under the models package. It uses Scala case classes, and it simply adds the fields specified in the command while also including the necessary imports.

    $ generate <modelName> <attributes>

As an example, the following command is valid:

    $ generate Post title:String numberOfLikes:Integer date:Date

This will create the Post class inside the models package with the specified attributes. The result can naturally be modified afterwards. 

**Database generation**

Just like most web application frameworks, database is now a core built-in part of the application. This means, that every model will have a correspondent SQL table automatically created.

To perform a migration you can use the following command:

    $ migrateDatabase

This feature is currently under development and there are some known limitations: new tables are generated but the migration file must be created manually.


**Scaffolding**

Scaffolding is implemented in a very similar way to [Ruby on Rails][], only applied in the context of Android development. This means that layouts and activities+fragments are generated automatically from a model, by looking at its public fields and implementing the [CRUD][] and list operations.

Continuing the previous example, running the following:

    $ scaffold Post

Would result in the something similar to this:


<p align="center">
  <img alt="Scaffold Example #1" src="http://paginas.fe.up.pt/~ei10139/pdis/_media/media-20140310_1_.png?cache=" height="320" width="200"><span> </span>
  <img alt="Scaffold Example #2" src="http://paginas.fe.up.pt/~ei10139/pdis/_media/media-20140310_2_.png?cache=" height="320" width="200"><span> </span>
  <img alt="Scaffold Example #3" src="http://paginas.fe.up.pt/~ei10139/pdis/_media/media-20140310_3_.png?cache=" height="320" width="200"><span> </span>
  <img alt="Scaffold Example #4" src="http://paginas.fe.up.pt/~ei10139/pdis/_media/media-20140310_4_.png?cache=" height="320" width="200">
</p>


We can elaborate a bit more our Post model and also add the Author and Comment models:

    $ generate Author name:String age:Int
    $ generate Comment author:Author date:Date text:String
    $ generate Post author:Author title:String content:String coments:Array[Comment] date:Date
    $ scaffold Post

Now we get the following:
<p align="center">
  <img alt="Scaffold Example #1" src="http://paginas.fe.up.pt/~ei10139/pdis/_media/screenshot_2014-04-21-17-53-27.png?w=200&amp;tok=d6def1" height="320" width="200"><span> </span>
  <img alt="Scaffold Example #2" src="http://paginas.fe.up.pt/~ei10139/pdis/_media/screenshot_2014-04-21-17-54-23.png?w=200&amp;tok=2391de" height="320" width="200"><span> </span>
  <img alt="Scaffold Example #3" src="http://paginas.fe.up.pt/~ei10139/pdis/_media/screenshot_2014-04-21-17-54-38.png?w=200&amp;tok=f057fe" height="320" width="200"><span> </span>
  <img alt="Scaffold Example #4" src="http://paginas.fe.up.pt/~ei10139/pdis/_media/screenshot_2014-04-21-17-55-24.png?w=200&amp;tok=8e3c2e" height="320" width="200">
</p>


The current main known limitation is a model with an array of primitive types - a possible workaround is to create a new model and use it as the array.

**Automatic Android permissions**

When programming in Android, writing code that requires a specific permission is very common. However, there are 3 possible situations: (i) you remembered you needed the permission and manually added; (ii) the application crashed and you got a descriptive error message; (iii) the application crashed and you had to go to Stackoverflow to get your awnser. In any of these cases, work is placed on the developer --- this is far from ideal.

[PScout][] is a tool that maps the Android API function calls to the required permission(s). This plugin makes use of this information and checks if the application being developed calls a permission that is missing from the manifest.

You can run this command using:

    $ checkPermissions

But by default, this is performed everytime you run\install an application. The permissions are added automatically to the manifest file (or not) according to the setting key <i>permissionsAddAutomatically</i>.

Not all permissions are caught, and some false-positives might occur. Nonetheless, most cases are covered.

## Usage statistics

To better understand the usability and usefulness of this tool, the commands issued are submitted anonymously by default. However, you can control this behaviour with the following setting key:

    $ sendAnonymousUsageStatistics

Your feedback is important, so please feel free to add a project issue here.

## Hacking on the plugin

If you need make modifications to the plugin itself, you can compile and install it locally (you need at least sbt 0.13.x to build it):

    $ git clone git://github.com/luismfonseca/agile-scala-android.git
    $ cd agile-scala-android
    $ sbt publish-local

And don't forget to create and run tests:

    $ sbt scripted


[sbt]: https://github.com/harrah/xsbt/wiki
[pfn/android-sdk-plugin]: https://github.com/pfn/android-sdk-plugin
[np]: https://github.com/softprops/np
[Ruby on Rails]: http://rubyonrails.org
[CRUD]: http://en.wikipedia.org/wiki/Create,_read,_update_and_delete
[PScout]: http://pscout.csl.toronto.edu/PScout-CCS2012-slides.pdf
