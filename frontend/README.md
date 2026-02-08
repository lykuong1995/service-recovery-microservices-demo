# Frontend (Vue 3 + Vite)

This is a lightweight SPA that talks to the microservices through the API Gateway.

## Run

```bash
cd frontend
npm install
VITE_API_URL=http://localhost:8080 npm run dev
```

App runs on `http://localhost:5173`.

## Routes

This project uses simple hash routing (no extra deps):

- `#/login`
- `#/products`
- `#/cart`
- `#/orders`

## Structure

- `src/pages/` – Page-level UI (Products, Cart, Orders, Login)
- `src/features/` – Feature modules (API calls + feature-specific logic)
- `src/shared/` – Reusable UI + shared libs (HTTP client, tokens, router, formatters)
- `src/styles/` – Global design tokens + base layout helpers

## Styling rules

- Prefer component-scoped styles in `*.vue`.
- Keep global CSS limited to `src/styles/index.css` (tokens + layout helpers).
- Use `src/shared/components/ui/*` primitives instead of ad-hoc button/input/card CSS.
