#!/usr/bin/env bash
# Sobe um servidor HTTP estático para a pasta Frontend/, necessário porque
# abrir index.html via file:// quebra as chamadas fetch() por CORS.
set -e
cd "$(dirname "$0")"
echo "Servindo Frontend/ em http://localhost:5500 (Ctrl+C para parar)"
python3 -m http.server 5500 || python -m http.server 5500
