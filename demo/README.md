# Demo — Regex Preview Companion

No hace falta ningún archivo de proyecto para esto -- es un tool
window autocontenido, con su propio campo de patrón y su propia área
de texto de muestra.

## Pasos

1. Abrí el tool window **Regex Preview** (abajo del IDE, secundario --
   si no lo ves, `View → Tool Windows → Regex Preview`).
2. Pegá este texto de muestra en el área de texto:

```
Contact: ada@example.com or grace.hopper@navy.mil
Phone: +1 (555) 123-4567, alt: 555.987.6543
Order IDs: ORD-2026-0001, ord-2026-0002 (lowercase, distinto caso)
Invalid: not-an-email@, @missing-user.com
```

3. Probá cada patrón, y para cada uno anotá cuántos matches resalta
   (el contador real, no de ojo):

| Patrón | Qué debería matchear |
|---|---|
| `[\w.]+@[\w.]+` | Los 2 emails válidos, no los inválidos |
| `\+?\d[\d\s().-]{7,}\d` | Los 2 teléfonos |
| `ORD-\d{4}-\d{4}` | Solo `ORD-2026-0001` (mayúsculas) -- probá tildar **Case insensitive** después y confirmar que ahí sí matchea las 2 |
| `[` (a propósito, patrón inválido) | Debería mostrar el mensaje real de `PatternSyntaxException`, no quedar en blanco |

4. Probá los 3 checkboxes (**Case insensitive**, **Multiline**,
   **Dot all**) con el patrón `ORD-\d{4}-\d{4}` y confirmá que el
   comportamiento cambia como corresponde.
5. Escribí letra por letra un patrón (no pegues) y confirmá que el
   resaltado se actualiza en vivo, sin tener que apretar Enter ni
   ningún botón.

## Qué reportar

- ¿El conteo de matches es correcto en cada caso?
- ¿El patrón inválido (`[`) muestra el mensaje real de error, no algo
  genérico ni una pantalla en blanco?
- ¿Los 3 checkboxes cambian el resultado como se espera?
- ¿Se actualiza en vivo con cada tecla, sin lag perceptible?
