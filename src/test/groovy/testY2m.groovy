
@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/repo/' )
@Grab('com.goldin:gcommons:0.5.3.4')
@GrabExclude('commons-net:commons-net')
@GrabExclude('org.codehaus.groovy:groovy-all')
import com.goldin.gcommons.GCommons
import com.goldin.gcommons.beans.ExecOption

final File   testArchive = new File( '../resources/y2m/jetbrains/issues.zip' ).canonicalFile
final File   y2m         = new File( '../../main/groovy/y2m.groovy' ).canonicalFile

assert [ testArchive, y2m ].every{ it.file }

GCommons.file().with { GCommons.general().with {

    File tempDir  = tempDirectory()
    File testData = new File( tempDir, 'jetbrains-issues.csv' ).canonicalFile

    try
    {
        unpack( testArchive, tempDir )
        assert testData.file

        runJetBrainsTest( y2m, testData, [],                                                                   'table-1.txt' )
        runJetBrainsTest( y2m, testData, [ "Issue Id, Subsystem, Type, State" ],                               'table-2.txt' )
        runJetBrainsTest( y2m, testData, [ "Issue Id, Subsystem, Summary, Description" ],                      'table-3.txt' )
        runJetBrainsTest( y2m, testData, [ "Issue Id, Type, State, Summary", "Type, State, Summary" ],         'table-4.txt' )
        runJetBrainsTest( y2m, testData, [ "Issue Id, Type, State, Summary", "Type, State, Summary", 'true' ], 'table-5.txt' )
    }
    finally
    {
        delete( tempDir )
    }
}}



def runJetBrainsTest ( File y2m, File testData, List<String> args, String testResultPath )
{
    final String encoding   = 'UTF-8'
    final long   t          = System.currentTimeMillis()
    final File   testResult = new File( "../resources/y2m/jetbrains/$testResultPath" ).canonicalFile
    assert testResult.file

    File y2mFile = new File( testData.path + ".result.txt" )
    System.setProperty( 'y2mFile', y2mFile.canonicalPath )
    new GroovyShell().run( y2m, ([ 'http://youtrack.jetbrains.net/', testData.path ] + args ) as List )

    String output         = y2mFile.getText( encoding )
    String expectedOutput = testResult.getText( encoding )

    if ( output != expectedOutput )
    {
        File copyResult = new File( testResult.path + '.result.txt' )
        assert y2mFile.renameTo( copyResult )
        assert false, "Running $args produced result different from [$testResult], result copied to [$copyResult]"
    }

    println "$testResult - Ok, [${ System.currentTimeMillis() - t }] ms"
}