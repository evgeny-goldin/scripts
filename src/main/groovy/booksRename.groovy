
def publishers = 'Addison.Wesley Apress Big.Nerd FT.Press IBM.Press Manning No.Starch.Press No.Starch Oreilly Packtpub Pragmatic Prentice.Hall Prentice.Hall SitePoint QUE Sams Wiley Wrox'.tokenize()
def months     = 'Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec'.tokenize()

new File( '.' ).listFiles().findAll { it.file && it.name.endsWith( '.pdf' ) }.
                            each    {
                                File f ->
                                def newName = f.name.
                                              replaceAll  ( /20\d\d\./, '' ).
                                              replaceFirst( /pdf$/,     '' ).
                                              replaceAll  (( publishers + months ).collect{ /\Q$it.\E/ }.join( '|' ), '' ).
                                              replace     ( '.',    ' '    ).
                                              replaceAll  ( /\s*(\d+)(nd|rd|th) Edition/, ', $1Ed' ).
                                              trim()

                                def newFile = new File( "${newName}.pdf" )

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

