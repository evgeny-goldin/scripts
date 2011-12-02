
@GrabResolver( name='com.goldin', root='http://evgeny-goldin.org/artifactory/repo/' )
@Grab('com.goldin:gcommons:0.5.3.6')
@GrabExclude('xml-apis:xml-apis')
import com.goldin.gcommons.GCommons

class Test
{
    static void main ( String ... args )
    {
        println "Test"
    }
}
