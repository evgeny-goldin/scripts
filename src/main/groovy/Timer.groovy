
/**
 * Reads commands to execute from stdin, outputs their average run time to stdout
 */

final def nTimes       = ( args ? args[ 0 ] as int : 10 )
final def inputStream  = System.in
final def tempFile     = File.createTempFile( "timer-temp", ".bat" )
final def tempFilePath = tempFile.absolutePath

/**
 * Executes temp batch file
 */
def exec = { def p = tempFilePath.execute(); p.waitFor(); p.destroy() }

/**
 * Executes each line
 */
inputStream.readLines( 'UTF-8' ).findAll{ it }.each
{
    String line ->

    String[] commands = line.split( /\s*;\s*/ )

    print "\"${ commands[ -1 ] }\","

    tempFile.write( commands.join( "\r\n" ) )
    2.times { exec() }

    nTimes.times { 
        long t = System.currentTimeMillis()
        exec()
        print "${( System.currentTimeMillis() - t )},"
    }
    
    println ''
}

inputStream.close()
assert tempFile.delete()
