cd target
rd /S /Q fakeroot
rd /S /Q jpackage-dest
mkdir fakeroot
mkdir jpackage-dest
copy robomazeblast*.jar fakeroot\
jpackage --name "Robo Maze Blast" --type app-image --input fakeroot --dest jpackage-dest --main-jar robomazeblast-*.jar
jpackage --name "Robo Maze Blast" --type exe --input fakeroot --dest . --main-jar robomazeblast-*.jar --win-menu