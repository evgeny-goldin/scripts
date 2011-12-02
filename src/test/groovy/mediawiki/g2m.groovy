package mediawiki

@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/repo/' )
@Grab('com.goldin:gcommons:0.5.3.6')
@GrabExclude('xml-apis:xml-apis')
import com.goldin.gcommons.GCommons


/**
 * Tests "g2m.groovy" script
 */
class g2m
{
    private final File projectRoot = new File( System.getProperty( 'user.dir' ), '../../../..' ).canonicalFile
    private final File g2mScript   = new File( projectRoot, 'src/main/groovy/mediawiki/g2m.groovy' )

    g2m()
    {
        assert projectRoot.directory && projectRoot.listFiles()*.name.any{ it == 'build.gradle' }
        assert g2mScript.file
    }


    void test()
    {
        for ( f in new File( projectRoot, 'src/test/resources/g2m/markdown' ).listFiles())
        {
            final expectedResult = new File( projectRoot, "src/test/resources/g2m/mediawiki/${ f.name.replaceFirst( /\.markdown$/, '.mediawiki' ) }" )
            final result         = GCommons.file().delete( new File( expectedResult.parentFile, expectedResult.name + '.out' ))

            new GroovyShell().run( g2mScript, [ f.canonicalPath, result.canonicalPath ] )

            assert expectedResult.file && result.file
            assert expectedResult.getText( 'UTF-8' ) == result.getText( 'UTF-8' ), "[$expectedResult] != [$result]"
            assert result.delete()
        }
    }


    static void main( String ... args )
    {
        new g2m().test()
    }
}
