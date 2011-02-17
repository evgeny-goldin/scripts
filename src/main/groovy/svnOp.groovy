
@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/libs-releases/' )
@Grab( group='com.goldin', module='gcommons', version='0.5' )
import com.goldin.gcommons.GCommons
import groovy.io.FileType


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
def operations = ( args.length > 1 ) ? args[ 1 .. -1 ] : [ 'status' ]
def hasSvn     = { File[] dirs -> dirs.every { File dir -> dir.listFiles().any{ File f -> ( f.name == '.svn' ) }}}

println "Runing SVN operation${ GCommons.general().s( operations.size()) } $operations starting from [$root.canonicalPath]"

GCommons.file().recurse( root, [ filterType   : FileType.DIRECTORIES,
                                 type         : FileType.DIRECTORIES,
                                 stopOnFilter : true,
                                 filter       : { File dir -> (( dir.name != '.svn' ) && ( ! hasSvn( dir, dir.parentFile ))) } ] ) {
    File directory ->
    if ( hasSvn( directory ))
    {
        for ( operation in operations )
        {
            def command = "svn $operation $directory.canonicalPath"
            println "[$command]"
            println command.execute().text
        }
    }
}
