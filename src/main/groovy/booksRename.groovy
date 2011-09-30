
def publishers = 'Addison.Wesley QUE Prentice.Hall Oreilly Packtpub No.Starch.Press Pragmatic FT.Press Sams Big.Nerd Prentice.Hall Apress IBM.Press'.tokenize()
def months     = 'Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec'.tokenize()

new File( '.' ).listFiles().findAll { it.file && it.name.endsWith( '.pdf' ) }.
                            each    {
                                File f ->
                                def newName = f.name.
                                              replaceAll  ( /\d{4}/, ''    ).
                                              replaceFirst( /pdf$/,  ''    ).
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

