# Various helpful Groovy, shell and batch scripts

* `"src/main/groovy"`
    * `"mvnOp.groovy"` - runs Maven command recursively through all checked-out projects on a disk
    * `"svnOp.groovy"` - runs SVN command recursively through all checked-out projects on a disk
    * `"mediawiki/g2m.groovy"` - converts GitHub flavored Markdown files to MediaWiki syntax, see [Wiki](http://evgeny-goldin.com/wiki/MediaWiki_Tools) for more details.
    * `"mediawiki/links.groovy"` - checks internal and cross-pages links in MediaWiki files, see [Wiki](http://evgeny-goldin.com/wiki/MediaWiki_Tools) for more details.
    * `"mediawiki/spaces.groovy"` - makes sure proper spacing is used in MediaWiki files, see [Wiki](http://evgeny-goldin.com/wiki/MediaWiki_Tools) for more details.
    * `"mediawiki/y2m.groovy"` - converts YouTrack CSV export files to MediaWiki tables, see [Wiki](http://evgeny-goldin.com/wiki/MediaWiki_Tools) for more details.
* `"src/main/shell"`
* `"src/main/batch"`

Test are run by `"run-tests.sh"`.
