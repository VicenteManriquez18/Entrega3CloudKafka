# 🎤 GUÍA DE DEFENSA ORAL Y BANCO DE PREGUNTAS TÍPICAS DEL DOCENTE
## Evaluación Parcial Nº 5: Seguridad y Calidad en el Desarrollo de Software (ISY1102)
**Tiempo de Defensa:** 10 minutos de presentación + 5 minutos de preguntas individuales.

---

### 1. Estructura Cronometrada para los 10 Minutos de Presentación

```mermaid
gantt
    title Cronograma de Defensa Oral (10 Minutos)
    dateFormat  m:s
    axisFormat  %M:%S
    Introducción y Arquitectura (Minuto 0 a 2)      :00:00, 02:00
    Hallazgos OWASP y Mitigaciones (Minuto 2 a 5)    :02:00, 03:00
    Pruebas Automatizadas y E2E (Minuto 5 a 7)       :05:00, 02:00
    DevSecOps, CI/CD y SonarQube (Minuto 7 a 9)      :07:00, 02:00
    Planes de Mantenimiento y Cierre (Minuto 9 a 10) :09:00, 01:00
```

#### Minuto 0:00 - 2:00 | Introducción y Arquitectura Evaluada
* **Qué decir:** *"Buenas tardes profesor. Nuestro proyecto corresponde a la plataforma de gestión escolar Colegio BO, construida sobre una arquitectura distribuida de 7 microservicios en Spring Boot, RabbitMQ para mensajería asíncrona desacoplada y un API Gateway como único punto perimetral. En esta etapa final de cierre semestral nos enfocamos en validar la seguridad y calidad mediante el ciclo DevSecOps: Planificar, Diseñar, Probar, Integrar, Identificar hallazgos, Mitigar y Automatizar."*
* **Lámina / Pantalla de apoyo:** Mostrar el diagrama de arquitectura y el ciclo de vida del informe.

#### Minuto 2:00 - 5:00 | Identificación de Vulnerabilidades y Mitigaciones Implementadas (IE1 - 30%)
* **Qué decir:** *"Al auditar la solución identificamos riesgos críticos de OWASP Top 10: falta de cabeceras de seguridad (A05), fuga de trazas internas de PostgreSQL (CWE-209) y riesgo de escalación de privilegios en el registro (A01). En lugar de dejarlo en teoría, implementamos mitigaciones directas en el código:
  1. Creamos `SecurityHeadersFilter.java` en el Gateway para forzar CSP, HSTS, `X-Frame-Options: DENY` contra Clickjacking y `nosniff`.
  2. Implementamos `GlobalExceptionHandler.java` con `@RestControllerAdvice` para enmascarar los errores de BD, mitigando la fuga de información técnica.
  3. Establecimos una lista blanca (*whitelisting*) de roles autorizados y forzamos contraseñas complejas con hashing BCrypt."*
* **Lámina / Pantalla de apoyo:** Mostrar la tabla comparativa de vulnerabilidades antes vs después y el código Java de los filtros.

#### Minuto 5:00 - 7:00 | Pruebas Automatizadas: Seguridad, Sistema y E2E (IE5 - 24%)
* **Qué decir:** *"Para asegurar que el sistema es resiliente y funcional, automatizamos 3 suites de pruebas:
  1. Una suite automatizada en Python de 8 casos de prueba basados en OWASP Top 10, alcanzando el 100% de cumplimiento.
  2. Pruebas globales de integración que validan el Circuit Breaker Resilience4j: si el servicio de usuarios o matrícula experimenta fallas, el Gateway responde con un fallback controlado sin tumbar el sistema escolar.
  3. Pruebas End-to-End con Selenium que verifican la experiencia de usuario, comprobando que si un usuario ingresa una clave insegura en el formulario de registro, el sistema lo bloquea tanto en frontend como en backend."*
* **Lámina / Pantalla de apoyo:** Mostrar la terminal con los tests en verde `[PASSED]` y el panel de Selenium IDE o el reporte gráfico HTML.

#### Minuto 7:00 - 9:00 | Integración Continua DevSecOps y SonarQube (IE3 - 14%)
* **Qué decir:** *"Incorporamos el enfoque 'Shift-Left' mediante un pipeline de GitHub Actions con 6 etapas: compilación y pruebas de regresión, análisis estático SAST con SonarQube para revisar código limpio y seguro, auditoría SCA de dependencias con Trivy, escaneo dinámico DAST con OWASP ZAP sobre el Gateway en ejecución, pruebas E2E y finalmente el Quality Gate. Esto garantiza el cumplimiento de la Ley 19.628 de protección de datos personales de alumnos y la Ley 21.459 de delitos informáticos."*
* **Lámina / Pantalla de apoyo:** Mostrar el diagrama de pipeline de GitHub Actions y el Dashboard de SonarQube con el Quality Gate en `PASSED`.

#### Minuto 9:00 - 10:00 | Plan de Mantenimiento y Conclusión (IE7 e IE9)
* **Qué decir:** *"Para preservar la calidad del producto en el tiempo, diseñamos un plan de mantenimiento en cuatro ejes: Correctivo (parches en menos de 24 hrs), Preventivo (actualización periódica de dependencias con Dependabot), Adaptativo (frente a nuevas circulares del MINEDUC) y Perfectivo (optimización continua de tiempos de respuesta bajo 200 ms). Con esto aseguramos una solución escolar mantenible, segura y alineada a ISO/IEC 25010."*

---

### 2. Banco de Preguntas Típicas del Docente y Respuestas Modelo (5 Minutos de Preguntas)

#### ❓ Pregunta 1: *"¿Por qué implementaron las cabeceras de seguridad en el API Gateway y no en cada microservicio individual?"*
* **Respuesta Modelo:**
  > *"Aplicamos el principio de defensa perimetral y centralización arquitectónica. Al ser una arquitectura de microservicios, el API Gateway es el único punto de entrada público que se comunica con el navegador del cliente. Inyectar las cabeceras de seguridad (`X-Frame-Options`, `Content-Security-Policy`, `HSTS`, `X-Content-Type-Options`) en el Gateway mediante un `GlobalFilter` reactivo garantiza que **todas** las respuestas salientes hacia el cliente estén blindadas uniformemente, evitando duplicidad de código en los 7 microservicios internos y simplificando el mantenimiento."*

#### ❓ Pregunta 2: *"¿Qué diferencia hay entre el análisis SAST (SonarQube) y el análisis DAST (OWASP ZAP / Burp Suite) que aplicaron?"*
* **Respuesta Modelo:**
  > *"Son enfoques complementarios:
  > * **SAST (SonarQube)** es un análisis de **caja blanca**: examina el código fuente en reposo sin necesidad de compilar o ejecutar la aplicación, detectando defectos sintácticos, malas prácticas, variables sin sanitizar y fallos lógicos (*Code Smells* y vulnerabilidades internas).
  > * **DAST (OWASP ZAP / Burp Suite)** es un análisis de **caja negra**: prueba la aplicación levantada y en funcionamiento interactuando contra los endpoints HTTP del API Gateway (puerto 9090), evaluando cómo responde el sistema frente a inyecciones reales, comprobando cabeceras reales de respuesta y analizando tiempos de respuesta o caídas de servicio."*

#### ❓ Pregunta 3: *"¿Cómo mitigan la fuga de información técnica si la base de datos PostgreSQL se cae o lanza un error de clave foránea?"*
* **Respuesta Modelo:**
  > *"Mitigamos la debilidad **CWE-209** implementando un `GlobalExceptionHandler` con la anotación `@RestControllerAdvice` en Spring Boot. Este interceptor captura específicamente las excepciones de base de datos como `DataIntegrityViolationException` y emite una respuesta JSON estándar y sanitizada con código `409 Conflict` o `400 Bad Request`. Nunca devolvemos el objeto `e.getMessage()` crudo ni el stack trace de la base de datos al cliente, ocultando así la estructura de tablas, dialectos de PostgreSQL y rutas de clases internas."*

#### ❓ Pregunta 4: *"¿Cómo garantiza la solución el cumplimiento de la Ley Nº 19.628 de Protección de Datos Personales en Chile?"*
* **Respuesta Modelo:**
  > *"La Ley Nº 19.628 exige el resguardo y confidencialidad estricta de datos personales y sensibles, especialmente tratándose de fichas escolares, asistencia y calificaciones de menores de edad. Nuestro sistema garantiza esto mediante cuatro controles:
  > 1. Almacenamiento seguro de contraseñas mediante hashing unidireccional con **BCrypt** y factor de costo criptográfico.
  > 2. Uso obligatorio de **HSTS** y canales seguros para la transmisión de tokens JWT.
  > 3. Almacenamiento de tokens en el frontend dentro de `sessionStorage` (que se destruye al cerrar la pestaña), evitando la persistencia insegura en discos o `localStorage`.
  > 4. Enmascaramiento de errores de base de datos para impedir que un usuario malicioso infiera identificadores o datos personales de otros alumnos mediante técnicas de inyección o enumeración."*

#### ❓ Pregunta 5: *"¿Qué rol cumple el Circuit Breaker de Resilience4j en la disponibilidad y seguridad del sistema?"*
* **Respuesta Modelo:**
  > *"El Circuit Breaker previene los fallos en cascada (*cascading failures*) y los ataques de denegación de servicio internos. Si un microservicio (por ejemplo, el de reportes o mensajería) se satura o su base de datos se desconecta, el Circuit Breaker abre el circuito tras sobrepasar el umbral de 50% de fallos en una ventana deslizante de 10 peticiones. En lugar de dejar al usuario colgado esperando un timeout, redirige de inmediato la llamada al endpoint de fallback interno (`/fallback/...`), retornando una respuesta rápida y controlada, permitiendo que el resto del colegio continúe operando normalmente."*

---

### 3. Check-list Rápido antes de Entrar a la Sala / Conexión

- [ ] Archivo [`INFORME_FINAL_ISY1102_DUOC_UC.md`](file:///home/hater/Downloads/CloudRabbitMQ-main/INFORME_FINAL_ISY1102_DUOC_UC.md) descargado o convertido a Word/PDF con la portada completada (integrantes, carrera, sección, fecha).
- [ ] Terminal lista en la carpeta del proyecto para ejecutar demostración en vivo si el docente lo pide:
  ```bash
  python3 scripts/mock_test_server.py &
  sleep 1
  python3 tests/security/test_security_suite.py
  ```
- [ ] Archivo [`tests/security/REPORTE_PRUEBAS_SEGURIDAD.html`](file:///home/hater/Downloads/CloudRabbitMQ-main/tests/security/REPORTE_PRUEBAS_SEGURIDAD.html) abierto en una pestaña del navegador listo para proyectar.
- [ ] Archivo [`.github/workflows/devsecops-pipeline.yml`](file:///home/hater/Downloads/CloudRabbitMQ-main/.github/workflows/devsecops-pipeline.yml) abierto en el editor para mostrar la integración CI/CD.

