
@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/repo/' )
@Grab('com.goldin:gcommons:0.5.3.6')
@GrabExclude('commons-net:commons-net')
@GrabExclude('org.codehaus.groovy:groovy-all')
@GrabExclude('xml-apis:xml-apis')
import com.goldin.gcommons.GCommons


/**
 * Takes care of MediaWiki spacing between the sections: leaves two empty lines before each "= section =" (number of '=' may vary).
 * Usage: groovy spaces.groovy <directory> [files include pattern] [fail if update]
 */

assert args, 'Arguments expected: <directory> [files include pattern] [fail if update]'
final File       directory    = GCommons.verify().directory( new File( args[ 0 ] ))
final String     pattern      = ( args.size() > 1 ? args[ 1 ]                    : '**'  )
final boolean    failIfUpdate = ( args.size() > 2 ? Boolean.valueOf( args[ 2 ] ) : false )
final List<File> files        = GCommons.file().files( directory, [ pattern ] )*.canonicalFile
boolean          filesUpdated = false
final long       time         = System.currentTimeMillis()

println "Fixing spaces in [$directory]/[$pattern], fail if update [$failIfUpdate]"

for ( f in files )
{
    def lines       = f.getText( 'UTF-8' ).readLines()
    def newLines    = []
    def fileUpdated = false

    lines.eachWithIndex {
        String line, int index ->

        if (( index > 1 ) && ( line ==~ /\s*(=+)[^=]+\1\s*/ )) // == Something Anything ==
        {
            if ( lines[ index - 1 ] ) { fileUpdated = true; newLines << '' }
            if ( lines[ index - 2 ] ) { fileUpdated = true; newLines << '' }
        }

        newLines << line.replaceAll( /\s*$/, '' ) // Trim trailing spaces
        fileUpdated = ( fileUpdated || ( newLines[ -1 ] != line ))
    }

    if ( fileUpdated )
    {
        f.write( newLines.join( System.getProperty( 'line.separator' )), 'UTF-8' )
        println "[$f] updated"
    }

    filesUpdated = ( filesUpdated || fileUpdated )
}

assert  ! ( filesUpdated && failIfUpdate )
println "Fixing spaces in [$directory]/[$pattern], fail if update [$failIfUpdate] - Done (${ System.currentTimeMillis() - time } ms)"

