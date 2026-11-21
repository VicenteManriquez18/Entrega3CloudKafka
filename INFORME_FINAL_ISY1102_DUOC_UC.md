# Evaluacion Parcial 5 - Seguridad y Calidad de Software
Asignatura: ISY1102
Integrantes: [Nombres]
Docente: [Nombre docente]
Fecha: Noviembre 2026

1. Pruebas globales o de sistema
Para probar el sistema completo se verifico que el API Gateway en el puerto 9090 se comunique bien con los microservicios de usuarios, asistencia, matricula, academico y reportes. Tambien se probo el circuit breaker para que si un servicio falla, el sistema no se caiga completo y devuelva una respuesta de respaldo.

Comando ejecutado:
python3 tests/system/test_system_integration.py

[Captura 1: Consola mostrando las pruebas SYS-01 a SYS-08 pasadas]

2. Pruebas End-to-End (E2E)
Se realizaron pruebas de punta a punta simulando las acciones de un usuario en el navegador web con Selenium:
- Se probo que el formulario no deje registrar contraseñas cortas o simples.
- Se probo el registro exitoso con una clave que cumpla los requisitos.
- Se probo el login para obtener el token JWT y entrar al dashboard.
- Se probo que si alguien intenta entrar a una ruta protegida como /admin sin iniciar sesion, el sistema lo devuelva al login.

Comando ejecutado:
python3 tests/e2e/test_e2e_selenium.py --simulated

[Captura 2: Consola o Selenium IDE mostrando las pruebas E2E pasadas]

3. Ejecucion y documentacion de pruebas de seguridad
Se ejecuto un script de pruebas de seguridad para verificar vulnerabilidades comunes:
- Se revisaron las cabeceras HTTP de seguridad (X-Frame-Options, X-Content-Type-Options, CSP).
- Se probo que el sistema rechace contraseñas debiles en el registro y en la actualizacion.
- Se enviaron intentos de inyeccion SQL en el login para verificar que no permitan acceso indebido.
- Se enviaron scripts XSS en los campos de texto para confirmar que no se ejecuten.
- Se probo que no se puedan asignar roles no permitidos.
- Se reviso que los mensajes de error no muestren informacion de la base de datos PostgreSQL.

Comando ejecutado:
python3 tests/security/test_security_suite.py

[Captura 3: Consola mostrando las pruebas SEC-01 a SEC-06 pasadas]

4. Identificacion y analisis de vulnerabilidades
En la revision inicial del proyecto se encontraron estos problemas:
- Al Gateway le faltaban cabeceras de seguridad como X-Frame-Options, lo que permitia riesgo de clickjacking.
- En la actualizacion de usuarios se podia poner una contraseña corta sin que el sistema la rechazara.
- Cuando la base de datos fallaba, salian mensajes con nombres de tablas y errores de PostgreSQL.
- En el registro se podia enviar cualquier texto en el rol del usuario.
- Faltaba configurar CORS de forma centralizada en el Gateway.

[Captura 4: Tabla o lista de vulnerabilidades encontradas]

5. Definicion de alternativas de mitigacion
Para solucionar los problemas encontrados se hicieron estos cambios en el codigo:
- Se agrego la clase SecurityHeadersFilter en el apigateway para poner cabeceras X-Frame-Options: DENY, nosniff, CSP y HSTS.
- Se agrego GlobalExceptionHandler con RestControllerAdvice en servicio-usuarios para capturar los errores de base de datos y devolver un mensaje generico sin mostrar detalles tecnicos.
- Se agrego validacion de contraseña obligatoria con minimo 8 caracteres, mayuscula, minuscula, numero y caracter especial tanto al registrar como al actualizar usuarios.
- Se creo una lista de roles permitidos (Admin, Profesor, Alumno, Apoderado) para que no se puedan inventar roles.
- Las contraseñas se guardan encriptadas con BCrypt.

[Captura 5: Codigo de SecurityHeadersFilter y GlobalExceptionHandler]

6. Automatizacion de pruebas funcionales y normativas
En la pantalla de registro (front/src/Signup.jsx) se pusieron validaciones que revisan la contraseña en tiempo real mientras el usuario escribe: minimo 8 caracteres, mayuscula, minuscula, numero y simbolo. Ademas se dejaron scripts en Python para correr todas las pruebas de forma automatica en cualquier momento.

[Captura 6: Formulario de registro mostrando las reglas de la contraseña]

7. Incorporacion de conceptos asociados a CI/CD y seguridad dentro del ciclo de desarrollo
Se configuro un archivo de pipeline en .github/workflows/devsecops-pipeline.yml usando GitHub Actions con los pasos necesarios para revisar el codigo de forma continua:
- Compilacion del backend y ejecucion de pruebas unitarias con Maven.
- Analisis de codigo estatico con SonarQube para buscar fallos.
- Revision de dependencias con Trivy y npm audit para buscar vulnerabilidades conocidas.
- Escaneo de seguridad con OWASP ZAP contra el Gateway.
- Pruebas E2E con Selenium.

[Captura 7: Archivo devsecops-pipeline.yml abierto en el editor]

8. Pruebas de regresion y validacion de funcionalidades previamente evaluadas
Despues de poner los filtros y las medidas de seguridad, se probo que las funciones anteriores sigan operando bien:
- La mensajeria entre microservicios con RabbitMQ sigue enviando y recibiendo mensajes sin fallas.
- Los servicios de asistencia, notas y cursos siguen respondiendo normalmente a traves del Gateway.
- La documentacion de Swagger sigue visible en /swagger-ui.html.

[Captura 8: Consola con pruebas de sistema pasando o panel de SonarQube]

9. Recomendaciones para preservar la calidad, seguridad y cumplimiento
- Ley 19.628 (Proteccion de datos personales): Cuidar los datos de los alumnos y apoderados usando siempre contraseñas con BCrypt, tokens temporales que se borren al cerrar el navegador y no guardar datos personales en los logs.
- Ley 21.459 (Delitos informaticos): Mantener el Gateway con circuit breaker y controles de acceso para evitar ingresos no autorizados o ataques que boten el sistema.
- Mantenimiento: Aplicar parches cuando salgan fallas, actualizar dependencias de Maven y Node cada cierto tiempo, y ajustar el sistema si el MINEDUC cambia alguna norma escolar.
