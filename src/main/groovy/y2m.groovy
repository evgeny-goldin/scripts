@Grab( 'net.sf.opencsv:opencsv:2.3' )
import au.com.bytecode.opencsv.CSVReader

/**
 * Groovy script converting YouTrack CVS export results into MediaWiki tables.
 * Usage: groovy y2m.groovy <YouTrack URL> <CSV file> [<Fields>] [<Group-By-Field>]
 */

List<String> defaultFields        = [ 'Issue Id', 'Type', 'Summary' ]
List<String> defaultGroupByFields = [ 'Type', 'Summary' ]

if ( args.length < 2 )
{
    System.err.println """
------------------------------------------------------------------------------------------------------------------------------
Usage: groovy y2m.groovy <YouTrack URL> <CSV file> [<Fields>] [<Group-By-Fields>]
------------------------------------------------------------------------------------------------------------------------------
YouTrack URL    - Base URL of YouTrack application, like "http://youtrack.jetbrains.net" or "http://evgeny-goldin.org/youtrack"
CSV file        - "Issues in CSV"-exported file from YouTrack
Fields          - (optional) comma-separated list of fields to use in MediaWiki table, "${ defaultFields.join( ', ' )}" by default
Group-By-Fields - (optional) comma-separated list of fields to group table rows by, "${ defaultGroupByFields.join( ', ' )}" for the default fields
------------------------------------------------------------------------------------------------------------------------------
"""
    System.exit( 1 )
}

final String       youTrackUrl   = args[ 0 ].replaceFirst( /(\\|\/)*$/, '' )
final File         f             = new File( args[ 1 ] ).canonicalFile
final List<String> fields        = ( args.size() > 2          ) ? args[ 2 ].split( ',' )*.trim().grep() : defaultFields
final List<String> groupByFields = ( args.size() > 3          ) ? args[ 3 ].split( ',' )*.trim().grep() :
                                   ( fields.is( defaultFields ))? defaultGroupByFields                  :
                                                                  []
assert youTrackUrl && f.file && fields
assert ( ! groupByFields ) || fields.containsAll( groupByFields ), "Fields $fields don't contain $groupByFields"

List<String[]> lines  = new CSVReader( new StringReader( convertMultilines( f.text ))).readAll()
assert         lines?.size() > 1 , "No CSV data found in [$f]"

// https://sourceforge.net/tracker/?func=detail&aid=3425997&group_id=148905&atid=773541
lines = lines.findAll{ it.size() == lines[ 0 ].size() }
lines.each{ String[] line -> assert ( line.size() == lines[ 0 ].size()) }

/**
 * Mapping of fields to their corresponding indices in each String[]:  "Issue Id" => 0, "Project" => 1, "Tags" => 2, etc.
 */
Map<String, Integer> fieldsMapped = lines[ 0 ].inject( [:] ){ Map m, String field -> m[ field ] = m.size(); m }
fieldsMapped.keySet().with {
    assert containsAll( fields ), "CSV file [$f] contains $delegate fields, but doesn't contain ${ fields - intersect( fields )} fields"
}

lines = reorderLines( lines[ 1 .. -1 ], groupByFields, fieldsMapped )
assert lines && lines.every{ it.size() == lines[ 0 ].size() }

/**
 * MediaWiki table template
 */
String template = '''
{| border="1" cellspacing="0" cellpadding="5" class="wikitable" width="90%"
|-<% for ( field in fields ){ %>
| $field<% } %>
|-<% for ( line in lines ){ for ( field in fields ){
    String fieldValue = line[ fieldsMapped[ field ]].with {
        ( field == 'Issue Id' ) ? "[$youTrackUrl/issue/$delegate $delegate]" : delegate
    }
%>
| $fieldValue<% } %>
|-<% } %>
|}'''

println new groovy.text.GStringTemplateEngine().
        createTemplate( template ).
        make([ youTrackUrl  : youTrackUrl,
               fields       : fields,
               fieldsMapped : fieldsMapped,
               lines        : lines ])

/**
 * Re-orders lines specified by the fields provided.
 *
 * @param lines         lines to group
 * @param groupByFields fields to group the lines by, lines are not grouped if empty
 * @param fieldsMapped  mapping of fields to their indices in each String[] line
 * @return              lines re-ordered by the groupByFields
 */
List<String[]> reorderLines ( List<String[]> lines, List<String> groupByFields, Map<String, Integer> fieldsMapped )
{
    assert lines && fieldsMapped

    groupByFields ? lines.sort {
                        String[] line1, String[] line2 ->

                        for ( field in groupByFields )
                        {
                            String fieldValue1 = line1[ fieldsMapped[ field ]]
                            String fieldValue2 = line2[ fieldsMapped[ field ]]

                            if ( fieldValue1 != fieldValue2 )
                            {
                                return fieldValue1.compareTo( fieldValue2 )
                            }
                        }

                        0
                    } :
                    lines
}


/**
 *
 * @param s
 * @return
 */
String convertMultilines ( String s )
{
    s
}