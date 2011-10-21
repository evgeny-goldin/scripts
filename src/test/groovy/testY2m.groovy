
@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/repo/' )
@Grab('com.goldin:gcommons:0.5.3.4')
@GrabExclude('commons-net:commons-net')
@GrabExclude('org.codehaus.groovy:groovy-all')
import com.goldin.gcommons.GCommons
import com.goldin.gcommons.beans.ExecOption


File testArchive = new File( '../resources/y2m/jetbrains-issues.zip' ).canonicalFile
File y2m         = new File( '../../main/groovy/y2m.groovy' ).canonicalFile

assert [ testArchive, y2m ].every{ it.file }

GCommons.file().with { GCommons.general().with {

    File tempDir  = tempDirectory()
    File testData = new File( tempDir, 'jetbrains-issues.csv' ).canonicalFile

    try
    {
        unpack( testArchive, tempDir )
        assert testData.file

        runTest( y2m, testData, [],                                                                   'jetbrains-issues-1.txt' )
        runTest( y2m, testData, [ "Issue Id, Subsystem, Type, State" ],                               'jetbrains-issues-2.txt' )
        runTest( y2m, testData, [ "Issue Id, Subsystem, Summary, Description" ],                      'jetbrains-issues-3.txt' )
        runTest( y2m, testData, [ "Issue Id, Type, State, Summary", "Type, State, Summary" ],         'jetbrains-issues-4.txt' )
        runTest( y2m, testData, [ "Issue Id, Type, State, Summary", "Type, State, Summary", 'true' ], 'jetbrains-issues-5.txt' )
    }
    finally
    {
        delete( tempDir )
    }
}}



def runTest( File y2m, File testData, List<String> args, String testResultPath )
{
    def    t          = System.currentTimeMillis()
    File   testResult = new File( "../resources/y2m/$testResultPath" ).canonicalFile
    assert testResult.file

    final PrintStream           out  = System.out
    final ByteArrayOutputStream baos = new ByteArrayOutputStream()

    System.out = new PrintStream( baos, true )
    new GroovyShell().run( y2m, ([ 'http://youtrack.jetbrains.net/', testData.path ] + args ) as List )
    String output = baos.toString( 'UTF-8' )

    if ( output != testResult.getText( 'UTF-8' ))
    {
        File copyResult = new File( testResult.path + '-actual' )
        copyResult.setText( output, 'UTF-8' )
        assert false, "Running $args produced result different from [$testResult], result copied to [$copyResult]"
    }

    System.out = out
    println "$testResult - Ok, [${ System.currentTimeMillis() - t }] ms"
}