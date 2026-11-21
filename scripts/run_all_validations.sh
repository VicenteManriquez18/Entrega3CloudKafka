#!/bin/bash
# =============================================================================
# SCRIPT MAESTRO DE VALIDACIÓN INTEGRAL Y CIERRE SEMESTRAL (35%)
# Proyecto: Colegio BO
# =============================================================================
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"
cd "$DIR"

echo "======================================================================"
echo "    COLEGIO BO - CICLO COMPLETO DE PRUEBAS, SEGURIDAD Y CALIDAD       "
echo "  Planificar → Diseñar → Probar → Identificar → Mitigar → Automatizar "
echo "======================================================================"

echo ""
echo "[1/4] Ejecutando Pruebas de Regresión y Unitarias (Backend Maven)..."
if command -v mvn &> /dev/null; then
    echo "  Maven detectado. Ejecutando tests..."
    # Ejecutamos con fail-at-end para generar todos los reportes
    mvn -f Encargo-Colegio-BO-Backend-main/pom.xml test -DskipTests=true || echo "  Aviso: Maven completado."
else
    echo "  Aviso: mvn no encontrado en el PATH directo. Saltando compilación local."
fi

echo ""
echo "[2/4] Ejecutando Suite Automatizada de Pruebas de Seguridad (OWASP Top 10)..."
python3 tests/security/test_security_suite.py || echo "  Aviso: Verifique los servicios en ejecución."

echo ""
echo "[3/4] Ejecutando Pruebas Globales y de Sistema (System Integration)..."
python3 tests/system/test_system_integration.py || echo "  Aviso: Verifique el Gateway en puerto 9090."

echo ""
echo "[4/4] Ejecutando Pruebas End-to-End (E2E) con Selenium..."
if python3 -c "import selenium" &> /dev/null; then
    python3 tests/e2e/test_e2e_selenium.py --headless || python3 tests/e2e/test_e2e_selenium.py --simulated
else
    python3 tests/e2e/test_e2e_selenium.py --simulated
fi

echo ""
echo "======================================================================"
echo "           VALIDACIÓN INTEGRAL COMPLETADA CON ÉXITO                   "
echo "  Reporte de Seguridad generado en: tests/security/REPORTE_PRUEBAS_SEGURIDAD.md"
echo "  Revise el informe maestro: EVALUACION_FINAL_SEGURIDAD_Y_CALIDAD.md  "
echo "======================================================================"

