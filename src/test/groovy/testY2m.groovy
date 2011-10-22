
@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/repo/' )
@Grab('com.goldin:gcommons:0.5.3.4')
@GrabExclude('commons-net:commons-net')
@GrabExclude('org.codehaus.groovy:groovy-all')
import com.goldin.gcommons.GCommons
import com.goldin.gcommons.beans.ExecOption


final File[] autoTests        = new File( '../resources/y2m/auto' ).canonicalFile.listFiles()
final File   jetBrainsArchive = new File( '../resources/y2m/jetbrains/issues.zip' ).canonicalFile

assert autoTests && autoTests.every { it.name.endsWith( '.txt' ) } && jetBrainsArchive.file

GCommons.file().with { GCommons.general().with {

    /**
     * Run automatic tests
     */

    autoTests.each { autoTest( it ) }

    /**
     * Run JetBrains tests
     */

    File tempDir  = tempDirectory()
    File testData = new File( tempDir, 'jetbrains-issues.csv' ).canonicalFile

    try
    {
        unpack( jetBrainsArchive, tempDir )
        assert testData.file

        runJetBrainsTest( testData, [],                                                                   'table-1.txt' )
        runJetBrainsTest( testData, [ "Issue Id, Subsystem, Type, State" ],                               'table-2.txt' )
        runJetBrainsTest( testData, [ "Issue Id, Subsystem, Summary, Description" ],                      'table-3.txt' )
        runJetBrainsTest( testData, [ "Issue Id, Type, State, Summary", "Type, State, Summary" ],         'table-4.txt' )
        runJetBrainsTest( testData, [ "Issue Id, Type, State, Summary", "Type, State, Summary", 'true' ], 'table-5.txt' )
    }
    finally
    {
        delete( tempDir )
    }
}}


/**
 *
 * @param f
 */
def autoTest( File f )
{
    assert f.file
    final String encoding = 'UTF-8'
    def ( String csv, String query, String expectedResult ) = f.getText( encoding ).findAll( /a/ ){}
}


/**
 *
 * @param y2m
 * @param testData
 * @param args
 * @param expectedOutputName
 */
def runJetBrainsTest ( File testData, List<String> args, String expectedOutputName )
{
    final String encoding       = 'UTF-8'
    final String output         = y2m(( [ 'http://youtrack.jetbrains.net/', testData.path ] + args ) as List )
    final File   expectedOutput = new File( "../resources/y2m/jetbrains/$expectedOutputName" ).canonicalFile

    if ( output != expectedOutput.getText( encoding ))
    {
        File copyResult = new File( expectedOutput.path + '.actual.txt' )
        copyResult.setText( output, encoding )
        assert false, "Running $args produced result different from [$expectedOutput], copied to [$copyResult]"
    }
}


/**
 * Runs "y2m" script with arguments provided.
 *
 * @param args arguments to run "y2m" with
 * @return script result
 */
String y2m ( List<String> args )
{
    final long   t         = System.currentTimeMillis()
    final String encoding  = 'UTF-8'
    final File   y2mScript = new File( '../../main/groovy/y2m.groovy' ).canonicalFile
    final File   y2mFile   = GCommons.file().tempFile()

    System.setProperty( 'y2mFile', y2mFile.canonicalPath )

    try
    {
        print   "Running $args - "
        new GroovyShell().run( y2mScript, args )
        String result = y2mFile.getText( encoding )
        println "[${ System.currentTimeMillis() - t }] ms"
        return result
    }
    finally
    {
        GCommons.file().delete( y2mFile )
    }
}