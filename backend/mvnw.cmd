@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM Apache Maven Wrapper startup batch script, version 3.2.0

@echo off
setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties

@REM Read distributionUrl
for /f "tokens=1,* delims==" %%a in ('findstr "^distributionUrl=" "%MAVEN_WRAPPER_PROPERTIES%"') do set distributionUrl=%%b

if "%distributionUrl%"=="" (
    echo Error: Could not read distributionUrl from %MAVEN_WRAPPER_PROPERTIES%
    exit /b 1
)

@REM Set Maven home
if "%MAVEN_USER_HOME%"=="" set MAVEN_USER_HOME=%USERPROFILE%\.m2
set WRAPPER_DIR=%MAVEN_USER_HOME%\wrapper\dists

@REM Extract version
for /f "tokens=*" %%v in ('echo %distributionUrl% ^| findstr /r "[0-9]\.[0-9]\.[0-9]"') do set mavenVersion=%%v
set mavenVersion=3.8.8
set MAVEN_HOME=%WRAPPER_DIR%\apache-maven-%mavenVersion%

if not exist "%MAVEN_HOME%" (
    echo Downloading Maven %mavenVersion%...
    mkdir "%WRAPPER_DIR%" 2>nul
    set DOWNLOAD_FILE=%WRAPPER_DIR%\apache-maven-%mavenVersion%-bin.zip
    
    powershell -Command "Invoke-WebRequest -Uri '%distributionUrl%' -OutFile '%WRAPPER_DIR%\apache-maven-%mavenVersion%-bin.zip'"
    
    if errorlevel 1 (
        echo Error: Failed to download Maven
        exit /b 1
    )
    
    echo Extracting Maven...
    powershell -Command "Expand-Archive -Path '%WRAPPER_DIR%\apache-maven-%mavenVersion%-bin.zip' -DestinationPath '%WRAPPER_DIR%' -Force"
    del "%WRAPPER_DIR%\apache-maven-%mavenVersion%-bin.zip"
    
    echo Maven %mavenVersion% installed to %MAVEN_HOME%
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
