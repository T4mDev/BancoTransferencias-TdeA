# Prompts utilizados

Prompts empleados en Cursor para construir la aplicación, siguiendo la misma secuencia del laboratorio (reglas → configuración → entidades → repositorios → servicios → controladores → CORS → frontend → documentación).

## 1. Proyecto base (Spring Initializr)

Configuración: Maven · Java · Spring Boot 4.0.x · Group `com.tdea` · Artifact `banktransfer` · Jar · Properties · Java 21.
Dependencias: Spring Web, Spring Boot DevTools, Spring Data JPA, PostgreSQL Driver, SpringDoc OpenAPI, Validation.

## 2. Reglas de Cursor

```
Crea un archivo de reglas para cursor (mdc) llamado 'springboot' que siempre se aplique,
con las buenas practicas de Java 25 en el desarrollo de un servicio en spring boot para
que las aplique en el desarrollo del proyecto.
Ten en cuenta que los nombres de clases, métodos y atributos deben ser en ingles.
Cada método público debe tener su documentación Javadoc actualizada.
Para los DTO usamos java records.
El dinero se maneja siempre con BigDecimal.
Agrega la estructura actual del proyecto dentro de las reglas.
```

## 3. application.properties

```
Crea la configuración para la conexión a la base de datos postgresql de nombre 'banktdea'
con el usuario 'postgres' y contraseña 'root', usando variables de entorno con esos valores
por defecto. Habilita la actualización del esquema automático en hibernate, el dialecto JPA
e imprime las sentencias sql de JPA en consola. Agrega una propiedad para el origen CORS
del frontend (http://localhost:3000).
```

## 4. Entidades

```
Crea dos entidades JPA para un sistema bancario.

Account (tabla accounts):
- id: Long, clave primaria autogenerada
- accountNumber: String único (6 a 20 dígitos)
- ownerName: String
- balance: BigDecimal (precision 19, scale 2)
- createdAt: LocalDateTime asignado en @PrePersist
- version: Long con @Version
- métodos debit(amount) y credit(amount)

Transaction (tabla transactions), que funciona como histórico:
- id: Long autogenerado
- reference: String UUID único generado en @PrePersist
- sourceAccount y targetAccount: números de cuenta como String
- amount: BigDecimal
- description: String opcional
- status: enum TransactionStatus con valores COMPLETED y REJECTED
- failureReason: String con el motivo cuando es rechazada
- createdAt: LocalDateTime

Generar getters, setters y constructores. Usa las anotaciones JPA correctas.
```

## 5. Repositorios

```
Crea los repositorios Spring Data JPA para Account y Transaction.

AccountRepository:
- buscar por número de cuenta
- buscar por número de cuenta con bloqueo PESSIMISTIC_WRITE para las transferencias
- verificar si un número de cuenta ya existe

TransactionRepository:
- listar todas las transacciones de la más reciente a la más antigua
- listar las transacciones donde una cuenta es origen o destino, ordenadas igual
- buscar por referencia
```

## 6. Excepciones y manejo de errores

```
Crea una jerarquía sealed de excepciones de negocio que extienda RuntimeException y que
cada una defina su HttpStatus:
- AccountNotFoundException (404)
- DuplicateAccountException (409)
- InsufficientFundsException (422)
- InvalidTransferException (400)
Los mensajes deben estar en español.
Crea un @RestControllerAdvice que devuelva ProblemDetail (RFC 9457) para estas excepciones
y para los errores de validación de @Valid, incluyendo un timestamp.
```

## 7. DTOs

```
Crea los DTOs como java records con validaciones Jakarta y mensajes en español:
- AccountRequest(accountNumber, ownerName, initialBalance)
- AccountResponse con un método estático from(Account)
- TransferRequest(sourceAccount, targetAccount, amount > 0, description opcional)
- TransactionResponse con from(Transaction)
- TransferResponse(transaction, qrCode como data URI PNG, qrContent)
```

## 8. Servicio de QR

```
Crea un servicio QrCodeService usando la librería ZXing que:
- construya un texto con los datos básicos de la transacción (referencia, cuenta origen,
  cuenta destino, monto en pesos colombianos, fecha y estado)
- genere la imagen PNG del QR de 300x300 con codificación UTF-8
- devuelva la imagen como bytes y como data URI en base64
Agrega las dependencias de ZXing al pom.xml.
```

## 9. Capa Service

```
Crea las clases AccountService y TransferService con la lógica de negocio.

AccountService: listar cuentas, consultar por número y crear cuenta (sin números repetidos).

TransferService:
- Transferir dinero solo si: las dos cuentas existen, son distintas, el monto es mayor que
  cero y la cuenta origen tiene saldo suficiente.
- Hacer el débito, el crédito y el registro de la transacción en una sola transacción de
  base de datos, bloqueando las cuentas siempre en el mismo orden para evitar deadlocks.
- Si una regla falla, hacer rollback, guardar el intento como REJECTED con su motivo en una
  transacción aparte y relanzar la excepción.
- Devolver la transacción con su código QR.
- Consultar el histórico, con filtro opcional por número de cuenta.
- Buscar una transacción por referencia.
```

## 10. Capa Controller

```
Crea los controladores REST:

AccountController (/api/cuentas):
- GET /api/cuentas → listar cuentas
- GET /api/cuentas/{accountNumber} → consultar una cuenta
- POST /api/cuentas → crear cuenta

TransferController (/api):
- POST /api/transferencias → realizar transferencia (devuelve transacción + QR)
- GET /api/transacciones?cuenta= → histórico con filtro opcional
- GET /api/transacciones/{reference}/qr → imagen PNG del QR

Devuelve códigos de estado HTTP adecuados en cada caso y documenta los endpoints con
anotaciones de OpenAPI.
```

## 11. Datos de ejemplo

```
Crea un CommandLineRunner que, si la tabla de cuentas está vacía, inserte cuatro cuentas de
ejemplo con nombres y saldos en pesos colombianos. Que se pueda desactivar con la
propiedad app.seed-data.
```

## 12. Pruebas

```
Crea pruebas de integración con @SpringBootTest usando H2 en memoria que verifiquen:
- una transferencia exitosa mueve el dinero y devuelve el QR
- saldo insuficiente se rechaza, no cambia el saldo y queda en el histórico como REJECTED
- misma cuenta y cuenta inexistente se rechazan
```

## 13. Filtro CORS

```
Crea una clase Configuration para crear un filtro de CORS que se aplicará a todas las rutas
(/**). Le indica a Spring Boot que debe aceptar peticiones del origen configurado en
app.cors.allowed-origin (http://localhost:3000) y permite todos los métodos y cabeceras
necesarios para la comunicación.
```

## 14. Servicio del frontend

```
Usando @.cursor/rules/springboot.mdc como regla, crea un proyecto ReactJS con Vite en el
puerto 3000 y un servicio llamado bankService.

Debe:
- Usar AXIOS
- Tener métodos para:
  - Obtener todas las cuentas
  - Crear una cuenta
  - Realizar una transferencia
  - Obtener el histórico de transacciones (con filtro opcional por cuenta)
  - Obtener la URL del QR de una transacción
- Usar una variable de entorno (VITE_API_URL) para la URL del backend
- Extraer el mensaje de error del ProblemDetail del backend

Realiza las configuraciones adecuadas para que todo quede en funcionamiento.
```

## 15. Vistas del frontend

```
Crea la interfaz en ReactJS para el banco:

1. Panel de cuentas: tarjetas con titular, número y saldo; al hacer clic se usa como cuenta
   origen. Incluye un formulario plegable para abrir una cuenta.
2. Formulario de transferencia con los campos: cuenta origen, cuenta destino (lista de
   selección), monto y descripción opcional. Todos obligatorios excepto la descripción.
   Muestra el saldo disponible y avisa si el monto lo supera.
3. Comprobante: al completar la transferencia muestra el monto, las cuentas, la fecha, la
   referencia y el código QR, con opción para descargarlo.
4. Tabla del histórico de transacciones con filtro por cuenta, estado (completada /
   rechazada con su motivo) y un botón para ver el QR de cada transacción en un modal.
5. Si ocurre un error al transferir, muestra un componente tipo toast con el mensaje.

Debe verse bien en celular.
```

## 16. Documentación

```
Genera un Readme.md completo y detallado que incluya el stack tecnológico empleado en el
backend y el frontend, la arquitectura usada, los endpoints, cómo ejecutarlo y capturas.
Súbelo después al repo de GitHub.
```
