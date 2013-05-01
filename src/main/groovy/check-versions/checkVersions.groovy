
@Grab( 'com.google.guava:guava:14.0-rc2' )
import com.google.common.io.ByteStreams
import com.google.common.io.InputSupplier
import java.util.zip.Adler32

/**
 * Validates pages checksum to notify of new versions released.
 * URLs map: key   - URL to watch
 *           value - one or more elements list:
 *                   [0]    - (required) page checksum
 *                   [1..N] - (optional) regexes of elements to remove before checking the checksum
 */


final URLs = [
    'http://confluence.jetbrains.com/display/TW/Previous+Releases+Downloads'       : [ 2223232451, /(?s).+<div class="wiki-content">/,
                                                                                                   /(?s)<rdf:RDF xmlns:rdf.+/ ],
    'http://www.jetbrains.com/youtrack/download/get_youtrack.html'                 : [ 2076456293, /(?s).+?<dt>WAR<\/dt>/,
                                                                                                   /(?s)<dt class="gray">.+/ ],
    'http://services.gradle.org/distributions'                                     : [ 3437334446, /(?s)^.+?<ul class="items">/,
                                                                                                   /(?s)<\/ul>.+$/ ],
    'http://sourceforge.net/projects/codenarc/files/codenarc/'                     : [ 11162705,   /(?s)^.+Looking for the latest version/,
                                                                                                   /(?s)<div id="files-sidebar">.+/,
                                                                                                   /<td headers="files_date_h" class="opt">.+?<\/td>/,
                                                                                                   /<td headers="files_status_h" class="status folder">.+?<\/td>/,
                                                                                                   /(?s)<div id="sidebar-ads">.+/,
                                                                                                   /document.write\(.+?\)/,
                                                                                                   /(?s)<footer id="site-copyright-footer">.+?<\/footer>/,
                                                                                                   /<script src="http:\/\/a.fsdn.com.+?<\/script>/,
                                                                                                   /<script type="text\/javascript" src="http:\/\/a.fsdn.com.+?<\/script>/ ]
]


URLs.each {
    String url, List data ->

    final oldChecksum     = data.head()
    final text            = data.tail().inject( url.toURL().getText( 'UTF-8' )) { String s, String regex -> s.replaceAll( regex, '' )}
    final checksum        = checksum( text )
    final checksumChanged = ( checksum != oldChecksum )

    println ( "URL [$url], checksum [$oldChecksum] => [$checksum], changed [$checksumChanged]\n-----\n$text\n-----\n" )
    assert  ( ! checksumChanged ), "URL [$url] checksum has changed: [$oldChecksum] => [$checksum]"
}


long checksum( String text )
{
    ByteStreams.getChecksum({ new ByteArrayInputStream( text.getBytes( 'UTF-8' )) } as InputSupplier, new Adler32())
}
