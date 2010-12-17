
/**
 * Reads commands to execute from stdin, outputs their average run time to stdout
 */

final def nTimes       = 10
final def inputStream  = args.length ? new File( args[ 0 ] ).newInputStream() : System.in
final def tempFile     = File.createTempFile( "timer-temp", ".bat" )
final def tempFilePath = tempFile.absolutePath


/**
 * Executes each line
 */
inputStream.readLines( 'UTF-8' ).findAll{ it }.each
{
    String line ->

    String[] commands = line.split( /\s*;\s*/ )

    tempFile.write( commands.join( "\r\n" ) )
    nTimes.times { tempFilePath.execute() }

    long t = System.currentTimeMillis()
    nTimes.times { tempFilePath.execute() }
    println "\"${ commands[ -1 ] }\",${( System.currentTimeMillis() - t ) / ( nTimes )}"
}

inputStream.close()
assert tempFile.delete()
