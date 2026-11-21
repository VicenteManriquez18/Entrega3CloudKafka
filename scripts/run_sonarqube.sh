#!/bin/bash
# =============================================================================
# EJECUCIÓN Y ESCANEO CON SONARQUBE - COLEGIO BO
# =============================================================================
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"
cd "$DIR"

echo "======================================================================"
echo "    INICIANDO ANÁLISIS DE CALIDAD Y SEGURIDAD CON SONARQUBE          "
echo "======================================================================"

echo "[1/3] Levantando contenedor de SonarQube..."
docker compose -f Encargo-Colegio-BO-Backend-main/docker-compose.yml up -d sonarqube sonar-db

echo "[2/3] Esperando a que el servicio SonarQube esté listo en http://localhost:9000..."
echo "      (Esto puede demorar entre 30 y 60 segundos en el primer inicio)"

# Bucle de espera
until curl -s http://localhost:9000/api/system/status | grep -q '"status":"UP"'; do
    sleep 5
    echo "      Esperando inicio de SonarQube..."
done

echo ""
echo "[+] SonarQube está activo en http://localhost:9000"
echo "    Credenciales por defecto: admin / admin"
echo ""

echo "[3/3] Disparando análisis estático SAST sobre el proyecto..."
if command -v sonar-scanner &> /dev/null; then
    sonar-scanner
elif command -v mvn &> /dev/null; then
    cd Encargo-Colegio-BO-Backend-main
    mvn sonar:sonar \
      -Dsonar.projectKey=colegio-bo-platform \
      -Dsonar.host.url=http://localhost:9000 \
      -Dsonar.login=admin \
      -Dsonar.password=admin
else
    echo "Para ejecutar el scanner desde Docker:"
    echo "docker run --rm --network=colegio-network -v \"\$(pwd):/usr/src\" sonarsource/sonar-scanner-cli"
fi

echo ""
echo "======================================================================"
echo "  Análisis completado. Abra http://localhost:9000 para ver el informe  "
echo "  Tome captura del Quality Gate, Vulnerabilities y Security Hotspots  "
echo "======================================================================"

