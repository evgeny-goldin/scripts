
@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/repo/' )
@Grab('com.goldin:gcommons:0.5.3.6')
@GrabExclude('commons-net:commons-net')
@GrabExclude('org.codehaus.groovy:groovy-all')
@GrabExclude('xml-apis:xml-apis')
import com.goldin.gcommons.GCommons


GCommons.file().with { GCommons.general().with {

    /**
     * Run automatic tests
     */

    new File( '../resources/y2m/auto' ).canonicalFile.listFiles().sort().with {
        assert delegate && delegate.every { it.name.endsWith( '.txt' ) }
        delegate.each { autoTest( it ) }
    }

    /**
     * Run JetBrains tests
     */

    runJetBrainsTest( [],                                                                   'table-1.txt' )
    runJetBrainsTest( [ 'Issue Id, Subsystem, Type, State' ],                               'table-2.txt' )
    runJetBrainsTest( [ 'Issue Id, Subsystem, Summary, Description' ],                      'table-3.txt' )
    runJetBrainsTest( [ 'Issue Id, Type, State, Summary', 'Type, State, Summary' ],         'table-4.txt' )
    runJetBrainsTest( [ 'Issue Id, Type, State, Summary', 'Type, State, Summary', 'true' ], 'table-5.txt' )
}}


/**
 * Runs automatic test for the file specified.
 *
 * @param f file containing CSV input, command-line arguments and expected output.
 */
void autoTest( File f )
{
    assert f.file

    final String encoding = 'UTF-8'
    final String delim    = '~' * 158

    def ( String csv, String query, String expectedResult ) =
        f.getText( encoding ).findAll( /(?s)^.*$delim(.+?)$delim(.+?)$delim(.+?)$/ ){ it[ 1, 2, 3 ]*.trim() }[ 0 ]
    assert csv && query && expectedResult

    def ( String url, String fields, String groupByFields, String addN ) =
        query.findAll( /(\S+)\s*("[^"]+?")?\s*("[^"]+?")?\s*(true|false)?/ ){ it [ 1 .. 4 ] }[ 0 ]
    assert url

    final File csvFile            = GCommons.file().tempFile()
    final File expectedResultFile = GCommons.file().tempFile()

    csvFile.setText( csv, encoding )
    expectedResultFile.setText( expectedResult, encoding )

    compareResults( f.canonicalPath,
                    [ url, csvFile.canonicalPath, fields, groupByFields, addN ].grep() as List,
                    expectedResultFile )

    GCommons.file().delete( csvFile, expectedResultFile )
}


/**
 * Runs test for JetBrains CSV file.
 *
 * @param args           y2m command line arguments
 * @param expectedResult expected result file name
 */
void runJetBrainsTest ( List<String> args, String expectedResult )
{
    assert ( args != null ) && expectedResult

    File csvFile            = new File( '../resources/y2m/jetbrains/issues.csv'      ).canonicalFile
    File expectedResultFile = new File( "../resources/y2m/jetbrains/$expectedResult" ).canonicalFile
    compareResults( expectedResultFile.path,
                    ( [ 'http://youtrack.jetbrains.net/', csvFile.path ] + args ) as List,
                    expectedResultFile )
}


/**
 * Compares result from running the "y2m" script and expected one.
 *
 * @param title          test title
 * @param args           y2m command line arguments
 * @param expectedResult file containing expected result
 */
void compareResults ( String title, List<String> args, File expectedResult )
{
    assert title && args && expectedResult.file

    final String encoding = 'UTF-8'
    final String result   = y2m( title, args )

    if ( result != expectedResult.getText( encoding ))
    {
        File copyResult = new File( expectedResult.path + '.actual.txt' )
        copyResult.setText( result, encoding )
        assert false, "Running $args produced result different from [$expectedResult], copied to [$copyResult]"
    }
}


/**
 * Runs the "y2m" script using the command-line arguments specified.
 *
 * @param title tests title
 * @param args  y2m command line arguments
 * @return script execution result
 */
String y2m ( String title, List<String> args )
{
    assert title && args

    final long   t         = System.currentTimeMillis()
    final String encoding  = 'UTF-8'
    final File   y2mScript = new File( '../../main/groovy/mediawiki/y2m.groovy' ).canonicalFile
    final File   y2mFile   = GCommons.file().tempFile()

    assert [ y2mScript, y2mFile ].every { it.file }
    System.setProperty( 'y2mFile', y2mFile.canonicalPath )

    try
    {
        print   "Running [$title] - "
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
