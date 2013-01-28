
@GrabResolver( name='evgenyg.artifactoryonline.com', root='http://evgenyg.artifactoryonline.com/evgenyg/repo/' )
@Grab( 'com.github.goldin:gcommons:0.6.3-SNAPSHOT' )
@Grab( 'org.slf4j:slf4j-nop:1.7.2' )
@GrabExclude( 'xml-apis:xml-apis' )
@GrabExclude( 'asm:asm' )
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

final root       = new File( args[ 0 ] )
final t          = System.currentTimeMillis()
final colorStart = '\033[1;31m'
final colorEnd   = '\033[0m'
final callback   = {
    File directory ->

    final exec = { String command -> command.execute( [], directory ).text.trim() } 
    
    if ( directory.listFiles().any{ it.name == '.git' } )
    {
        final clean      = exec( 'git status' ).contains( 'nothing to commit, working directory clean' )
        final branchName = exec( 'git rev-parse --abbrev-ref HEAD' )
        final pushed     = ( exec( "git log origin/$branchName..HEAD" ) == '' )
        final problems   = ! ( clean && pushed )

        println "[${ problems ? colorStart : '' }${ directory.canonicalPath }${ problems ? colorEnd : '' }]: " + 
                "[${ clean    ? 'clean'  : "$colorStart**not committed**$colorEnd" }], " + 
                "[${ pushed   ? 'pushed' : "$colorStart**not pushed**$colorEnd" }]"
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