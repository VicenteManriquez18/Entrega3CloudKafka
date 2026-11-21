# 📊 GUION Y ESTRUCTURA DE DIAPOSITIVAS PARA LA DEFENSA ORAL
## Material de Apoyo para la Presentación (Evaluación Parcial Nº 5 - ISY1102)
**Tiempo de Presentación:** 10 Minutos (1 minuto por diapositiva) + 5 Minutos de Preguntas  
**Formato sugerido:** PowerPoint / Google Slides / Canva

---

### Diapositiva 1: Portada Institucional (Minuto 0:00 - 1:00)
* **Título:** Optimización del Aseguramiento de la Calidad y Seguridad del Software
* **Subtítulo:** Validación Integral y Cierre Semestral de la Plataforma Escolar Colegio BO
* **Datos requeridos:**
  * Asignatura: Seguridad y Calidad en el Desarrollo de Software (ISY1102)
  * Integrantes: [Nombres Completos]
  * Carrera: Ingeniería en Informática / Analista Programador
  * Docente: [Nombre del Docente]
  * Institución: Duoc UC - 2026
* **Guion del orador:**
  > *"Buenas tardes profesor y compañeros. Hoy presentamos el cierre semestral y la validación integral de calidad y seguridad para la plataforma Colegio BO, recorriendo el ciclo DevSecOps: planificar, diseñar, probar, identificar hallazgos, mitigar y automatizar."*

---

### Diapositiva 2: Arquitectura del Sistema y Ciclo DevSecOps (Minuto 1:00 - 2:00)
* **Contenido Visual:** Diagrama de arquitectura de microservicios:
  * API Gateway (9090) como punto perimetral único.
  * 6 microservicios de negocio independientes con persistencia en PostgreSQL.
  * Broker de mensajería asíncrona RabbitMQ.
* **Puntos Clave:**
  * Desacoplamiento de servicios y comunicación síncrona/asíncrona.
  * Adopción del enfoque *"Shift-Left Security"* incorporando controles tempranos de calidad.
* **Guion del orador:**
  > *"Colegio BO está diseñado bajo una arquitectura distribuida de microservicios con Spring Cloud Gateway y RabbitMQ. Durante esta etapa aplicamos pruebas en múltiples capas para garantizar que la solución sea no solo funcional, sino resiliente y segura."*

---

### Diapositiva 3: Pruebas Globales o de Sistema & Circuit Breaker (Minuto 2:00 - 3:00)
* **Contenido Visual:** Captura de pantalla de `test_system_integration.py` con los 8 casos en verde `[PASSED]`.
* **Puntos Clave:**
  * Enrutamiento dinámico verificado a través del API Gateway.
  * Tolerancia a fallos: Implementación de **Resilience4j Circuit Breaker**.
  * Mecanismo de Fallback ante caídas de servicios para evitar fallas en cascada.
* **Guion del orador:**
  > *"Nuestras pruebas de sistema validaron el enrutamiento transversal y la resiliencia operativa. Con Resilience4j, si un microservicio experimenta degradación, el Gateway redirige el tráfico a un fallback seguro sin interrumpir la operación del colegio."*

---

### Diapositiva 4: Pruebas End-to-End (E2E) con Selenium (Minuto 3:00 - 4:00)
* **Contenido Visual:** Captura de Selenium IDE ejecutando la suite `colegio_bo_e2e.side` con checks verdes.
* **Puntos Clave:**
  * Validación de flujos web completos en Frontend React + Vite.
  * `TC_E2E_01`: Bloqueo automático ante contraseñas débiles en formulario.
  * `TC_E2E_02` y `03`: Registro exitoso, autenticación JWT y redirección al Dashboard.
  * `TC_E2E_04`: Protección de rutas privadas impidiendo accesos sin token activo.
* **Guion del orador:**
  > *"Automatizamos los flujos críticos con Selenium. Demostramos que las políticas de seguridad se hacen cumplir desde la interfaz web, impidiendo el registro con claves inseguras y bloqueando el acceso a paneles privados sin una sesión válida."*

---

### Diapositiva 5: Ejecución y Reporte de Pruebas de Seguridad (Minuto 4:00 - 5:00)
* **Contenido Visual:** Captura del Dashboard web `REPORTE_PRUEBAS_SEGURIDAD.html` (100% de cumplimiento).
* **Puntos Clave:**
  * Suite automatizada de pruebas basada en **OWASP Top 10**.
  * Evaluación de vectores: Inyección SQL, Cross-Site Scripting (XSS), escalación de roles y fuga de datos.
  * 8 pruebas ejecutadas, 8 aprobadas (0 fallas críticas).
* **Guion del orador:**
  > *"Construimos una suite automatizada en Python para auditar los riesgos de OWASP Top 10. Todos los casos de prueba de inyección y autenticación fueron neutralizados con éxito, generando reportes auditables en HTML y Markdown."*

---

### Diapositiva 6: Matriz de Vulnerabilidades Identificadas (Minuto 5:00 - 6:00)
* **Contenido Visual:** Tabla comparativa de la Matriz de Riesgos inicial:
  * VULN-01: Falta de cabeceras HTTP de seguridad (OWASP A05).
  * VULN-02: Contraseñas débiles en modificación de usuarios (OWASP A07).
  * VULN-03: Fuga de información técnica y stack traces en BD (CWE-209).
  * VULN-04: Inyección de roles no autorizados (OWASP A01).
* **Guion del orador:**
  > *"El diagnóstico inicial reveló hallazgos relevantes: ausencia de cabeceras de protección en el Gateway, riesgo de Clickjacking y fuga de trazas de PostgreSQL ante excepciones. A partir de esto definimos alternativas de mitigación a nivel de código."*

---

### Diapositiva 7: Mitigaciones Técnicas Implementadas en Código (Minuto 6:00 - 7:00)
* **Contenido Visual:** Fragmentos de código de:
  * `SecurityHeadersFilter.java`: `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, `CSP`, `HSTS`.
  * `GlobalExceptionHandler.java`: Captura de errores con `@RestControllerAdvice` (CWE-209).
  * `AuthController.java`: Expresión regular normativa y hashing BCrypt.
* **Guion del orador:**
  > *"Mitigamos cada hallazgo de raíz: blindamos el Gateway con un filtro reactivo global de cabeceras, enmascaramos las excepciones internas para no filtrar nombres de tablas y forzamos contraseñas robustas cifradas con BCrypt."*

---

### Diapositiva 8: Pipeline CI/CD DevSecOps y SonarQube (Minuto 7:00 - 8:00)
* **Contenido Visual:** Diagrama del Pipeline de GitHub Actions (`devsecops-pipeline.yml`):
  * Maven Tests ➔ SonarQube SAST ➔ Trivy SCA ➔ OWASP ZAP DAST ➔ Selenium E2E ➔ Release Gate.
* **Puntos Clave:**
  * Detección estática temprana (SAST) y dinámica (DAST).
  * Quality Gate en SonarQube: Cero vulnerabilidades críticas para autorizar releases.
* **Guion del orador:**
  > *"Integramos un pipeline DevSecOps en GitHub Actions con 6 etapas continuas. Cada cambio de código es auditado automáticamente con SonarQube, Trivy y OWASP ZAP antes de llegar a producción."*

---

### Diapositiva 9: Pruebas de Regresión y Validación de Funcionalidades (Minuto 8:00 - 9:00)
* **Contenido Visual:** Captura del Dashboard de SonarQube con **Quality Gate: PASSED**, **Security: A**, **Vulnerabilities: 0**.
* **Puntos Clave:**
  * Preservación intacta de la mensajería asíncrona mediante RabbitMQ.
  * Consistencia de contratos de API REST y documentación OpenAPI/Swagger.
  * Regresión funcional aprobada al 100%.
* **Guion del orador:**
  > *"Las pruebas de regresión confirmaron que el endurecimiento de seguridad no afectó ninguna funcionalidad previa: la mensajería RabbitMQ entre usuarios y matrícula continúa operando de forma transparente y sin pérdidas."*

---

### Diapositiva 10: Plan de Mantenimiento Continuo y Cumplimiento Legal (Minuto 9:00 - 10:00)
* **Contenido Visual:** Diagrama del plan de mantenimiento en 4 ejes:
  * **Correctivo:** Parches de seguridad con SLA < 24 horas.
  * **Preventivo:** Actualización periódica con Dependabot y Trivy.
  * **Adaptativo:** Alineación con circulares del MINEDUC.
  * **Perfectivo:** Latencia de respuesta en Gateway < 200 ms.
* **Puntos Clave:**
  * Cumplimiento estricto de la **Ley Nº 19.628** (Protección de datos de menores) y **Ley Nº 21.459** (Delitos informáticos).
  * Estándar internacional **ISO/IEC 25010** (Seguridad, Fiabilidad y Mantenibilidad).
* **Guion del orador:**
  > *"Concluimos con un plan de mantenimiento continuo y cumplimiento de las leyes chilenas 19.628 y 21.459. De esta forma garantizamos que la plataforma Colegio BO mantenga su calidad, resiliencia y seguridad a lo largo de todo su ciclo de vida. Muchas gracias, quedamos atentos a sus preguntas."*

