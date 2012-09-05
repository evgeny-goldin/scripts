
@GrabResolver( name='evgenyg.artifactoryonline.com', root='http://evgenyg.artifactoryonline.com/evgenyg/repo/' )
@Grab('com.github.goldin:gcommons:0.6-SNAPSHOT')
@GrabExclude('xml-apis:xml-apis')
import com.github.goldin.gcommons.GCommons

class Test
{
    static void main ( String ... args )
    {
        println "Test"
    }
}
