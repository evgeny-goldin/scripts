
@GrabResolver( name='evgenyg.artifactoryonline.com', root='http://evgenyg.artifactoryonline.com/evgenyg/repo/' )
@Grab('com.github.goldin:gcommons:0.6.2')
@GrabExclude('xml-apis:xml-apis')
@GrabExclude('asm:asm')
import com.github.goldin.gcommons.GCommons
import groovy.io.FileType

GCommons.general() // Trigger MOP updates

/**
 * Performs action on all SVN repositories checked out locally, recursively.
 * Helpful when a large number of repos are checked out and all of them need to be kept updated.
 *
 * Usage:
 * - groovy svnOp.groovy <directory>                // "svn status"
 * - groovy svnOp.groovy <directory> update status  // "svn update" + "svn status"
 * - groovy svnOp.groovy <directory> status         // "svn status"
 */

def root       = new File( args[ 0 ] )
def t          = System.currentTimeMillis()
def callback   = {
    File directory ->

    println "==> [$directory.canonicalPath]"

    if ( directory.listFiles().any{ it.name == '.git' } )
    {
        final clean      ='git status'.execute().text.contains( 'nothing to commit, working directory clean' )
        final branchName = "git rev-parse --abbrev-ref HEAD".execute().text
        final pushed     = "git log origin/$branchName..HEAD".execute().text == ''

        println "[$clean][$branchName][$pushed]"
        false // Stop recursion at this point
    }
    else
    {
        true  // Continue recursion
    }
}

println "Checking Git projects starting from [$root.canonicalPath]"

if ( callback( root ))
{
    root.recurse([ type        : FileType.DIRECTORIES,
                   stopOnFalse : true,
                   detectLoops : true ], callback )
}

println "Done, [${ ( System.currentTimeMillis() - t ).intdiv( 1000 ) }] sec"