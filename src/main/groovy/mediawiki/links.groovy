package mediawiki

@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/repo/' )
@Grab('com.goldin:gcommons:0.5.3.6')
@GrabExclude('xml-apis:xml-apis')
import com.goldin.gcommons.GCommons


/**
 * Validates MediaWiki internal and external links in the directory specified
 * Usage: groovy links.groovy <directory> [files include pattern]
 */

assert args, 'Arguments expected: <directory> [files include pattern]'
final File       directory = GCommons.verify().directory( new File( args[ 0 ] ))
final String     pattern   = ( args.size() > 1 ? args[ 1 ] : '**' )
final List<File> files     = GCommons.file().files( directory, [ pattern ] )*.canonicalFile
final int        maxName   = files*.path*.size().max()
final Map        headings  = files.inject( [:] ){ Map m, File f -> m[ f.name - GCommons.file().extension( f ) - '.' ] = processInternal( f, maxName ); m }

files.each { File f -> processExternal( f, headings, maxName )}

println "[${ files.size()}] file${ GCommons.general().s( files.size())} processed"


/**
 * Processes the file specified and checks its internal and external links.
 *
 * @param f file to check
 * @return headings found in a file
 */
Set<String> processInternal ( File f, int maxNameLength )
{
    assert f.file
    printFileName( f, maxNameLength )

    String       text     = f.getText( 'UTF-8' ).replaceAll( '\uFEFF', '' )
    List<String> headings = text.findAll( /(?m)^\s*=+ +(.+?) +=+\s*$/ ) {  // "= Text =" => "Text"
        it[ 1 ].trim().
        replaceAll( /\[.+?\s+(.+?)\]/ ){ it[ 1 ] }.                        // [url Text1], [url Text2] => Text1, Text2
        replaceAll( /'{2,}|<code>|<\/code>/, ''  )                         // <code>'''Text'''</code>  => Text
    }

    Set<String> internalLinks = text.findAll( /\[\[#(.+?)\|.+?\]\]/ ){ it[ 1 ] } as Set // [[#Text|description]] => Text
    assert    ( internalLinks || ( ! text.contains( '[[#' )))

    internalLinks.findAll{ ! headings.contains( it ) }.with {
        assert ( ! delegate ), "Broken internal links:\n* [${ delegate.join( ']\n* [' )}]"
    }

    printNumber( 'internal', internalLinks.size())
    headings
}


void processExternal( File f, Map<String, Set<String>> headings, int maxNameLength )
{
    assert f.file && headings
    printFileName( f, maxNameLength )

    String      text          = f.getText( 'UTF-8' ).replaceAll( '\uFEFF', '' )
    Set<String> externalLinks = text.findAll( /\[(http.+?)\s+.+?\]/ ){ it[ 1 ] } as Set // [url text] => url
    assert    ( externalLinks || ( ! text.contains( '[http' )))

    externalLinks.findAll { it.with{ contains( '#' ) && ( ! contains( '/#' )) }}.
                  findAll {
                      def ( String fileName, String anchor ) = it.findAll( /\/([^\/]+)#(.+?)$/ ){ it[ 1, 2 ] }[ 0 ]
                      anchor = anchor.replace( '.3C', '<' ).
                                      replace( '.3E', '>' ).
                                      replace( '.22', '"' ).
                                      replace( '.2C', ',' ).
                                      replace( '_',   ' ' )
                      headings.containsKey( fileName ) && ( ! headings[ fileName ].contains( anchor ))
                  }.with {
        assert ( ! delegate ), "Broken external links:\n* [${ delegate.join( ']\n* [' )}]"
    }

    printNumber( 'external', externalLinks.size())
}


void printFileName( File f, int maxNameLength ) { print   String.format( "%-${ maxNameLength + 2 }s - ", "[$f]" ) }
void printNumber( String description, int n   ) { println String.format( "%-3d $description link%s", n, GCommons.general().s( n )) }