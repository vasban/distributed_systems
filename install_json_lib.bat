echo Installing JSON parser library
git clone https://github.com/stleary/JSON-java.git
move "./JSON-java/src/main/java/org" "src/main/java"
del "./JSON-java/" /s /q /f /a
rmdir "./JSON-java/" /s /q
cd "./src/main/java/"
javac "./org/json/*.java"
cd "consoleapp/"
jar cf json-java.jar ../org/json/*.class