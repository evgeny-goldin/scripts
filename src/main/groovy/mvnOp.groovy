/**
 * http://groovy.codehaus.org/Grape
 */
@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/repo/' )
@Grab('com.goldin:gcommons:0.5.3.4')
@GrabExclude('commons-net:commons-net')
@GrabExclude('org.codehaus.groovy:groovy-all')
import com.goldin.gcommons.GCommons

import groovy.io.FileType

GCommons.general() // Trigger MOP updates

/**
 * Performs action on all Maven projects recursively.
 *
 * Usage:
 * - groovy mvnOp.groovy <root directory> <Maven goals>
 */

assert System.getenv( 'M2_HOME' ), "[M2_HOME] environment variable should be defined"

def root       = new File( args[ 0 ] )
def mavenGoals = ( args.length > 1 ) ? args[ 1 .. -1 ] : [ 'clean' ]
def t          = System.currentTimeMillis()
def isWindows  = System.getProperty( 'os.name' ).toLowerCase().contains( 'windows' )
def callback   = {
    File directory ->

    println "==> [$directory.canonicalPath]"
    if ( directory.listFiles().any{ it.name == 'pom.xml' } )
    {
        def command = "${ System.getenv( 'M2_HOME' ) }/bin/mvn${ isWindows ? '.bat' : '' } ${ mavenGoals.join( ' ' )}"
        println "[$command]"
        println command.execute( null, directory ).text

        false // Stop recursion at this point
    }
    else
    {
        true  // Continue recursion
    }
}

println "Runing Maven goal${ GCommons.general().s( mavenGoals.size()) } $mavenGoals starting from [$root.canonicalPath]"

if ( callback( root ))
{
    root.recurse([ type        : FileType.DIRECTORIES,
                   stopOnFalse : true,
                   detectLoops : true ], callback )
}

println "Done, [${ ( System.currentTimeMillis() - t ).intdiv( 1000 ) }] sec"