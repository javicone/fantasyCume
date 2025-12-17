# 🚨 Página de Error Personalizada

## ✅ Implementación completada

Se ha creado una página de error personalizada para la aplicación Liga del Cume.

## 📁 Archivos creados

### 1. **error.html** (`src/main/resources/templates/`)
Página de error con diseño moderno que incluye:

- ✅ **Logo de la liga** - Se muestra el logoliga.png centrado con animación
- ✅ **Icono de error** - Triángulo de advertencia animado
- ✅ **Mensaje de error en grande** - Muestra el parámetro `error` de la URL
- ✅ **Diseño responsive** - Se adapta a móviles y tablets
- ✅ **Botón para volver** - Redirige a la página principal

### 2. **ErrorController.java** (`src/main/java/.../Controller/`)
Controlador que maneja la ruta `/error`

## 🎨 Características del diseño

### Elementos visuales:
- 🎨 **Fondo degradado** - Tonos oscuros consistentes con el tema
- 🖼️ **Logo animado** - Efecto pulse suave
- 🔴 **Icono de error** - Con animación shake
- 📝 **Mensaje destacado** - Texto grande con fondo semitransparente
- 🔵 **Botón llamativo** - Degradado azul con hover effect

### Animaciones:
- ✨ **Entrada suave** - fadeInUp al cargar la página
- 💓 **Logo pulsante** - Efecto de respiración continuo
- 🔔 **Icono shake** - Movimiento de alerta al entrar
- 🎯 **Hover en botón** - Elevación y brillo

## 📝 Uso

### Redirigir con mensaje personalizado:
```java
return "redirect:/error?error=" + URLEncoder.encode("Tu mensaje aquí", "UTF-8");
```

### Ejemplos de uso:

**Desde SecurityConfig:**
```java
response.sendRedirect("/error?error=unauthorized");
```
Muestra: "unauthorized"

**Desde un Controller:**
```java
redirectAttributes.addFlashAttribute("error", "No tienes permisos");
return "redirect:/error?error=No tienes permisos";
```
Muestra: "No tienes permisos"

**URL directa:**
```
http://localhost:8080/error?error=Sesión expirada
```
Muestra: "Sesión expirada"

## 🔍 Ejemplo de visualización

Cuando accedes a `/error?error=No autorizado`, verás:

```
┌────────────────────────────────────────┐
│                                        │
│         [Logo Liga del Cume]           │
│           (animado)                    │
│                                        │
│            ⚠️                          │
│                                        │
│     ¡Oops! Algo salió mal             │
│                                        │
│  ┌──────────────────────────────┐    │
│  │                              │    │
│  │      No autorizado          │    │
│  │                              │    │
│  └──────────────────────────────┘    │
│                                        │
│  Por favor, intenta nuevamente o      │
│  regresa a la página principal.       │
│                                        │
│     [🏠 Volver al Inicio]             │
│                                        │
└────────────────────────────────────────┘
```

## 🎯 Integración con Spring Security

Ya está integrado en `SecurityConfig.java`:

```java
.exceptionHandling(exception -> exception
    .authenticationEntryPoint((request, response, authException) -> {
        response.sendRedirect("/error?error=unauthorized");
    })
)
```

Cuando alguien intenta acceder sin login a `/liga/**`, verá:

**Mensaje mostrado:**
```
unauthorized
```

### Mejorar el mensaje de Spring Security:

Puedes cambiar el redirect en `SecurityConfig.java` a:

```java
response.sendRedirect("/error?error=" + 
    URLEncoder.encode("Debes iniciar sesión para acceder a esta página", "UTF-8"));
```

## 📱 Responsive

La página se adapta automáticamente a diferentes tamaños de pantalla:

- **Desktop**: Logo 150px, texto grande, espaciado amplio
- **Tablet**: Tamaños intermedios
- **Mobile**: Logo 100px, texto ajustado, padding reducido

## 🎨 Colores utilizados

- **Fondo**: Degradado gris oscuro (#0f172a → #1e293b)
- **Contenedor**: Gris semitransparente con blur
- **Error**: Rojo (#ef4444)
- **Botón**: Azul degradado (#3b82f6 → #1e40af)
- **Texto**: Blanco y gris claro

## ✅ Testing

### Test 1: Error sin parámetro
```
URL: http://localhost:8080/error
Resultado: "Ha ocurrido un error inesperado"
```

### Test 2: Error con mensaje personalizado
```
URL: http://localhost:8080/error?error=Prueba de error
Resultado: "Prueba de error" (en grande)
```

### Test 3: Acceso sin login
```
1. Cierra el navegador
2. Intenta acceder: http://localhost:8080/liga/1/ranking
3. Redirige a: http://localhost:8080/error?error=unauthorized
4. Muestra: "unauthorized"
```

### Test 4: Botón volver
```
1. Estando en la página de error
2. Click en "Volver al Inicio"
3. Redirige a: http://localhost:8080/
```

## 🔧 Personalización

### Cambiar el mensaje por defecto:
Edita `error.html` línea 153:
```html
<div class="error-message" th:unless="${param.error}">
    Tu mensaje personalizado aquí
</div>
```

### Agregar más información:
Puedes agregar más parámetros en la URL:
```
/error?error=mensaje&code=404&detail=No encontrado
```

Y mostrarlos en el template:
```html
<div th:if="${param.code}">
    Código: <span th:text="${param.code[0]}"></span>
</div>
```

## 📊 Comparación

| Aspecto | Antes | Ahora |
|---------|-------|-------|
| Página de error | ❌ No existe | ✅ Personalizada |
| Logo | ❌ No | ✅ Sí |
| Mensaje visible | ❌ Genérico | ✅ Personalizado |
| Diseño | ❌ Básico | ✅ Profesional |
| Responsive | ❌ No | ✅ Sí |
| Animaciones | ❌ No | ✅ Sí |

## 🎯 Resultado final

Ahora cuando ocurra un error o alguien intente acceder sin login:
- ✅ Verá una página profesional con el logo de la liga
- ✅ El mensaje de error se mostrará en grande y claro
- ✅ Podrá volver fácilmente a la página principal
- ✅ La experiencia del usuario será mucho mejor

---

**¡Página de error personalizada lista! 🎨✨**

