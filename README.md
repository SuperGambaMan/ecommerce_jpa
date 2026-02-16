# Documentación de la API Ecommerce JPA

## Arquitectura y capas

La aplicación sigue una arquitectura por capas:

- **Domain (Dominio):** Entidades JPA que representan las tablas de la base de datos (`Product`, `Category`, `User`, `CartItem`).
- **Repositorio:** Interfaces para acceder a la base de datos usando Spring Data JPA (`ProductRepository`, `CategoryRepository`, etc.).
- **Servicio:** Lógica de negocio y validaciones (`ProductService`, `CartItemService`).
- **Controlador:** Expone los endpoints REST (`ProductController`, `CartItemController`).
- **DTO (Data Transfer Object):** Clases que definen la estructura de los datos enviados/recibidos por la API, separando la representación interna de la forma en que se exponen al cliente (`CartProductDTO`, `CartSummary`).

## Endpoints disponibles

### Productos

- **POST /api/productos**
  - Crea un nuevo producto.
  - Body (JSON):
    ```json
    {
      "name": "string",
      "descrip": "string",
      "image_url": "string",
      "sku": "string",
      "price": decimal,
      "quantity": integer,
      "category": { "id": long }
    }
    ```
  - Respuesta: Producto creado o error estructurado.

- **GET /api/productos**
  - Lista todos los productos.
  - Respuesta: Array de productos.

- **GET /api/productos/{id}**
  - Obtiene un producto por su ID.
  - Respuesta: Producto o error estructurado.

- **PUT /api/productos/{id}**
  - Actualiza un producto existente.
  - Body (JSON): igual que en creación.
  - Respuesta: Producto actualizado o error estructurado.

- **DELETE /api/productos/{id}**
  - Elimina un producto por su ID.
  - Respuesta: 204 No Content o error estructurado.

- **GET /api/productos/search**
  - Busca productos con filtros combinados, paginación y ordenación.
  - Parámetros:
    - `name`: filtra por nombre (opcional)
    - `categoryId`: filtra por id de categoría (opcional)
    - `sku`: filtra por SKU (opcional)
    - `page`: número de página (por defecto 0)
    - `size`: tamaño de página (por defecto 10)
    - `sortBy`: campo de ordenación (`id`, `name`, `price`)
    - `direction`: dirección (`asc`, `desc`)
  - Respuesta: Página de productos o error estructurado si no hay resultados.
  - Ejemplo de error:
    ```json
    {
      "status": 404,
      "error": "Not Found",
      "message": "No se encontró ningún producto",
      "path": "/api/productos/search"
    }
    ```

### Carrito

- **POST /api/carrito/add**
  - Añade un producto al carrito de un usuario.
  - Parámetros:
    - `userId`: ID del usuario
    - `productId`: ID del producto
    - `quantity`: cantidad a añadir (debe ser > 0 y <= stock disponible)
  - Respuesta: JSON con el listado de productos del carrito y el total acumulado.
  - Si la cantidad llega a 0, el producto se elimina del carrito.
  - Ejemplo de respuesta:
    ```json
    {
      "products": [
        { "productId": 1, "name": "Producto 1", "price": 10.5, "quantity": 2 },
        { "productId": 2, "name": "Producto 2", "price": 5.0, "quantity": 1 }
      ],
      "total": 26.0
    }
    ```
  - Ejemplo de error:
    ```json
    {
      "status": 400,
      "error": "Bad Request",
      "message": "No se puede añadir más unidades de las disponibles en stock (stock actual: 5)",
      "path": "/api/carrito/add"
    }
    ```

- **GET /api/carrito/resumen**
  - Obtiene el resumen del carrito de un usuario.
  - Parámetro:
    - `userId`: ID del usuario
  - Respuesta: Igual que en el endpoint de añadir producto al carrito.

## Rutas usadas en la colección de Postman

- `POST http://localhost:8080/api/productos` — Crear producto
- `GET http://localhost:8080/api/productos` — Listar productos
- `GET http://localhost:8080/api/productos/1` — Obtener producto por ID
- `PUT http://localhost:8080/api/productos/1` — Actualizar producto
- `DELETE http://localhost:8080/api/productos/1` — Eliminar producto
- `GET http://localhost:8080/api/productos/search?name=abc&categoryId=2&sku=xyz&page=0&size=10&sortBy=price&direction=asc` — Buscar productos con filtros, paginación y ordenación
- `POST http://localhost:8080/api/carrito/add?userId=1&productId=2&quantity=3` — Añadir producto al carrito
- `GET http://localhost:8080/api/carrito/resumen?userId=1` — Resumen del carrito

## Notas
- Todos los endpoints devuelven errores claros y estructurados si los parámetros son incorrectos o no se encuentra el recurso.
- El endpoint de búsqueda soporta filtros combinados, paginación y ordenación.
- El endpoint de carrito suma cantidades si el producto ya existe en el carrito y elimina el producto si la cantidad llega a 0.
- Puedes probar todos los endpoints con la colección de Postman incluida en el proyecto.

