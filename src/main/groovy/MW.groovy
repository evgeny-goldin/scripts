
/**
 * Takes care of MediaWiki spacing between the sections: leaves two empty lines after each /=(+) section =(+)/.
 * Usage: groovy MW.groovy < input.txt > output.txt
 */

def lines = []
System.in.eachLine( 'UTF-8' ) { lines << it }

lines.eachWithIndex {
    String line, int index ->

    if (( index > 1 ) && ( line ==~ /(=+)[^=]+\1/ )) // == Something Anything ==
    {
        if ( lines[ index - 1 ] ) println ""
        if ( lines[ index - 2 ] ) println ""
    }

    println line
}
