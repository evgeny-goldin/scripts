
final publishers = '''Addison.Wesley Apress Packt Artima AWP Big.Nerd For.Dummies Leanpub PP FriendsofED FT.Press IBM.Press Manning Microsoft.Press New.Riders No.Starch.Press
                    No.Starch Oreilly OReilly Pactpub Packtpub Peachpit Peachpit.Press Pragmatic Prentice.Hall Prentice.Hall SitePoint Sitepoint QUE Sams Wiley Wrox'''.
                    stripIndent().tokenize()
final months     = 'Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec'.tokenize()
final filter     = ( publishers + months ).collect{ /\Q$it.\E/ }.join( '|' )

new File( '.' ).listFiles().findAll { File f -> f.file && f.name.endsWith('.pdf') }.
                            each    { File f ->
                                def ( body, extension ) = f.name.findAll( /^(.+)\.([^\.]+)$/ ){ it[1,2] }[ 0 ]
                                def newBody =
                                    body.replaceAll  ( /\d{10}/, '' ).
                                         replaceAll  ( /\d{4}/,  '' ).
                                         replaceAll  ( filter,   '' ).
                                         replaceAll  ( /\.+/,    ' ').
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
