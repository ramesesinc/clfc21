@echo off
set RUN_DIR=%cd%
echo. run directory: %cd%
rem Specify the java home directory 
rem set JAVA_HOME=C:\Program Files\Java\jdk1.6.0_45

rem Specify some java options
set JAVA_OPTS=-Xmx512m

rem build the java command 
set JAVA=java
if not "%JAVA_HOME%" == "" set JAVA=%JAVA_HOME%\bin\java

echo.
echo.========================================================================
echo.
echo   Platform    : Osiris3 Client Platform 
echo   Application : CLFC 2.0
echo.
echo   JAVA_HOME   : %JAVA_HOME%
echo   JAVA_OPTS   : %JAVA_OPTS%
echo   JAVA        : %JAVA%
echo.
echo.========================================================================

%JAVA% %JAVA_OPTS% -cp lib/*;. com.rameses.osiris3.platform.OSBootStrap
pause
