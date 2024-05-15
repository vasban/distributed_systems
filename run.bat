echo Running files
call compile.bat
cd "consoleapp/"
java -cp .;json-java.jar Test