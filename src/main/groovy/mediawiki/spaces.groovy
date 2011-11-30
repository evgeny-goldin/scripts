
@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/repo/' )
@Grab('com.goldin:gcommons:0.5.3.6')
@GrabExclude('commons-net:commons-net')
@GrabExclude('org.codehaus.groovy:groovy-all')
@GrabExclude('xml-apis:xml-apis')
import com.goldin.gcommons.GCommons


/**
 * Takes care of MediaWiki spacing between the sections: leaves two empty lines before each "= section =" (number of '=' may vary).
 * Usage: groovy spaces.groovy <directory> [files include pattern]
 */

assert args, 'Arguments exepcted: <directory> [files include pattern]'
final File       directory = GCommons.verify().directory( new File( args[ 0 ] ))
final String     pattern   = ( args.size() > 1 ? args[ 1 ] : '**' )
final List<File> files     = GCommons.file().files( directory, [ pattern ] )*.canonicalFile

for ( f in files )
{
    def lines    = f.getText( 'UTF-8' ).readLines()
    def newLines = []
    def update   = false

    lines.eachWithIndex {
        String line, int index ->

        if (( index > 1 ) && ( line ==~ /\s*(=+)[^=]+\1\s*/ )) // == Something Anything ==
        {
            if ( lines[ index - 1 ] ) { update = true; newLines << '' }
            if ( lines[ index - 2 ] ) { update = true; newLines << '' }
        }

        newLines << line.replaceAll( /\s*$/, '' ) // Trim trailing spaces
        update = ( update || ( newLines[ -1 ] != line ))
    }

    if ( update )
    {
        f.write( newLines.join( System.getProperty( 'line.separator' )), 'UTF-8' )
        println "[$f] updated"
    }
}
