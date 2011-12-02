Geb (pronounced “jeb”) is a browser automation solution. It brings together the power of WebDriver, the elegance of jQuery content selection, the robustness of Page Object modelling and the expressiveness of the Groovy language."

For more information about the project, see the [http://www.gebish.org/ http://www.gebish.org].
== How to contribute ==
=== Build Environment ===

Geb builds with [http://www.gradle.org/ "Home - Gradle" Gradle]. You do not need to have Gradle installed to work with the Geb build as gradle provides an executable wrapper that you use to drive the build.

On UNIX type environments this is <code>'''gradlew'''</code> and is <code>'''gradlew.bat'''</code> on Windows.

For example to run the Geb test suite for the entire project you would run…

<syntaxhighlight lang="text">
./gradlew test
</syntaxhighlight>


=== Contributing Documentation ===

Geb documentation comes in two forms; the manual and the api (i.e. the Groovydoc amongst the source).
==== The Manual ====

The manual project can be found at <code>'''doc/manual'''</code> within the project tree. The [http://daringfireball.net/projects/markdown/ "Daring Fireball: Markdown" Markdown] source files, HTML templates, CSS and Javascript that make up the manual can be found at <code>'''doc/manual/src'''</code> (the manual is compiled using a tool called [http://github.com/alkemist/markdown2book markdown2book]).

Most documentation contributions are simply modifications to these files.

To compile the manual in or to see any changes made, simply run (from the root of the geb project)…

<syntaxhighlight lang="text">
./gradlew :doc:manual:compileManual
</syntaxhighlight>


You will then find the compiled HTML in the directory <code>'''doc/manual/build/manual'''</code>
==== The API reference ====

The API reference is made up of the Groovydoc (like Javadoc) that annotates the groovy files for the different modules in <code>'''module/'''</code>. To make a change to the reference API documentation, find the corresponding file in <code>'''module/«module»/src/main/groovy'''</code> and make the change.

You can then generate the API reference HTML by running…

<syntaxhighlight lang="text">
./gradlew :doc:manual:compileApi
</syntaxhighlight>


You will then find the compiled HTML in the directory <code>'''doc/manual/build/manual/api'''</code>

> Note that you can build the manual chapters and reference API in one go with <code>'''./gradlew doc:manual:compile'''</code>
=== Contributing features/patches ===

The source code for all of the modules is contained in the <code>'''module/'''</code> directory.

To run the tests after making your change to a module, you can run…

<syntaxhighlight lang="text">
./gradlew :module:«module-name»:test
</syntaxhighlight>


There are lots of example tests in the <code>'''geb-core'''</code> module that use the classes from the <code>'''test-utils'''</code> module for running against an in memory HTTP server.

To run the entire test suite, run…

<syntaxhighlight lang="text">
./gradlew test
</syntaxhighlight>


== Development Mailing List ==

If you want to do some work on Geb and want some help, you can join the <code>'''dev@geb.codehaus.org'''</code> mailing list via [http://xircles.codehaus.org/projects/geb/lists "Codehaus: Geb: Lists" Codehaus' Xircles].