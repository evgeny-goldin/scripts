
/**
 * Performs action on SVN repositories recursively
 * Usage:
 * - groovy svnOp.groovy directory
 * - groovy svnOp.groovy directory update status
 * - groovy svnOp.groovy directory status
 */

def root        = new File( args[ 0 ] )
def ops         = ( args.length > 1 ) ? args[ 1 .. -1 ] : [ 'status' ]
def directories = [] // List of top-level SVN directories

println "Runing SVN operations $ops starting from [$root.canonicalPath]"

new File( args[ 0 ] ).eachDirRecurse {

    File directory ->

    def path = directory.canonicalPath

    if ( ! directories.any{ path.startsWith( it ) } )
    {
        if ( new File( directory, '.svn' ).isDirectory())
        {
            directories << path
            ops.each {
                def command = "svn $it $path"
                println "[$command]"
                println command.execute().text
            }
        }
    }
}
