# Pruebas del programa

Las pruebas se ejecutaron con `java Pruebas`. Los casos revisan las reglas principales del lab; el resultado obtenido fue el esperado en todos.

| Caso | Datos de entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| Datos iniciales | Iniciar el programa | 8 órdenes, 3 cuadrillas e ingresos en Q0.00 | Correcto |
| Instalación con horas por puntos | Zona metropolitana, 2.5 horas, 5 puntos | 4 horas facturables y costo Q460.00 | Correcto |
| Instalación eléctrica certificada | Mismos datos, materiales por Q2,000 | Q460 + Q120 + Q100 de garantía = Q680.00 | Correcto |
| Diagnóstico | Metro, 1.5 horas / periférica, 1 hora | Q150 y 1 día / Q225 y 2 días | Correcto |
| Instalación periférica | 1.1 horas, 1 punto | 2 horas facturables, Q355.00 y 5 días | Correcto |
| Mantenimiento de aire | Metro, 2 horas, 24,000 BTU | Q400.00 y 2 días, usando la base asumida de Q100 | Correcto |
| Cotización sin aceptación | Cotizar un diagnóstico nuevo | No se agrega a la lista ni genera ingresos | Correcto |
| Aceptar cotización | Registrar el mismo diagnóstico metropolitano | Se guarda el cobro inicial de Q150.00 | Correcto |
| Cuadrilla no certificada | Asignar mantenimiento de aire a C-002 | Operación rechazada y orden sigue pendiente | Correcto |
| Capacidad excedida | Orden de 7.5 horas en cuadrilla de 7 horas | Operación rechazada; jornada y orden no cambian | Correcto |
| Retiro de orden | Retirar durante la preparación | La orden sigue pendiente y no se genera ingreso | Correcto |
| Segunda visita | Diagnóstico: primera visita fallida y segunda iniciada | Vuelve a pendiente tras el fallo; al iniciar la segunda se suma Q40.00 | Correcto |
| Agotar visitas | Diagnóstico con tres visitas fallidas | Queda cancelado y ya no se puede asignar | Correcto |

La tarifa base del mantenimiento de aire se tomó como Q100 porque el enunciado no indica otro monto y el servicio solo se permite en zona metropolitana. Esta interpretación debe confirmarse con el profesor.
