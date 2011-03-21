@echo off

echo Running [%1]
groovy -e "new GroovyShell().evaluate('%1'.toURL().text)"
