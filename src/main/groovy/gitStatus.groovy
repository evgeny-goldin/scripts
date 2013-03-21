
@GrabResolver( name='evgenyg.artifactoryonline.com', root='http://evgenyg.artifactoryonline.com/evgenyg/repo/' )
@Grab( 'com.github.goldin:gcommons:0.6.3-SNAPSHOT' )
@Grab( 'org.slf4j:slf4j-nop:1.7.2' )
@GrabExclude( 'xml-apis:xml-apis' )
@GrabExclude( 'org.sonatype.sisu.inject:cglib' )
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

final root            = new File( args[ 0 ] )
final message         = "Checking Git projects starting from [${ root.canonicalPath }] "
final redColorStart   = '\033[1;31m'
final redColorEnd     = '\033[0m'
final greenColorStart = '\033[1;32m'
final greenColorEnd   = '\033[0m'
final results         = [:]
final sticks          = [ '|', '/', '-', '\\', '|', '/', '-', '\\' ]
int   counter         = 0L
long  foldersChecked  = 0
final t               = System.currentTimeMillis() 
final printStick      = { final j = ( counter % sticks.size()); print "\r${ message }${ sticks[ j ] }"; counter++; if ( j == ( sticks.size() - 1 )){ counter = 0 }}
final callback        = {
    File directory ->

    foldersChecked++
    final exec = { String command -> command.execute( [], directory ).text.trim() } 
    
    if ( directory.listFiles().any{ it.name == '.git' } )
    {
        final clean      = exec( 'git status' ).contains( 'nothing to commit, working directory clean' )
        final branchName = exec( 'git rev-parse --abbrev-ref HEAD' )
        final pushed     = ( exec( "git log origin/$branchName..HEAD" ) == '' )
        final origin     = exec( "git remote -v" ).readLines().find{ it.with{ contains( 'origin' ) && contains( '(push)' ) }}.tokenize()[ 1 ]

        results[ directory.canonicalPath ] = [ clean, pushed, origin ]

        printStick()    
        false // Stop recursion at this point
    }
    else
    {
        printStick()
        true  // Continue recursion
    }
}

print message

if ( callback( root ))
{
    root.recurse([ type        : FileType.DIRECTORIES,
                   stopOnFalse : true,
                   detectLoops : true ], callback )
}

println ( '\r' + message + ' ' )
println "[$foldersChecked] folder${ foldersChecked == 1 ? '' : 's' } checked in [${ System.currentTimeMillis() - t }] ms"

final maxPathSize = results.keySet()*.size().max()

results.each {
    String path, List status ->

    final clean      = status[ 0 ]
    final pushed     = status[ 1 ]
    final origin     = status[ 2 ]
    final problems   = ! ( clean && pushed )

    println "[${ problems ? redColorStart : '' }${ path }${ problems ? redColorEnd : '' }]:".padRight( maxPathSize + 4 + ( problems ? 11 : 0 )) +
            "[${ clean    ? "${ greenColorStart }clean${ greenColorEnd }"  : "${ redColorStart }dirty${ redColorEnd }" }], " + 
            "[${ pushed   ? "${ greenColorStart }pushed${ greenColorEnd }" : "${ redColorStart }waiting${ redColorEnd }" }]" +
            " ($origin)"
}
