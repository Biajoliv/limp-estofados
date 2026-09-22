@echo off
REM Sobe um servidor HTTP estatico para a pasta Frontend/, necessario porque
REM abrir index.html via file:// quebra as chamadas fetch() por CORS.
cd /d "%~dp0"
echo Servindo Frontend/ em http://localhost:5500 (Ctrl+C para parar)
python -m http.server 5500
