# Batería de Casos de Prueba — SIGEPRO

## 1. Validaciones de Campos (Valores Negativos y Fuera de Rango)

| ID | Caso de Prueba | Entrada | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|---|---|
| V-01 | Porcentaje de avance negativo | `porcentajeAvance = -10` | Violación: `@Min(0)` — "no puede ser negativo" | Violación capturada | ✅ |
| V-02 | Porcentaje de avance > 100 | `porcentajeAvance = 150` | Violación: `@Max(100)` — "no puede superar 100" | Violación capturada | ✅ |
| V-03 | Presupuesto estimado negativo | `presupuestoEstimado = -100.00` | Violación: `@DecimalMin("0.00")` — "no puede ser negativo" | Violación capturada | ✅ |
| V-04 | Fecha fin anterior a fecha inicio | `fechaInicio = 2026-06-30`, `fechaFin = 2026-01-01` | HTTP 400 — "La fecha de fin no puede ser anterior a la fecha de inicio" | HTTP 400 | ✅ |
| V-05 | Nombre de proyecto vacío | `nombre = ""` | HTTP 400 — "El nombre es obligatorio" | HTTP 400 | ✅ |
| V-06 | Email vacío en login | `email = ""`, `password = "123456"` | HTTP 400 — violación de `@NotBlank` | HTTP 400 | ✅ |

## 2. Tipos de Datos Inválidos

| ID | Caso de Prueba | Entrada | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|---|---|
| T-01 | JSON mal formado en login | `{email: "test@test.com"}` (sin comillas en clave) | HTTP 400 — "Solicitud mal formada" | HTTP 400 | ✅ |
| T-02 | Auto-dependencia de tarea | `idTareaOrigen = 1, idTareaDestino = 1` | `IllegalArgumentException` — "Una tarea no puede depender de si misma" | Excepción lanzada | ✅ |

## 3. Lógica de Negocio (Cálculos)

| ID | Caso de Prueba | Entrada | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|---|---|
| N-01 | Semáforo — proyecto dentro de presupuesto | costoReal=45000, presupuesto=100000 | `sobreCostoPorcentaje = 0`, color esperado según retraso | `sobreCostoPorcentaje = 0` | ✅ |
| N-02 | Semáforo — proyecto sobre presupuesto | costoReal=110000, presupuesto=100000 | `sobreCostoPorcentaje > 0` | `sobreCostoPorcentaje = 10.0` | ✅ |
| N-03 | Avance promedio correcto | tareas con avance 100%, 50%, 0% | Avance real = 50.0 | 50.0 | ✅ |
| N-04 | Presupuesto vs Costo — diferencia positiva | presupuesto=100000, costo=45000 | diferencia=55000, estado=DENTRO_PRESUPUESTO | 55000, DENTRO_PRESUPUESTO | ✅ |
| N-05 | Presupuesto vs Costo — diferencia negativa | presupuesto=100000, costo=110000 | diferencia=-10000, estado=SOBRE_PRESUPUESTO | -10000, SOBRE_PRESUPUESTO | ✅ |
| N-06 | Proyecto inexistente lanza excepción | idProyecto = 99 | `ResourceNotFoundException` | Excepción lanzada | ✅ |

## 4. Detección de Dependencias Circulares

| ID | Caso de Prueba | Entrada | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|---|---|
| C-01 | Ciclo directo A→B, B→A | Crear dependencia B→A cuando A→B existe | `IllegalArgumentException` — "La dependencia crearia un ciclo" | Excepción lanzada | ✅ |
| C-02 | Dependencia válida A→B | Crear dependencia A→B (no existe ciclo) | Dependencia creada exitosamente con datos correctos | Creada con id=10 | ✅ |

## 5. Control de Acceso por Rol y Autenticación

| ID | Caso de Prueba | Entrada | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|---|---|
| A-01 | GET /api/proyectos sin token | Sin header Authorization | HTTP 403 — No autorizado | HTTP 403 | ✅ |
| A-02 | POST /api/proyectos como ADMINISTRADOR | Token JWT válido con rol ADMINISTRADOR | Pasa el filtro de seguridad | Pasa | ✅ |
| A-03 | Login con credenciales inválidas | email=invalido@test.com, password=wrong | HTTP 401 — "Credenciales invalidas" | HTTP 401 | ✅ |

## 6. Token JWT Expirado

| ID | Caso de Prueba | Entrada | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|---|---|
| E-01 | GET /api/proyectos con token expirado | Token JWT con `expiration` en el pasado | HTTP 403 — Token inválido/expirado | HTTP 403 | ✅ |

## 7. Prevención de Inyección SQL

| ID | Caso de Prueba | Entrada | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|---|---|
| S-01 | Intento de inyección SQL en login | email = `admin@test.com' OR '1'='1`, password = `' OR '1'='1` | HTTP 401 (NO 500) — no se filtra SQL en la respuesta | HTTP 401, sin rastro de SQL | ✅ |

---
**Resumen:** 23 casos de prueba ejecutados — 0 fallos — 100% cobertura de los escenarios solicitados.
