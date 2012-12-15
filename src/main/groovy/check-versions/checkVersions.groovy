
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
    'http://confluence.jetbrains.net/display/TW/Previous+Releases+Downloads'       : [ 4175323664, /(content|value)=".+?"/,
                                                                                                   /"\/s\/en\/2172\/.+?"/ ],
    'http://repository.jetbrains.com/kotlin/org/jetbrains/kotlin/kotlin-compiler/' : [ 3480225374, /(\d\d-\w+-\d{4} \d\d:\d\d)|(\d+ bytes)/,
                                                                                                   /(Artifactory\/\d+\.\d+\.\d+)/ ],
    'http://services.gradle.org/distributions'                                     : [ 3795661861 ],
    'http://sourceforge.net/projects/codenarc/files/codenarc/'                     : [ 2789492120, /sfs-consume-\d+/,
                                                                                                   /<meta id="webtracker".+?>/,
                                                                                                   /document.write\(.+?\)/,
                                                                                                   /\d+ downloads/,
                                                                                                   /<td headers="files_status_h" class="status folder">.+?<\/td>/,
                                                                                                   /(?s)<footer id="site-copyright-footer">.+?<\/footer>/ ]
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
