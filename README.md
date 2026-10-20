# TecniHogar - Lab 3 Herencia

Aplicación de consola en Java para cotizar y registrar órdenes de servicio, organizar cuadrillas y jornadas, registrar visitas y consultar reportes. Los datos se conservan solo mientras el programa está abierto.

Las pruebas revisan los datos iniciales, cálculos de cobro, cotización sin ingresos, reprogramación, cargo por segunda visita, certificación y rechazo de operaciones inválidas.
Los casos y resultados obtenidos también están resumidos en `PRUEBAS.md`.

El programa inicia con dos órdenes de cada categoría y tres cuadrillas. Los datos iniciales no generan ingresos. Incluyen una instalación donde las horas por puntos superan las horas estimadas.

## Menú

Permite cotizar y registrar órdenes, registrar cuadrillas, crear jornadas, asignar o retirar órdenes, iniciar jornadas, registrar resultados, consultar órdenes y cuadrillas y generar los reportes.

## Nota sobre la tarifa de aire acondicionado

El enunciado indica que se cobra una tarifa base según la zona, pero no especifica su monto. Como solo acepta zona metropolitana, el programa asume Q100, la tarifa metropolitana indicada para instalaciones. Conviene confirmar esta interpretación con el profesor.
