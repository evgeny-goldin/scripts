
@GrabResolver( name='evgenyg.artifactoryonline.com', root='http://evgenyg.artifactoryonline.com/evgenyg/repo/' )
@Grab('com.google.guava:guava:13.0.1')
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
    'http://confluence.jetbrains.net/display/TW/Previous+Releases+Downloads'       : [ 1645850325, /(?s).+<div class="wiki-content">/,
                                                                                                   /(?s)<rdf:RDF xmlns:rdf.+/ ],
    'http://repository.jetbrains.com/kotlin/org/jetbrains/kotlin/kotlin-compiler/' : [ 2352484388, /(\d\d-\w+-\d{4} \d\d:\d\d)|(\d+ bytes)/,
                                                                                                   /(Artifactory\/\d+\.\d+\.\d+)/ ],
    'http://services.gradle.org/distributions'                                     : [ 1158170063, /(?s)^.+?<ul class="items">/,
                                                                                                   /(?s)<\/ul>.+$/ ],
    'http://sourceforge.net/projects/codenarc/files/codenarc/'                     : [ 3971608245, /(?s)^.+Looking for the latest version/,
                                                                                                   /(?s)<div id="files-sidebar">.+/,
                                                                                                   /<td headers="files_status_h" class="status folder">.+?<\/td>/,
                                                                                                   /(?s)<div id="sidebar-ads">.+/,
                                                                                                   /document.write\(.+?\)/,
                                                                                                   /(?s)<footer id="site-copyright-footer">.+?<\/footer>/,
                                                                                                   /<script src="http:\/\/a.fsdn.com.+?<\/script>/,
                                                                                                   /<script type="text\/javascript" src="http:\/\/a.fsdn.com.+?<\/script>/ ]
]

for ( entry in URLs )
{
    final url            = entry.key
    final oldCheksum     = entry.value.head()
    final excludeRegexes = entry.value.tail()
    text                 = excludeRegexes.inject( url.toURL().getText( 'UTF-8' )) {
        String text, String regex -> text.replaceAll( regex, '' )
    } as String

    println( "URL [$url], old checksum [$oldCheksum]\n-----\n$text\n-----\n" )

    final  checksum  = checksum( text )
    assert checksum == oldCheksum, "URL [$url] checksum has changed to [$checksum]"
}


long checksum( String text )
{
    ByteStreams.getChecksum( new InputSupplier(){ InputStream getInput(){ new ByteArrayInputStream( text.getBytes( 'UTF-8' )) }},
                             new Adler32())
}
