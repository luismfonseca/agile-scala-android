[![Stories in Ready](https://badge.waffle.io/luismfonseca/agile-scala-android.png?label=ready&title=Ready)](https://waffle.io/luismfonseca/agile-scala-android)
# Towards agile Android development with Scala

agile-scala-android is an extension for the Scala build tool [sbt][] which aims to make it as simple as possible to get started with Scala on Android.

Together with several technologies, you can create and build a simple Android Scala project in a matter of seconds.

## Getting started

In most cases, a global installation will make the most sense. This is strongly suggested as the plugin can be used to create entirely new Android projects from scratch.

If you don't already have one, create an `~/.sbt/0.13/plugins` directory. And inside of it, create an `agile-scala-android.sbt` (it doesn't matter what you call it) file containing the line:

    addSbtPlugin("pt.pimentelfonseca.luis" % "agile-scala-android" % "0.1-SNAPSHOT")
    
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

## Current work

Scaffolding is being implemented, very similar to what [Ruby on Rails][] has, only applied in the context of Android development. This means that layouts and activities+fragments are generated automatically from a model, by looking at its public fields and implementing the [CRUD][] and list operations.

Continuing the previous example, running the following:

    $ scaffold Post

Would result in the something similar to this:


<p align="center">
  <img alt="Scaffold Example #1" src="http://paginas.fe.up.pt/~ei10139/pdis/_media/media-20140310_1_.png?cache=" height="320" width="200"><span> </span>
  <img alt="Scaffold Example #2" src="http://paginas.fe.up.pt/~ei10139/pdis/_media/media-20140310_2_.png?cache=" height="320" width="200"><span> </span>
  <img alt="Scaffold Example #3" src="http://paginas.fe.up.pt/~ei10139/pdis/_media/media-20140310_3_.png?cache=" height="320" width="200"><span> </span>
  <img alt="Scaffold Example #4" src="http://paginas.fe.up.pt/~ei10139/pdis/_media/media-20140310_4_.png?cache=" height="320" width="200">
</p>

## Hacking on the plugin

If you need make modifications to the plugin itself, you can compile and install it locally (you need at least sbt 0.13.x to build it):

    $ git clone git://github.com/luismfonseca/agile-scala-android.git
    $ cd agile-scala-android
    $ sbt publish-local

And don't forget to create and run tests:

    $ sbt scripted


## Build Status from Travis CI - [![Build Status](https://secure.travis-ci.org/luismfonseca/agile-scala-android.png?branch=master)](http://travis-ci.org/luismfonseca/agile-scala-android)


[sbt]: https://github.com/harrah/xsbt/wiki
[np]: https://github.com/softprops/np
[Ruby on Rails]: http://rubyonrails.org
[CRUD]: http://en.wikipedia.org/wiki/Create,_read,_update_and_delete

