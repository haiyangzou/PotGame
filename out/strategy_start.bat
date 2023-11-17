title Login
set MAIN_CLASS=-jar pot-strategy-1.0-SNAPSHOT.war
set JVM_OPTIONS=-server -Xms256M -Xmx256M
set SYS_OPTIONS=-DdebugMode=true
java %JVM_OPTIONS% %SYS_OPTIONS% %MAIN_CLASS%
pause