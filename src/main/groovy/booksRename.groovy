
def publishers = 'QUE Oreilly Packtpub No.Starch Pragmatic FT.Press Sams.Teach.Yourself Big.Nerd Prentice.Hall'.tokenize()
def months     = 'May Jul Aug'.tokenize()

new File( '.' ).listFiles().findAll { it.file && it.name.endsWith( '.pdf' ) }.
                            each    {
                                File f ->
                                def newName = f.name.
                                              replaceAll  ( /\d{4}/, ''    ).
                                              replaceFirst( /pdf$/,  ''    ).
                                              replaceAll  (( publishers + months ).collect{ /\Q$it\E/ }.join( '|' ), '' ).
                                              replaceAll  ( /\s*(\d+)(th|rd) Edition/, ', $1Ed' ).
                                              replace     ( '.',    ' '    ).
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

