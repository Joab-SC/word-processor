# Word Processor — Arquitectura Pipes and Filters

Procesador de texto en **Java 17+** (sin dependencias externas) que toma un texto —desde un archivo o desde la consola—, le aplica una serie de transformaciones y escribe el resultado en pantalla. Toda la lógica está organizada siguiendo el patrón de arquitectura **Pipes and Filters**.

---

## 1. ¿Qué es la arquitectura Pipes and Filters?

**Pipes and Filters** (tuberías y filtros) es un estilo de arquitectura que descompone un procesamiento complejo en una cadena de pasos independientes y reutilizables. Su analogía natural es una línea de producción o una tubería de agua: el dato entra por un extremo y va pasando por etapas que lo transforman, una a una, hasta salir por el otro extremo.

Tiene 4 tipos de componentes:

| Componente | Rol |
|---|---|
| **Fuente (Source)** | Genera los datos de entrada. Es el único productor del sistema. |
| **Filtro (Filter)** | Unidad de procesamiento: consume los datos, los transforma y emite el resultado. Cada filtro es independiente y solo conoce su entrada y su salida. |
| **Tubería (Pipe)** | El canal por el que viajan los datos de un filtro al siguiente. Encapsula cómo se conectan los filtros. |
| **Sumidero (Sink)** | Consumidor final: recibe el dato ya procesado y lo escribe donde corresponda (consola, archivo, red, memoria...). |

```
┌────────┐   pipe   ┌────────┐   pipe   ┌────────┐          ┌────────┐   pipe   ┌──────┐
│ Source │ ───────▶ │ Filtro │ ───────▶ │ Filtro │ ── ... ─▶│ Filtro │ ───────▶ │ Sink │
└────────┘          └────────┘          └────────┘          └────────┘          └──────┘
```

**Ventajas de este estilo:**

- **Modularidad**: cada transformación vive en su propia clase y se entiende por separado.
- **Reutilización**: los mismos filtros se pueden reordenar o combinar en distintos pipelines.
- **Extensibilidad**: agregar una nueva transformación es crear un filtro nuevo y "enchufarlo" a la cadena; no hay que tocar el resto del código.
- **Testabilidad**: cada filtro es una función pura (entrada → salida), trivial de probar.
- **Fácil sustitución de entrada/salida**: cambiar un archivo por la consola o por la web solo implica cambiar el `Source` o el `Sink`.

---

## 2. Cómo se aplica en este proyecto

En este proyecto el patrón se implementa de forma directa y ligera:

| Concepto del patrón | Implementación aquí |
|---|---|
| **Fuente** | Interfaz `Source` → `FileSource`, `ConsoleSource`, `TextSource` |
| **Filtro** | Interfaz `Filter` → las 6 clases del paquete `filters/` |
| **Tubería** | La clase `Pipeline` (el "pipe" es la `List<String>` que viaja entre filtros) |
| **Sumidero** | Interfaz `Sink` → `ConsoleSink`, `MemorySink` |

La cadena concreta que arma `Main` es:

```
                    ┌────────────────────────── Filter.process() ──────────────────────────┐
FileSource          │                                                                      │
   o                │  RemovePunctuation → NormalizeSpaces → RemoveEmptyLines              │
ConsoleSource  ───▶ │       → ToUpperCase → LineNumber → WordCount                         │ ───▶ ConsoleSink
                    │                                                                      │
                    └──────────────────────────────────────────────────────────────────────┘
```

**Ejemplo paso a paso** con la línea `Hola, mundo!`:

| Etapa | Salida después del filtro |
|---|---|
| entrada (`Source.read()`) | `Hola, mundo!` |
| `RemovePunctuationFilter` | `Hola mundo ` (la puntuación se vuelve espacio) |
| `NormalizeSpacesFilter` | `Hola mundo` (recorta y colapsa espacios repetidos) |
| `RemoveEmptyLinesFilter` | la línea se mantiene (solo descarta las vacías) |
| `ToUpperCaseFilter` | `HOLA MUNDO` |
| `LineNumberFilter` | `1: HOLA MUNDO` |
| `WordCountFilter` | agrega al final `Total words: N` |
| `Sink.write()` | se imprime en consola |

> ⚠️ El **orden** en que se agregan los filtros importa: `WordCountFilter` debe ir después de `LineNumberFilter` porque limpia el prefijo `N: ` antes de contar palabras.

---

## 3. Estructura de carpetas

```
.
├── pom.xml                          # Definición del proyecto Maven (groupId, versión, Java 17)
├── text.txt                         # Archivo de ejemplo para probar el programa
├── src/                             # Código fuente (raíz de paquetes para Maven y javac)
│   └── com/wordprocessor/
│       ├── Main.java                # Punto de entrada: arma el pipeline y lo ejecuta
│       ├── pipeline/                # ⭐ El corazón de la arquitectura (abstracciones)
│       ├── filters/                 # Las transformaciones concretas (los filtros)
│       ├── source/                  # Implementaciones de Source (de dónde vienen los datos)
│       └── sink/                    # Implementaciones de Sink (a dónde va el resultado)
└── target/                          # Salida de compilación de Maven (generado, no editar)
```

### `pipeline/` — las abstracciones del patrón

Define **qué es** cada componente, sin importar cómo se comporta:

- **`Source.java`** — interfaz con un método `read()`: produce la lista de líneas de entrada.
- **`Filter.java`** — interfaz con un método `process(List<String>)`: recibe líneas, devuelve líneas transformadas.
- **`Sink.java`** — interfaz con un método `write(List<String>)`: consume el resultado final.
- **`Pipeline.java`** — el ensamblador: guarda la lista de filtros y ejecuta el flujo completo.

### `filters/` — las 6 transformaciones

Cada clase implementa `Filter` y hace **una sola cosa**:

| Filtro | Qué hace |
|---|---|
| `RemovePunctuationFilter` | Reemplaza todo signo de puntuación por espacios (conserva letras, dígitos y apóstrofes). |
| `NormalizeSpacesFilter` | Recorta cada línea y colapsa espacios repetidos en uno solo. |
| `RemoveEmptyLinesFilter` | Elimina las líneas vacías o en blanco. |
| `ToUpperCaseFilter` | Pasa todo a mayúsculas (independiente del locale con `Locale.ROOT`). |
| `LineNumberFilter` | Antepone `N: ` a cada línea. |
| `WordCountFilter` | Mantiene las líneas y agrega una final `Total words: N`. |

### `source/` — de dónde vienen los datos

| Source | Qué hace |
|---|---|
| `FileSource` | Lee un archivo de texto (línea por línea) desde disco. |
| `ConsoleSource` | Lee lo que el usuario escribe en la consola, hasta una línea que diga `FIN`. |
| `TextSource` | Envuelve un `String` ya en memoria (útil para pruebas o para el cuerpo de un request HTTP). No se usa en `Main`, está listo como punto de extensión. |

### `sink/` — a dónde va el resultado

| Sink | Qué hace |
|---|---|
| `ConsoleSink` | Imprime las líneas en la salida estándar. |
| `MemorySink` | Guarda el resultado en memoria y lo expone con `lines()` (útil para tests o para devolverlo por HTTP). No se usa en `Main`, está listo como punto de extensión. |

---

## 4. Funciones clave para su funcionamiento

**`Main.main(String[] args)`** — el punto de entrada. Solo hace dos cosas: ensambla el pipeline encadenando filtros y lo corre con la fuente/sink elegidos según los argumentos.

```java
Pipeline pipeline = new Pipeline()
        .add(new RemovePunctuationFilter())
        .add(new NormalizeSpacesFilter())
        .add(new RemoveEmptyLinesFilter())
        .add(new ToUpperCaseFilter())
        .add(new LineNumberFilter())
        .add(new WordCountFilter());

Source source = args.length > 0 ? new FileSource(Path.of(args[0])) : new ConsoleSource();
pipeline.run(source, new ConsoleSink());
```

Las firmas que sostienen toda la arquitectura:

| Método | Responsabilidad |
|---|---|
| `Pipeline.add(Filter)` | Registra un filtro al final de la cadena (API fluida: devuelve `this` para encadenar). |
| `Pipeline.run(Source, Sink)` | Orquesta el flujo completo: `sink.write(execute(source.read()))`. |
| `Pipeline.execute(List<String>)` | Recorre los filtros en orden, pasando el resultado de uno como entrada del siguiente. Aquí la `List<String>` que se va pasando hace el papel de **pipe**. |
| `Source.read()` | Produce los datos de entrada como lista de líneas. |
| `Filter.process(List<String>)` | La unidad de transformación: recibe líneas y devuelve líneas. |
| `Sink.write(List<String>)` | Consume el resultado final (imprimir, guardar, etc.). |

```java
// Pipeline.java — el ciclo completo del patrón en tres líneas
public void run(Source source, Sink sink) throws IOException {
    sink.write(execute(source.read()));
}

public List<String> execute(List<String> input) {
    List<String> data = input;              // "data" hace el papel del pipe
    for (Filter filter : filters) {
        data = filter.process(data);
    }
    return data;
}
```

---

## 5. Cómo correr el proyecto

**Requisito:** JDK 17 o superior. Maven es opcional (solo una de las dos formas).

### Opción A — Compilación directa con `javac` (sin Maven)

```bash
# Linux / macOS / Git Bash
javac -d out $(find src -name "*.java")
```

```bat
:: Windows (cmd)
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
```

**Correr con un archivo:**

```bash
java -cp out com.wordprocessor.Main text.txt
```

Salida esperada con `text.txt`:

```
1: HOLA MUNDO
2: ESTO ES UNA PRUEBA
3: OTRA LINEA CON PUNTUACION
Total words: 10
```

**Correr en modo interactivo** (sin argumentos): escribe tu texto y termina con la línea `FIN`:

```bash
java -cp out com.wordprocessor.Main
Escribe tu texto. Cuando termines, escribe FIN en una linea sola:
Hola, mundo!
FIN
1: HOLA MUNDO
Total words: 2
```

### Opción B — Con Maven

```bash
mvn compile
java -cp target/classes com.wordprocessor.Main text.txt
```

---

## 6. Cómo extender el pipeline

Gracias al patrón, agregar una transformación nueva requiere **un solo archivo** y una línea en `Main`:

```java
// src/com/wordprocessor/filters/ReverseLinesFilter.java
package com.wordprocessor.filters;

import com.wordprocessor.pipeline.Filter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReverseLinesFilter implements Filter {

    @Override
    public List<String> process(List<String> lines) {
        List<String> result = new ArrayList<>(lines);
        Collections.reverse(result);
        return result;
    }
}
```

```java
// En Main.java, en la posición que quieras de la cadena:
.add(new ReverseLinesFilter())
```

Lo mismo aplica para entradas y salidas: una nueva fuente solo implementa `Source.read()`, y una nueva salida solo implementa `Sink.write()`. El resto del sistema no cambia.
