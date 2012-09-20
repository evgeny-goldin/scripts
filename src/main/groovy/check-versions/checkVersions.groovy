
@GrabResolver( name='evgenyg.artifactoryonline.com', root='http://evgenyg.artifactoryonline.com/evgenyg/repo/' )
@Grab('com.google.guava:guava:13.0.1')
import com.google.common.io.ByteStreams
import com.google.common.io.InputSupplier
import org.apache.ivy.plugins.repository.Repository

import java.util.regex.Pattern
import java.util.zip.Adler32


/**
 * Validates pages checksum to notify of new versions released.
 * URLs map: key   - URL to watch
 *           value - one or two-elements array:
 *                   [0] - page checksum
 *                   [1] - (optional) regex of elements to remove before checking the checksum
 */


final URLs = [ 'http://confluence.jetbrains.net/display/TW/Previous+Releases+Downloads'       : [ 2963519938, /(content|value)="\w+"/    ],
               'http://repository.jetbrains.com/kotlin/org/jetbrains/kotlin/kotlin-compiler/' : [ 4059162509, /(\d\d-\w+-\d{4} \d\d:\d\d)|(\d+ bytes)|(Artifactory\/\d+\.\d+\.\d+)/ ]]

for ( entry in URLs )
{
    final url          = entry.key
    final oldCheksum   = entry.value[ 0 ]
    final excludeRegex = ( entry.value.size() > 1 ? entry.value[ 1 ] : null ) as String
    text               = url.toURL().getText( 'UTF-8' )

    if ( excludeRegex )
    {
        text = text.replaceAll( Pattern.compile( excludeRegex ), '' )
    }

    println( "URL [$url], old checksum [$oldCheksum]\n---\n$text\n---\n" )

    final  checksum  = checksum( text )
    assert checksum == oldCheksum, "URL [$url] checksum has changed to [$checksum]"
}


long checksum( String text )
{
    ByteStreams.getChecksum( new InputSupplier(){
        InputStream getInput(){ new ByteArrayInputStream( text.getBytes( 'UTF-8' )) }
    }, new Adler32())
}
