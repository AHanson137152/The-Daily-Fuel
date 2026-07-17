@echo off
setlocal
cd /d "%~dp0"
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac --module-path lib --add-modules javafx.controls,javafx.fxml -d out @sources.txt
if errorlevel 1 (
    del sources.txt
    pause
    exit /b 1
)
if not exist out\com\dailyfuel\ui mkdir out\com\dailyfuel\ui
copy /y src\com\dailyfuel\ui\styles.css out\com\dailyfuel\ui\styles.css >nul
del sources.txt
echo Compilation complete. Class files are in the out folder.
pause

