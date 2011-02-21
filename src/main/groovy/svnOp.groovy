
@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/libs-releases/' )
@Grab( group='com.goldin', module='gcommons', version='0.5' )
import com.goldin.gcommons.GCommons
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

assert GroovySystem.version.startsWith( '1.8.0' ), \
       "Only Groovy version \"1.8.0-beta-4\" and higher is supported. Current version is [${ GroovySystem.version }]"

def root       = new File( args[ 0 ] )
def operations = ( args.length > 1 ) ? args[ 1 .. -1 ] : [ 'status' ]
def t          = System.currentTimeMillis()

println "Runing SVN operation${ GCommons.general().s( operations.size()) } $operations starting from [$root.canonicalPath]"

root.recurse([ type        : FileType.DIRECTORIES,
               stopOnFalse : true,
               detectLoops : true ] ) {

    File directory ->
    println "==> [$directory.canonicalPath]"
    if ( directory.listFiles().any{ it.name == '.svn' } )
    {
        for ( operation in operations )
        {
            def command = "svn $operation $directory.canonicalPath"
            println "[$command]"
            println command.execute().text
        }
        false // Stop recursion at this point
    }
    else
    {
        true  // Continue recursion
    }
}

println "Done, [${ ( System.currentTimeMillis() - t ).intdiv( 1000 ) }] sec"