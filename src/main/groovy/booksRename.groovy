
def publishers = '''Addison.Wesley Apress Big.Nerd For.Dummies FriendsofED FT.Press IBM.Press Manning No.Starch.Press
                    No.Starch Oreilly Packtpub Peachpit.Press Pragmatic Prentice.Hall Prentice.Hall SitePoint Sitepoint QUE Sams Wiley Wrox'''.
                    stripIndent().tokenize()
def months     = 'Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec'.tokenize()
def extensions = 'pdf epub'.tokenize()

new File( '.' ).listFiles().findAll { File f -> f.file && extensions.any { f.name.endsWith( it ) }}.
                            each    { File f ->
                                def ( body, extension ) = f.name.findAll( /^(.+)\.([^\.]+)$/ ){ it[1,2] }[ 0 ]
                                def newBody =
                                    body.replaceAll  ( /\d{4}/, '' ).
                                         replaceAll  (( publishers + months ).collect{ /\Q$it.\E/ }.join( '|' ), '' ).
                                         replace     ( '.',    ' '    ).
                                         replaceAll  ( /\s*(\d+)(nd|rd|th) Edition/, ', $1Ed' ).
                                         trim()

                                def newFile = new File( f.parentFile, "$newBody.$extension" )

                                if ( newFile.file )
                                {
                                    println "!  [$f.name] is not renamed, [$newFile.name] already exists"
                                }
                                else
                                {
                                    assert   f.renameTo ( newFile )
                                    println "=> [$f.name] renamed to [$newFile.name]"
                                }
                            }

