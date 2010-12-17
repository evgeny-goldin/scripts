
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

    print "\"${ commands[ -1 ] }\","

    tempFile.write( commands.join( "\r\n" ) )
    2.times { tempFilePath.execute() }

    nTimes.times { 
        long t = System.currentTimeMillis()
        tempFilePath.execute()
        print "${( System.currentTimeMillis() - t )},"
    }
    
    println ''
}

inputStream.close()
assert tempFile.delete()
