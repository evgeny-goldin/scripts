@Grab( 'net.sf.opencsv:opencsv:2.3' )
import au.com.bytecode.opencsv.CSVReader

assert args.length == 2, "Usage: groovy YT2MW.groovy <YouTrack URL> <CSV file>"
String youTrackUrl = args[ 0 ]
File   f           = new File( args[ 1 ] ).canonicalFile
assert youTrackUrl && f.file

CSVReader      reader = new CSVReader( new FileReader( f ))
List<String[]> lines  = reader.readAll()
assert         lines?.size() > 1 , "No data found in [$f]"

Map<String, Integer> headers = [:]
lines[ 0 ].eachWithIndex { String header, int index -> headers[ header ] = index }

assert headers.keySet().containsAll( [ 'Issue Id', 'Type', 'Summary' ] ), \
       "CSV file should contain data for 'Issue Id', 'Type', and 'Summary' fields."

String template = '''
{| border="1" cellspacing="0" cellpadding="5" class="wikitable" width="90%"
|-
! width="8%"  | Issue #
! width="8%"  | Type
! width="54%" | Summary<%
for ( line in lines ) {
    String id      = line[ headers[ 'Issue Id' ]]
    String type    = line[ headers[ 'Type'     ]]
    String summary = line[ headers[ 'Summary'  ]]
 %>
|-
| align="center" | [<%= baseUrl %>/issue/$id $id]
| align="center" | $type
| $summary<% } %>
|}'''

println new groovy.text.GStringTemplateEngine().
        createTemplate( template ).
        make([ baseUrl : youTrackUrl,
               lines   : lines[ 1 .. -1 ],
               headers : headers ])


