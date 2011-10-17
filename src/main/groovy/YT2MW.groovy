@Grab( 'net.sf.opencsv:opencsv:2.3' )
import au.com.bytecode.opencsv.CSVReader

def idHeader      = 'Issue Id'
def typeHeader    = 'Type'
def summaryHeader = 'Summary'

assert args.length == 2, "Usage: groovy YT2MW.groovy <YouTrack URL> <CSV file>"
String youTrackUrl = args[ 0 ]
File   f           = new File( args[ 1 ] ).canonicalFile
assert youTrackUrl && f.file

CSVReader      reader = new CSVReader( new FileReader( f ))
List<String[]> lines  = reader.readAll()
assert         lines?.size() > 1 , "No data found in [$f]"

/**
 * Mapping of titles to their corresponding indices in String[], representing each data line.
 */
Map<String, Integer> headers = [:]
lines[ 0 ].eachWithIndex { String header, int index -> headers[ header ] = index }

assert headers.keySet().containsAll( [ idHeader, typeHeader, summaryHeader ] ), \
       "CSV file should contain data for '$idHeader', '$typeHeader', and '$summaryHeader' fields."

/**
 * Mapping of issue type, like 'Feature' or 'Bug', to list of lines.
 * Each String[] in the list represents a single issue of the corresponding type.
 */
Map<String, List<String[]>> linesMap = lines[ 1 .. -1 ].inject( [:].withDefault { [] } ){
    Map m, String[] line ->
    String type = line[ headers[ typeHeader ]]
    m[ type ] << line
    m
}

/**
 * List of lines, created by iterating over sorted issue types.
 */
List<String[]> linesGrouped = (( Set<String> ) linesMap.keySet()).sort().inject( [] ){
    List result, String issueType -> result.addAll( linesMap[ issueType ] )
    result
}

/**
 * MediaWiki table template
 */
String template = '''
{| border="1" cellspacing="0" cellpadding="5" class="wikitable" width="90%"
|-
! width="8%"  | <%= idHeader %>
! width="8%"  | <%= typeHeader %>
! width="54%" | <%= summaryHeader %><%
for ( line in lines ) {
    String id      = line[ headers[ idHeader      ]]
    String type    = line[ headers[ typeHeader    ]]
    String summary = line[ headers[ summaryHeader ]]
 %>
|-
| align="center" | [<%= baseUrl %>/issue/$id $id]
| align="center" | $type
| $summary<% } %>
|}'''

println new groovy.text.GStringTemplateEngine().
        createTemplate( template ).
        make([ idHeader      : idHeader,
               typeHeader    : typeHeader,
               summaryHeader : summaryHeader,
               baseUrl       : youTrackUrl,
               lines         : linesGrouped,
               headers       : headers ])
