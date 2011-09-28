
def publishers = 'Oreilly Packtpub No.Starch Pragmatic FT.Press Sams.Teach.Yourself'.tokenize()
def months     = 'May Jul Aug'.tokenize()

new File( '.' ).listFiles().findAll { it.file && it.name.endsWith( '.pdf' ) }.
                            each    {
                                File f ->
                                def newName = f.name.
                                              replaceAll  ( /\d{4}/, ''    ).
                                              replaceFirst( /pdf$/,  ''    ).
                                              replaceAll  (( publishers + months ).collect{ /\Q$it\E/ }.join( '|' ), '' ).
                                              replace     ( '.',    ' '    ).
                                              trim()

                                def newFile = new File( "${newName}.pdf" )

                                if ( newFile.file )
                                {
                                    println "! [$newFile.name] already exists, [$f.name] is not renamed"
                                }    
                                else
                                {
                                    assert   f.renameTo ( newFile )
                                    println "[$f.name] => [$newFile.name]"
                                }

                            }

