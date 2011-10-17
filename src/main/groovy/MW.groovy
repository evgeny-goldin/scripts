
@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/repo/' )
@Grab('com.goldin:gcommons:0.5.3.4')
@GrabExclude('commons-net:commons-net')
@GrabExclude('org.codehaus.groovy:groovy-all')
import com.goldin.gcommons.GCommons

GCommons.general() // To trigger MOP updates


/**
 * Takes care of MediaWiki spacing between the sections: leaves two empty lines before each /=(+) section =(+)/.
 * Usage: groovy MW.groovy < input.txt > output.txt
 */

def lines = System.in.splitWith( 'eachLine' )
lines.eachWithIndex {
    String line, int index ->

    if (( index > 1 ) && ( line ==~ /\s*(=+)[^=]+\1\s*/ )) // == Something Anything ==
    {
        if ( lines[ index - 1 ] ) println ""
        if ( lines[ index - 2 ] ) println ""
    }

    println line.replaceAll( /\s*$/, '' )
}
