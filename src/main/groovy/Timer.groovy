
/**
 * Reads commands to execute from stdin, outputs their average run time to stdout
 */

/**
 * Accepts a closure, runs it and returns its runtime (in microseconds).
 */
def measureNano =
{
    long t = System.nanoTime()
    it()
    return ( System.nanoTime() - t ).intdiv( 1000 )
}

final def nTimes     = (( args.length > 0 ) ? args[ 0 ] as int : 10 )
final def env        = System.getenv().collect{ key, value -> "$key=$value" } as String[]
final def currentDir = new File( System.getProperty( "user.dir" ))
final def tempFile   = File.createTempFile( "timer-temp", ".bat" )

System.in.eachLine( 'UTF-8' )
{
    String command ->

    tempFile.write( command )
    Runtime.getRuntime().exec( tempFile.absolutePath, env, currentDir )

    def timeNano  = measureNano { nTimes.times { command.execute().text }}.intdiv( nTimes )
    def timeMicro = timeNano.intdiv( 10.power( 3 ))
    def timeMilli = timeNano.intdiv( 10.power( 6 ))
    println "[$command]: [$timeMicro] microseconds or [$timeMilli] milliseconds"
}

assert tempFile.delete()
