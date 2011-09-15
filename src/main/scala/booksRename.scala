import java.io.File

val publishers:List[String] = "Oreilly Packtpub No.Starch Pragmatic".split( " " ).toList
val months:List[String]     = "May Jul Aug".split( " " ).toList

new File( "." ).listFiles().filter  { file => file.isFile && file.getName().endsWith( ".pdf" ) }.
                            foreach { file =>
                                def newName = file.getName().
                                              replaceAll  ( "\\d{4}", ""    ).
                                              replaceFirst( "pdf$",   ""    ).
                                              replaceAll  (( publishers ::: months ).map{ s => "\\Q" + s + "\\E" }.mkString( "|" ), "" ).
                                              replace     ( ".",    " "    ).
                                              trim()

                                def newFile = new File( "%s.pdf".format( newName ))

                                if ( newFile.isFile )
                                {
                                    println ( "! [%s] already exists, [%s] is not renamed".format( newFile.getName, file.getName ))
                                }    
                                else
                                {
                                    assert  ( file.renameTo ( newFile ))
                                    println ( "[%s] => [%s]".format( file.getName, newFile.getName ))
                                }
                            }

