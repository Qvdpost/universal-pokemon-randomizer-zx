@echo off
pushd "%~dp0"
cd /d "%~dp0"
java -Xmx4608M -jar PokeRandoZXQ.jar please-use-the-launcher
echo Press any key to exit...
pause >nul