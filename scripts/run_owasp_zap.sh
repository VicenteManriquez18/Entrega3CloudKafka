#!/bin/bash
# =============================================================================
# EJECUCIÓN DE PRUEBAS DE SEGURIDAD DAST CON OWASP ZAP
# =============================================================================
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"
cd "$DIR"

echo "======================================================================"
echo "    EJECUTANDO ESCANEO DE SEGURIDAD DINÁMICO (DAST) CON OWASP ZAP     "
echo "======================================================================"
echo "Target: API Gateway (http://localhost:9090)"
echo ""

mkdir -p zap_reports

echo "[*] Ejecutando OWASP ZAP Baseline Scan en contenedor Docker..."
echo "    Comando: docker run --rm -v \"$DIR/zap_reports:/zap/wrk/:rw\" -t zaproxy/zap-stable zap-baseline.py -t http://host.docker.internal:9090 -r zap_report.html -w zap_report.md"

docker run --rm -v "$DIR/zap_reports:/zap/wrk/:rw" --network host -t zaproxy/zap-stable \
    zap-baseline.py -t http://localhost:9090 -r zap_report.html -w zap_report.md || true

echo ""
echo "======================================================================"
echo "  Reporte DAST generado en: zap_reports/zap_report.html               "
echo "  Abra este archivo en su navegador y tome capturas de pantalla de:   "
echo "   1. Resumen de Alertas (Alert Summary: High, Medium, Low, Info)     "
echo "   2. Detalle de Vulnerabilidades Mitigadas (Security Headers, etc.)  "
echo "======================================================================"

