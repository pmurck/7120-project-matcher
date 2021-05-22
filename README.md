# Project Matcher
### Trabajo Práctico para 71.20 Modelos y Optimización III (1°C 2020)
Aplicación web que provee un sistema de optimización de asignaciones de desarrolladores a proyectos considerando requisitos, disponibilidades y preferencias de ambas partes.
Modelado como problema de programación lineal (parecido al [problema de transporte](https://es.wikipedia.org/wiki/Problema_de_transporte)) y optimizado con el método Simplex.
La interfaz es extremadamente básica (todo basado en los ejemplos de Bootstrap).

#### Herramientas:
- Kotlin + framework [Ktor](https://ktor.io/)
- MongoDB (uso via [KMongo](https://litote.org/kmongo/))
- [Koin] para un poco de DI
- Frontend hecho con [Apache Freemarker] + Bootstrap, jQuery y [Tabulator]
- [Apache Math] para el algoritmo simplex

[Koin]: https://insert-koin.io/
[Apache Math]: https://commons.apache.org/proper/commons-math/
[Apache Freemarker]: https://freemarker.apache.org/
[Tabulator]: http://tabulator.info/
---
#### Ejecución con Docker Compose:
El compose file tiene lo necesario para levantar mongo y la aplicación (tanto el build como la ejecución)
```sh
(sudo) docker-compose up (-d)
```
y si es necesario, previamente
```sh
(sudo) docker-compose build
```
Luego, ir a
```
http://localhost:8081
```
Si se quiere, cambiar los puertos en el compose file.
Además, con
```
http://localhost:8081/loadexamples
```
se pueden cargar los ejemplos definidos en `src/Example.kt`.
