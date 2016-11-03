@echo off
REM # 
REM # Shell script to run the Bean tester
REM #
REM # Usage runnit [ path to jdk root ]
REM #

REM setlocal
set USE_JAVA=c:\jdk1.3
if not "%1" == "" set USE_JAVA=%1

@echo ## Running BeanTest with %USE_JAVA%

%USE_JAVA%\bin\java -cp beanbuilder.jar;%USE_JAVA%\lib\dt.jar beantest.BeanTest
