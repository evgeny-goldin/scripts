
@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/repo/' )
@Grab('com.goldin:gcommons:0.5.3.5')
@GrabExclude('commons-net:commons-net')
@GrabExclude('org.codehaus.groovy:groovy-all')
@GrabExclude('xml-apis:xml-apis')
import com.goldin.gcommons.GCommons


/**
 * Validates MediaWiki internal and external links in the directory specified
 */

assert args, 'Arguments exepcted: <directory> [files include pattern]'
final int        maxWidth  = 80
final File       directory = GCommons.verify().directory( new File( args[ 0 ] ))
final String     pattern   = ( args.size() > 1 ? args[ 1 ] : '**' )
final List<File> files     = GCommons.file().files( directory, [ pattern ] )*.canonicalFile
final Map        headings  = files.inject( [:] ){ Map m, File f -> m[ f.name - GCommons.file().extension( f ) - '.' ] = processInternal( f ); m }

files.each { File f -> processExternal( f, headings )}

println "[${ files.size()}] file${ GCommons.general().s( files.size())} processed"


/**
 * Processes the file specified and checks its internal and external links.
 *
 * @param f file to check
 * @return headings found in a file
 */
Set<String> processInternal ( File f )
{
    assert f.file

    println "Processing internal links of [$f]"
    String text               = f.getText( 'UTF-8' ).replaceAll( '\uFEFF', '' )
    Set<String> headings      = text.findAll( /(?m)^\s*=+\s*([^=]+)\s*=+\s*$/ ){       // "= Text =" => "Text"
        it[ 1 ].trim().
        replaceAll( /\[.+?\s+(.+?)\]/ ){ it[ 1 ] }.                                  // [url Text1], [url Text2] => Text1, Text2
        replaceAll( /'{2,}|<code>|<\/code>/, ''  )                                   // <code>'''Text'''</code>  => Text
    } as Set

    Set<String> internalLinks = text.findAll( /\[\[#(.+?)\|.+?\]\]/ ){ it[ 1 ] } as Set // [[#Text|description]] => Text
    assert    ( internalLinks || ( ! text.contains( '[[#' )))

    for ( link in internalLinks )
    {
        assert headings.contains( link ), "Internal link [$link] references non-existing section"
    }

    headings
}



void processExternal( File f, Map<String, Set<String>> headings )
{
    assert f.file && headings

    println "Processing external links of [$f]"
    String      text          = f.getText( 'UTF-8' ).replaceAll( '\uFEFF', '' )
    Set<String> externalLinks = text.findAll( /\[(http.+?)\s+.+?\]/ ){ it[ 1 ] } as Set // [url text] => url
    assert    ( externalLinks || ( ! text.contains( '[http' )))

    for ( link in externalLinks.findAll { it.contains( '#' ) } )
    {
        def ( String fileName, String anchor ) = link.findAll( /\/([^\/]+)#(.+?)$/ ){ it[ 1, 2 ] }[ 0 ]
        if ( headings.containsKey( fileName ))
        {
            assert headings[ fileName ].contains( anchor ), "External link [$fileName#$anchor] references non-existing section"
        }
    }
}
