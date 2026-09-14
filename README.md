# 🍰 Pastelería Las Melosas — Arquitectura de Microservicios

Sistema integral de gestión para la pastelería **"Las Melosas"**, desarrollado bajo una arquitectura de microservicios con **Spring Boot 3.4**, **Spring Cloud 2024**, **Java 21**, **Docker** y una interfaz web con **Thymeleaf** estilizada mediante **Bootstrap 5**.

---

## 📐 Modelo de Vistas de Arquitectura 4+1 (Kruchten)

El modelo **4+1** describe la arquitectura del software mediante 5 perspectivas complementarias que satisfacen las necesidades de los distintos interesados (usuarios, desarrolladores, integradores e ingenieros de sistemas).

> [!NOTE]
> En todos los diagramas se incorpora el microservicio planificado **`ms-auth` / `ms-user`** (gestión de usuarios, roles y autenticación JWT), proyectado para ser integrado en la siguiente fase del proyecto.

```mermaid
graph TD
    subgraph "Modelo 4+1 de Kruchten"
        UC["🎯 +1: Escenarios (Casos de Uso)"]
        LV["🧠 Vista Lógica"]
        PV["⚡ Vista de Procesos"]
        DV["📦 Vista de Desarrollo"]
        PhV["🖥️ Vista Física / Despliegue"]
        
        UC --> LV
        UC --> PV
        UC --> DV
        UC --> PhV
    end
```

---

### 1. 🎯 Vista de Escenarios (+1: Casos de Uso)
Representa los requerimientos funcionales del sistema desde la perspectiva de los usuarios finales (Administrador, Vendedor/Cajero y Cliente).

```mermaid
graph LR
    subgraph Actores
        Admin["👤 Administrador"]
        Vendedor["👤 Vendedor / Cajero"]
        Cliente["👥 Cliente"]
    end

    subgraph "Casos de Uso del Sistema"
        UC_Auth["🔐 Iniciar Sesión / Gestionar Usuarios (ms-auth)"]
        UC_Compra["📥 Registrar Compra de Pasteles / Proveedores (ms-compra)"]
        UC_Cat["🏷️ Registrar Pastel en Catálogo (ms-catalogo)"]
        UC_Inv["📦 Asignar y Controlar Stock (ms-inventario)"]
        UC_Venta["🛒 Realizar Venta y Descontar Stock (ms-venta)"]
        UC_Estadisticas["📊 Consultar Ventas Diarias/Mensuales y Ranking (ms-venta)"]
        UC_Pago["💳 Procesar Pago: Efectivo, Transf., Crédito o Fiado (ms-pago)"]
        UC_Fiados["📋 Consultar y Liquidar Fiados Pendientes (ms-pago)"]
    end

    Admin --> UC_Auth
    Admin --> UC_Compra
    Admin --> UC_Cat
    Admin --> UC_Inv
    Admin --> UC_Estadisticas
    Admin --> UC_Fiados

    Vendedor --> UC_Auth
    Vendedor --> UC_Venta
    Vendedor --> UC_Pago
    Vendedor --> UC_Fiados

    Cliente -.->|Solicita pedido / Fiado| UC_Venta
```

---

### 2. 🧠 Vista Lógica (Logical View)
Describe la estructura de clases, entidades de dominio y sus relaciones conceptuales. Muestra cómo se organiza la información dentro de cada microservicio.

```mermaid
classDiagram
    %% ms-auth / user (Planificado)
    class Usuario {
        +Long id
        +String username
        +String email
        +String passwordHash
        +RolUsuario rol
        +Boolean activo
    }

    %% ms-compra
    class Compra {
        +Long id
        +String proveedorNombre
        +String proveedorContacto
        +LocalDateTime fechaCompra
        +BigDecimal costoTransporte
        +BigDecimal totalProductos
        +BigDecimal totalCompra
        +BigDecimal gananciaObtenida
        +EstadoCompra estadoCompra
    }

    class DetalleCompra {
        +Long id
        +String nombreProducto
        +Integer cantidad
        +BigDecimal precioUnitario
        +BigDecimal subtotal
    }

    %% ms-catalogo
    class Pastel {
        +Long id
        +String codigoPastel
        +String nombrePastel
        +String categoria
        +BigDecimal precioPastel
        +BigDecimal precioVenta
        +BigDecimal precioCosto
        +Integer pesoPastel
        +Integer diasVencimiento
        +Long compraId
    }

    %% ms-inventario
    class InventarioItem {
        +Long id
        +String codigoPastel
        +String nombrePastel
        +Integer stock
        +Integer ventaCosto
        +Long compraId
    }

    class MovimientoInventario {
        +Long id
        +String codigoPastel
        +TipoMovimiento tipoMovimiento
        +Integer cantidad
        +String motivo
        +LocalDateTime fechaMovimiento
    }

    %% ms-venta
    class Venta {
        +Long id
        +LocalDateTime fechaVenta
        +String calle
        +String ciudad
        +String clienteNombre
        +BigDecimal totalVenta
        +EstadoVenta estadoVenta
    }

    class DetalleVenta {
        +Long id
        +String codigoPastel
        +String nombrePastel
        +Integer cantidad
        +BigDecimal precioUnitario
        +BigDecimal subtotal
        +Boolean vendidoACosto
    }

    %% ms-pago
    class Pago {
        +Long id
        +Long ventaId
        +String clienteNombre
        +BigDecimal monto
        +MetodoPago metodoPago
        +EstadoPago estadoPago
        +LocalDateTime fechaPago
        +LocalDateTime fechaVencimiento
        +String numeroComprobante
    }

    %% Relaciones
    Compra "1" *-- "many" DetalleCompra : contiene
    Pastel ..> Compra : originado por (compraId)
    InventarioItem ..> Pastel : vinculado por (codigoPastel)
    MovimientoInventario ..> InventarioItem : auditoría por (codigoPastel)
    Venta "1" *-- "many" DetalleVenta : contiene
    DetalleVenta ..> InventarioItem : descuenta stock (codigoPastel)
    Pago ..> Venta : cancela / referencia (ventaId)
    Usuario ..> Venta : registrado por
    Usuario ..> Compra : gestionado por
```

---

### 3. ⚡ Vista de Procesos (Process View)
Describe la concurrencia, comunicación entre servicios y el flujo dinámico de ejecución en tiempo real mediante llamadas REST internas (OpenFeign) y enrutamiento por el Gateway.

#### Flujo Principal: Autenticación, Venta, Descuento de Stock y Pago

```mermaid
sequenceDiagram
    autonumber
    actor Usuario as 👤 Vendedor / Admin
    participant GW as 🚪 Gateway Service (8080)
    participant Auth as 🔐 ms-auth (8086 - Proyectado)
    participant Venta as 🛒 ms-venta (8083)
    participant Cat as 🏷️ ms-catalogo (8081)
    participant Inv as 📦 ms-inventario (8082)
    participant Pago as 💳 ms-pago (8084)

    Note over Usuario, Auth: 1. Autenticación previa
    Usuario->>GW: POST /api/v1/auth/login
    GW->>Auth: Validar credenciales
    Auth-->>Usuario: Token JWT + Info Usuario

    Note over Usuario, Venta: 2. Registro de Venta
    Usuario->>GW: POST /web/ventas/guardar (con Token/Sesión)
    GW->>Venta: Crear venta con lista de pasteles
    
    loop Por cada pastel en el pedido
        Venta->>Cat: GET /api/v1/catalogo/codigo/{codigoPastel} (OpenFeign)
        Cat-->>Venta: Precios (Venta/Costo) y Días Vencimiento
        
        Venta->>Inv: GET /api/v1/inventario/codigo/{codigoPastel}/stock (OpenFeign)
        Inv-->>Venta: Stock disponible
        
        alt Stock suficiente
            Venta->>Inv: PUT /descontar o /descontar-costo (OpenFeign)
            Inv-->>Venta: Stock actualizado y movimiento registrado
        else Stock insuficiente
            Venta-->>Usuario: Error: Stock insuficiente para pastel
        end
    end

    Venta-->>Usuario: Venta registrada exitosamente (ID generado)

    Note over Usuario, Pago: 3. Registro y Control de Pago
    Usuario->>GW: POST /web/pagos/guardar (ventaId, método, monto)
    GW->>Pago: Registrar Pago
    alt Método = FIADO
        Pago->>Pago: Estado = PENDIENTE + Fecha de Vencimiento
    else Método = Efectivo / Tarjeta / Transferencia
        Pago->>Pago: Estado = PAGADO
    end
    Pago-->>Usuario: Comprobante de pago generado
```

---

### 4. 📦 Vista de Desarrollo / Implementación (Development View)
Muestra la organización del código fuente en subsistemas, módulos y paquetes siguiendo la arquitectura por capas **MVC limpia** exigida para cada microservicio.

```mermaid
graph TD
    subgraph "Organización de Módulos (Maven Multi-Project)"
        ERK["eureka-server (Service Discovery)"]
        GTW["gateway-service (API Gateway & Routing)"]
        AUTH["ms-auth-user (Seguridad & Roles - Planificado)"]
        COMP["ms-compra (Compras a Proveedores)"]
        CAT["ms-catalogo (Catálogo y Vencimiento)"]
        INV["ms-inventario (Bodega y Kardex)"]
        VENT["ms-venta (Ventas, Lugares y Estadísticas)"]
        PAGO["ms-pago (Pagos, Comprobantes y Fiados)"]
    end

    subgraph "Estructura Estándar de Capas en cada Microservicio"
        direction TB
        PK_CFG["config (OpenAPI / Swagger, Security)"]
        PK_ENUM["enums (Constantes y Estados)"]
        PK_MODEL["model (Entidades JPA con atributos puros)"]
        PK_REPO["repository (Spring Data JPA Repositories)"]
        PK_DTO["dto (Data Transfer Objects con Validaciones)"]
        PK_MAP["mapper (Conversión Entity <-> DTO)"]
        PK_SRV["service + service.impl (Lógica de Negocio)"]
        PK_CLT["client (Clientes OpenFeign inter-servicio)"]
        PK_EXP["exception (Excepciones de Negocio y Global Handler)"]
        PK_CTRL["controller (REST API)"]
        PK_WEB["controller.web (Controladores Web Thymeleaf)"]
        PK_TMP["resources/templates (Vistas HTML con Bootstrap 5)"]
    end

    COMP -.-> PK_MODEL
    CAT -.-> PK_MODEL
    INV -.-> PK_MODEL
    VENT -.-> PK_MODEL
    PAGO -.-> PK_MODEL
    AUTH -.-> PK_MODEL
```

---

### 5. 🖥️ Vista Física / Despliegue (Physical / Deployment View)
Ilustra la infraestructura física y de contenedores Docker, la red virtual compartida (`pasteleria-net`), los puertos expuestos en el Host y la orquestación mediante Docker Compose.

```mermaid
graph TB
    subgraph "Host del Sistema (localhost / Servidor Cloud)"
        subgraph "Red Docker Bridge: pasteleria-net"
            
            subgraph "Infraestructura Base"
                D_EUK["eureka-server\nPuerto Host: 8761\nPuerto Interno: 8761"]
                D_GTW["gateway-service\nPuerto Host: 8080\nPuerto Interno: 8080"]
            end

            subgraph "Capa de Microservicios de Negocio"
                D_AUTH["ms-auth-user (Proyectado)\nPuerto Host: 8086\nPuerto Interno: 8086"]
                D_CAT["ms-catalogo\nPuerto Host: 8081\nPuerto Interno: 8081"]
                D_INV["ms-inventario\nPuerto Host: 8082\nPuerto Interno: 8082"]
                D_VENT["ms-venta\nPuerto Host: 8083\nPuerto Interno: 8083"]
                D_PAGO["ms-pago\nPuerto Host: 8084\nPuerto Interno: 8084"]
                D_COMP["ms-compra\nPuerto Host: 8085\nPuerto Interno: 8085"]
            end

            subgraph "Capa de Persistencia"
                DB_LOCAL["Bases de Datos H2 In-Memory\n(Configurables a PostgreSQL vía ENV)"]
            end
        end
    end

    %% Enrutamiento y dependencias
    D_GTW -->|Enruta tráfico| D_AUTH
    D_GTW -->|Enruta tráfico| D_CAT
    D_GTW -->|Enruta tráfico| D_INV
    D_GTW -->|Enruta tráfico| D_VENT
    D_GTW -->|Enruta tráfico| D_PAGO
    D_GTW -->|Enruta tráfico| D_COMP

    D_GTW -.->|Registrado en| D_EUK
    D_AUTH -.->|Registrado en| D_EUK
    D_CAT -.->|Registrado en| D_EUK
    D_INV -.->|Registrado en| D_EUK
    D_VENT -.->|Registrado en| D_EUK
    D_PAGO -.->|Registrado en| D_EUK
    D_COMP -.->|Registrado en| D_EUK

    D_CAT --> DB_LOCAL
    D_INV --> DB_LOCAL
    D_VENT --> DB_LOCAL
    D_PAGO --> DB_LOCAL
    D_COMP --> DB_LOCAL
    D_AUTH --> DB_LOCAL
```

---

## 🚀 Guía de Puesta en Marcha

### Opción 1: Con Docker Compose (Recomendado)

Para compilar las imágenes limpias desde cero y levantar todos los contenedores:

```powershell
# 1. Asegúrate de tener Docker Desktop abierto
# 2. Reconstruye sin caché y levanta
docker compose build --no-cache && docker compose up
```

Para detener los servicios:
```powershell
docker compose down
```

---

### Opción 2: Ejecución Local en Desarrollo (Maven)

1. **Paso 1: Levantar Eureka Server (obligatorio primero)**
   ```powershell
   cd eureka-server; .\mvnw spring-boot:run
   ```

2. **Paso 2: Levantar los microservicios necesarios (en terminales separadas)**
   ```powershell
   cd ms-compra;     .\mvnw spring-boot:run
   cd ms-catalogo;   .\mvnw spring-boot:run
   cd ms-inventario; .\mvnw spring-boot:run
   cd ms-venta;      .\mvnw spring-boot:run
   cd ms-pago;       .\mvnw spring-boot:run
   cd gateway-service; .\mvnw spring-boot:run
   ```

---

## 🌐 Puertos y Rutas de Acceso Web

| Microservicio | Puerto | URL Web (Thymeleaf + Bootstrap) | Swagger / OpenAPI |
|---|:---:|---|---|
| **Eureka Server** | `8761` | `http://localhost:8761` | Dashboard Eureka |
| **API Gateway** | `8080` | `http://localhost:8080` | Enrutador Central |
| **ms-compra** | `8085` | `http://localhost:8085/web/compras` | `http://localhost:8085/swagger-ui.html` |
| **ms-catalogo** | `8081` | `http://localhost:8081/web/catalogo` | `http://localhost:8081/swagger-ui.html` |
| **ms-inventario** | `8082` | `http://localhost:8082/web/inventario` | `http://localhost:8082/swagger-ui.html` |
| **ms-venta** | `8083` | `http://localhost:8083/web/ventas` | `http://localhost:8083/swagger-ui.html` |
| **ms-pago** | `8084` | `http://localhost:8084/web/pagos` | `http://localhost:8084/swagger-ui.html` |
| *ms-auth-user* | `8086` | *(Planificado)* | *(Planificado)* |