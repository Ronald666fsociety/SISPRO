# Guia de Despliegue — SIGEPRO

## Arquitectura

```
[Usuario] → https://sigepro.vercel.app (Frontend React)
                        ↓ (proxy /api/*)
          https://sigepro-backend.up.railway.app (Spring Boot)
                        ↓
          MySQL en Railway (o Clever Cloud / Aiven)
```

---

## 1. Backend — Spring Boot (Railway)

Railway tiene soporte nativo para Java + MySQL.

### Paso 1: Crear cuenta en https://railway.app

### Paso 2: Subir el backend
```bash
cd sigepro-backend

# Inicializar git (si no existe)
git init
git add .
git commit -m "Initial commit for deployment"

# Crear proyecto en Railway y conectar repositorio
# O usar Railway CLI:
npm i -g @railway/cli
railway login
railway init
railway up
```

### Paso 3: Agregar MySQL en Railway
- En el dashboard de Railway, agregar **MySQL** como servicio
- Railway provee la variable `MYSQL_URL`, `MYSQL_USER`, `MYSQL_PASSWORD`

### Paso 4: Configurar variables de entorno en Railway

| Variable | Valor |
|---|---|
| `MYSQL_HOST` | (provisto por Railway) |
| `MYSQL_PORT` | 3306 |
| `MYSQL_DATABASE` | sigepro |
| `JWT_SECRET` | (generar una clave larga aleatoria) |
| `JWT_EXPIRATION` | 86400000 |
| `SPRING_PROFILES_ACTIVE` | prod |

### Paso 5: Crear `application-prod.properties`

```properties
spring.datasource.url=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?useSSL=true&serverTimezone=UTC
spring.datasource.username=${MYSQL_USER}
spring.datasource.password=${MYSQL_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}
```

### Paso 6: Agregar `system.properties` para Railway

```properties
java.runtime.version=21
```

---

## 2. Frontend — React / Vite (Vercel)

### Paso 1: Conectar a Vercel

```bash
cd sigepro-frontend

# Instalar Vercel CLI
npm i -g vercel

# Login y deploy
vercel login
vercel --prod
```

O desde el dashboard de Vercel:
1. Ir a https://vercel.com/new
2. Importar repositorio de GitHub
3. Framework: **Vite**
4. Build command: `npm run build`
5. Output: `dist`

### Paso 2: Configurar variable de entorno en Vercel

| Variable | Valor |
|---|---|
| `VITE_API_URL` | `https://sigepro-backend.up.railway.app/api` |

(Opcional) Si no usas variable de entorno, editar `vercel.json` y reemplazar la URL del backend en `rewrites`.

### Paso 3: El archivo `vercel.json` ya esta creado

Redirige `/api/*` al backend y maneja SPA routing.

---

## 3. Alternativas para el Backend

| Servicio | Precio | MySQL | Java |
|---|---|---|---|
| **Railway** | Gratis (500h/mes) | Si (integrado) | Si |
| **Render** | Gratis (dormido) | Si (separado) | Si |
| **Fly.io** | Gratis (3 apps) | No (usar Aiven) | Si |
| **Clever Cloud** | Gratis limitado | Si | Si |
| **Koyeb** | Gratis | No | Si |

---

## 4. Comandos Rapidos

```bash
# Backend - build JAR
cd sigepro-backend
.\mvnw.cmd clean package -DskipTests
java -jar target\sigepro-1.0.0.jar --spring.profiles.active=prod

# Frontend - build production
cd sigepro-frontend
npm run build

# Deploy frontend a Vercel
cd sigepro-frontend
vercel --prod

# Deploy backend a Railway
cd sigepro-backend
railway up
```

---

## 5. Post-Deploy

- Ir a `https://sigepro.vercel.app`
- Login con credenciales por defecto (las que genere el DataSeeder)
- Verificar que las llamadas API funcionan
- Revisar los logs en Railway si hay errores
