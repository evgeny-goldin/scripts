
/**
 * Reads commands to execute from stdin, outputs their average run time to stdout
 */

final nTimes       = ( args ? args[ 0 ] as int : 10 )
final inputStream  = System.in
final tempFile     = File.createTempFile( 'timer-temp', '.bat' )
final tempFilePath = tempFile.absolutePath

/**
 * Executes each line
 */
inputStream.readLines( 'UTF-8' ).findAll{ it }.each
{
    String line ->

    String[] commands = line.split( /\s*;\s*/ )
    tempFile.write( "${ commands.join( '\r\n' )}\r\n" * nTimes )
    print ( '"' + commands[ -1 ] + '",' )

    long t = System.currentTimeMillis()
    tempFilePath.execute().waitFor()
    println (( System.currentTimeMillis() - t ) / nTimes )
}

inputStream.close()
assert tempFile.delete()
