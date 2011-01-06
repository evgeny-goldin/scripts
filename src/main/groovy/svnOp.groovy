
/**
 * Performs action on all SVN repositories checked out locally, recursively.
 * Helpful when a large number of repos are checked out and all of them need to be kept updated.
 *
 * Usage:
 * - groovy svnOp.groovy <directory>                // "svn status"
 * - groovy svnOp.groovy <directory> update status  // "svn update" + "svn status"
 * - groovy svnOp.groovy <directory> status         // "svn status"
 */

def root           = new File( args[ 0 ] )
def ops            = ( args.length > 1 ) ? args[ 1 .. -1 ] : [ 'status' ]
def topDirectories = []        // List of top-level SVN directories
def allDirectories = [ root ]  // List of all directories, starting with "root"

println "Runing SVN operation${ ( ops.size() == 1 ) ? '' : 's' } $ops starting from [$root.canonicalPath]"

root.eachDirRecurse { allDirectories << it }

for ( directory in allDirectories )
{
    def path = directory.canonicalPath

    if ( ! topDirectories.any{ path.startsWith( it ) } )
    {
        if ( new File( directory, '.svn' ).isDirectory())
        {
            topDirectories << path
            ops.each {
                def command = "svn $it $path"
                println "[$command]"
                println command.execute().text
            }
        }
    }
}
