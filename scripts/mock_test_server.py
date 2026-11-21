#!/usr/bin/env python3
"""
Servidor Mock de Pruebas y Validación (Simulador de Microservicios Colegio BO)
Permite validar y simular la suite de seguridad, pruebas de sistema y E2E
en cualquier entorno (puerto 9090).
"""

from http.server import HTTPServer, BaseHTTPRequestHandler
import json
import re

USERS_DB = [
    {"id": 1, "correo": "admin@bernardo.cl", "rol": "Admin", "telefono": "+56911112222"},
    {"id": 2, "correo": "profesor@bernardo.cl", "rol": "Profesor", "telefono": "+56933334444"},
    {"id": 3, "correo": "alumno@bernardo.cl", "rol": "Alumno", "telefono": "+56955556666"}
]

class MockGatewayHandler(BaseHTTPRequestHandler):
    def send_security_headers(self):
        self.send_header("X-Frame-Options", "DENY")
        self.send_header("X-Content-Type-Options", "nosniff")
        self.send_header("X-XSS-Protection", "1; mode=block")
        self.send_header("Strict-Transport-Security", "max-age=31536000; includeSubDomains")
        self.send_header("Content-Security-Policy", "default-src 'self'")
        self.send_header("Referrer-Policy", "strict-origin-when-cross-origin")
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Authorization, Content-Type")

    def do_OPTIONS(self):
        self.send_response(200)
        self.send_security_headers()
        self.end_headers()

    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_security_headers()
        self.end_headers()

        if self.path == "/api/gateway/health":
            resp = {"status": "UP", "service": "API Gateway", "security": "ACTIVE"}
        elif self.path.startswith("/api/usuarios"):
            resp = USERS_DB
        elif self.path == "/fallback/usuarios":
            resp = {"status": "CIRCUIT_BREAKER_ACTIVE", "fallback": True}
        else:
            resp = {"status": "OK", "endpoint": self.path}

        self.wfile.write(json.dumps(resp).encode("utf-8"))

    def do_POST(self):
        content_length = int(self.headers.get("Content-Length", 0))
        post_data = self.rfile.read(content_length)
        data = {}
        try:
            data = json.loads(post_data.decode("utf-8"))
        except:
            pass

        if self.path == "/api/auth/register":
            pwd = data.get("password", "")
            correo = data.get("correo", "")
            rol = data.get("rol", "Alumno")

            # Check email format
            if not re.match(r"^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$", correo):
                self.send_response(400)
                self.send_security_headers()
                self.send_header("Content-Type", "application/json")
                self.end_headers()
                self.wfile.write(json.dumps({"error": "Formato de correo electrónico inválido."}).encode("utf-8"))
                return

            # Check allowed roles
            if rol not in ["Admin", "Profesor", "Alumno", "Apoderado"]:
                self.send_response(400)
                self.send_security_headers()
                self.send_header("Content-Type", "application/json")
                self.end_headers()
                self.wfile.write(json.dumps({"error": "Rol no permitido."}).encode("utf-8"))
                return

            # Check password complexity
            is_valid_pwd = (
                len(pwd) >= 8 and
                any(c.isupper() for c in pwd) and
                any(c.islower() for c in pwd) and
                any(c.isdigit() for c in pwd) and
                any(not c.isalnum() for c in pwd)
            )

            if not is_valid_pwd:
                self.send_response(400)
                self.send_security_headers()
                self.send_header("Content-Type", "application/json")
                self.end_headers()
                self.wfile.write(json.dumps({"error": "La contraseña debe contener al menos una mayúscula, minúscula, número y especial."}).encode("utf-8"))
                return

            # Success
            new_user = {"id": len(USERS_DB) + 1, "correo": correo, "rol": rol}
            USERS_DB.append(new_user)
            self.send_response(201)
            self.send_security_headers()
            self.send_header("Content-Type", "application/json")
            self.end_headers()
            self.wfile.write(json.dumps({"mensaje": "Usuario registrado con éxito", "id": new_user["id"]}).encode("utf-8"))

        elif self.path == "/api/auth/login":
            correo = data.get("correo", "")
            pwd = data.get("password", "")

            # SQLi or invalid credentials
            if "'" in correo or "--" in correo or ";" in correo or "UNION" in correo.upper():
                self.send_response(401)
                self.send_security_headers()
                self.send_header("Content-Type", "application/json")
                self.end_headers()
                self.wfile.write(json.dumps({"error": "Credenciales inválidas"}).encode("utf-8"))
                return

            if correo == "admin@bernardo.cl" and pwd == "Admin123#":
                self.send_response(200)
                self.send_security_headers()
                self.send_header("Content-Type", "application/json")
                self.end_headers()
                self.wfile.write(json.dumps({"token": "mock.jwt.token", "rol": "Admin"}).encode("utf-8"))
            else:
                self.send_response(401)
                self.send_security_headers()
                self.send_header("Content-Type", "application/json")
                self.end_headers()
                self.wfile.write(json.dumps({"error": "Credenciales inválidas"}).encode("utf-8"))
        else:
            self.send_response(200)
            self.send_security_headers()
            self.end_headers()

def run(port=9090):
    server_address = ('', port)
    httpd = HTTPServer(server_address, MockGatewayHandler)
    print(f"Mock Gateway & Security Server corriendo en http://localhost:{port}")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nServidor mock detenido.")

if __name__ == "__main__":
    run()

