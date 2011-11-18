
@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/repo/' )
@Grab('com.goldin:gcommons:0.5.3.5')
@GrabExclude('commons-net:commons-net')
@GrabExclude('org.codehaus.groovy:groovy-all')
@GrabExclude('xml-apis:xml-apis')
import com.goldin.gcommons.GCommons


/**
 * Validates MediaWiki internal and external links in files/directories specified
 */

assert args.size() == 2, 'Two arguments exepcted: <directory> <files include pattern>'
final File       directory = GCommons.verify().directory( new File( args[ 0 ] ))
final String     pattern   = args[ 1 ]
final List<File> files     = GCommons.file().files( directory, [ pattern ] )*.canonicalFile.collect{ File f -> process( f ) }

println "[${ files.size()}] file${ GCommons.general().s( files.size())} processed"


File process ( File f )
{
    println "Processing [$f]"
    f
}
