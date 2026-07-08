@echo off
echo Building ChatApp...

IF NOT EXIST "lib\mysql-connector-j.jar" (
    echo ERROR: Put mysql-connector-j.jar in the lib\ folder first!
    pause & exit /b 1
)

mkdir out 2>nul

javac -cp "lib\mysql-connector-j.jar" -d out ^
    src\db\DBConnection.java ^
    src\dao\UserDAO.java ^
    src\dao\MessageDAO.java ^
    src\server\Server.java ^
    src\server\ClientHandler.java ^
    src\client\MessageListener.java ^
    src\client\Client.java ^
    src\gui\LoginFrame.java ^
    src\gui\ChatFrame.java

IF %ERRORLEVEL%==0 (echo BUILD SUCCESSFUL!) ELSE (echo BUILD FAILED!)
pause