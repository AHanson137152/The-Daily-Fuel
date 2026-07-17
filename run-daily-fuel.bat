@echo off
setlocal
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo Java compiler not found. Install JDK 22 or newer and reopen this folder.
    pause
    exit /b 1
)

if not exist out mkdir out
dir /s /b src\*.java > sources.txt

echo Compiling Daily Fuel...
javac --module-path lib --add-modules javafx.controls,javafx.fxml -d out @sources.txt
if errorlevel 1 (
    del sources.txt
    echo.
    echo Compilation failed. Review the errors above.
    pause
    exit /b 1
)

if not exist out\com\dailyfuel\ui mkdir out\com\dailyfuel\ui
copy /y src\com\dailyfuel\ui\styles.css out\com\dailyfuel\ui\styles.css >nul
del sources.txt

echo Starting Daily Fuel...
java -Djava.library.path=bin --module-path lib --add-modules javafx.controls,javafx.fxml -cp out com.dailyfuel.main.Main
if errorlevel 1 pause
