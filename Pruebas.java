public class Pruebas {
    public static void main(String[] args) {
        probarDatosInicialesYCalculos();
        probarCostosYTiempos();
        probarCotizacionYRegistro();
        probarVisitasYCargoAdicional();
        probarCompatibilidadYCapacidad();
        probarCancelacionAlAgotarVisitas();
        System.out.println("Todas las pruebas terminaron correctamente.");
    }

    private static void probarDatosInicialesYCalculos() {
        SistemaTecniHogar sistema = new SistemaTecniHogar();
        verificar(sistema.getOrdenes().size() == 8, "Se cargan dos órdenes por categoría.");
        verificar(sistema.getCuadrillas().size() == 3, "Se cargan tres cuadrillas.");
        for (OrdenServicio orden : sistema.getOrdenes()) {
            verificar(orden.getMontoRegistro() == 0.0 && orden.getCargoVisitas() == 0.0,
                    "Los datos iniciales no generan ingresos.");
        }
        Instalacion instalacion = (Instalacion) sistema.buscarOrden("I-001");
        verificar(instalacion.horasFacturables() == 4, "Las horas por puntos se redondean hacia arriba.");
        verificar(instalacion.calcularCosto() == 460.0, "La instalación de ejemplo cuesta Q460.00.");
        ElectricaCertificada electrica = (ElectricaCertificada) sistema.buscarOrden("E-001");
        verificar(electrica.calcularCosto() == 590.0, "La instalación certificada suma certificado y garantía.");
    }

    private static void probarCotizacionYRegistro() {
        SistemaTecniHogar sistema = new SistemaTecniHogar();
        OrdenServicio orden = new Diagnostico("D-TEST", "Cliente", "Zona 1", 1.0, Zona.METROPOLITANA);
        int cantidadAntes = sistema.getOrdenes().size();
        sistema.cotizar(orden);
        verificar(sistema.getOrdenes().size() == cantidadAntes, "Cotizar no registra la orden.");
        verificar(orden.getMontoRegistro() == 0.0, "Cotizar no genera ingresos.");
        verificar(sistema.registrarOrden(orden) == null, "La orden aceptada se registra.");
        verificar(orden.getMontoRegistro() == 150.0, "El cobro inicial se guarda al registrar.");
        verificar(sistema.registrarOrden(orden) != null, "No se acepta un código repetido.");
    }

    private static void probarCostosYTiempos() {
        OrdenServicio diagnosticoMetro = new Diagnostico("D-M", "Cliente", "Zona 1", 1.5, Zona.METROPOLITANA);
        OrdenServicio diagnosticoPeri = new Diagnostico("D-P", "Cliente", "Mixco", 1.0, Zona.PERIFERICA);
        Instalacion instalacionMetro = new Instalacion("I-M", "Cliente", "Zona 1", 2.5, Zona.METROPOLITANA, 5);
        Instalacion instalacionPeri = new Instalacion("I-P", "Cliente", "Mixco", 1.1, Zona.PERIFERICA, 1);
        ElectricaCertificada certificada = new ElectricaCertificada(
                "E-M", "Cliente", "Zona 1", 2.5, Zona.METROPOLITANA, 5, 2000.0);
        MantenimientoAC aire = new MantenimientoAC(
                "A-M", "Cliente", "Zona 1", 2.0, Zona.METROPOLITANA, 24000);

        verificar(diagnosticoMetro.calcularCosto() == 150.0 && diagnosticoMetro.diasEstimados() == 1,
                "Diagnóstico metropolitano: costo y tiempo.");
        verificar(diagnosticoPeri.calcularCosto() == 225.0 && diagnosticoPeri.diasEstimados() == 2,
                "Diagnóstico periférico: costo y tiempo.");
        verificar(instalacionMetro.calcularCosto() == 460.0 && instalacionMetro.diasEstimados() == 3,
                "Instalación metropolitana: horas por puntos y fracción.");
        verificar(instalacionPeri.calcularCosto() == 355.0 && instalacionPeri.diasEstimados() == 5,
                "Instalación periférica: tarifa base y redondeo de horas.");
        verificar(certificada.calcularCosto() == 680.0 && certificada.diasEstimados() == 5,
                "Instalación certificada: certificado, garantía y tiempo adicional.");
        verificar(aire.calcularCosto() == 400.0 && aire.diasEstimados() == 2,
                "Mantenimiento de aire: tarifa, horas, refrigerante y tiempo.");
        verificar(aire.maxVisitas() == 1 && certificada.maxVisitas() == 2
                        && instalacionMetro.maxVisitas() == 3 && diagnosticoMetro.maxVisitas() == 3,
                "Cada categoría devuelve su máximo de visitas.");
    }

    private static void probarVisitasYCargoAdicional() {
        SistemaTecniHogar sistema = new SistemaTecniHogar();
        OrdenServicio orden = new Diagnostico("D-VISITA", "Cliente", "Zona 1", 1.0, Zona.METROPOLITANA);
        verificar(sistema.registrarOrden(orden) == null, "Se registra la orden para la prueba.");
        verificar(sistema.crearJornada("C-002") != null, "Se crea la primera jornada.");
        Jornada primera = sistema.getJornadas().get(0);
        verificar(sistema.asignarOrden(primera.getId(), orden.getCodigo()) == null, "Se asigna la orden pendiente.");
        verificar(sistema.retirarOrden(primera.getId(), orden.getCodigo()) == null, "Se puede retirar en preparación.");
        verificar(orden.getCargoVisitas() == 0.0, "Retirar una orden no genera ingresos.");
        verificar(sistema.asignarOrden(primera.getId(), orden.getCodigo()) == null, "Se vuelve a asignar la orden.");
        verificar(sistema.iniciarJornada(primera.getId()) == null, "La jornada inicia.");
        verificar(sistema.registrarResultado(primera.getId(), orden.getCodigo(), false) == null,
                "Se registra la visita fallida.");
        verificar(orden.getEstado() == EstadoOrden.PENDIENTE, "La orden vuelve a pendiente.");
        verificar(orden.getCargoVisitas() == 0.0, "La primera visita no cobra adicional.");
        verificar(sistema.crearJornada("C-002") != null, "La cuadrilla queda libre al cerrar la jornada.");
        Jornada segunda = sistema.getJornadas().get(1);
        verificar(sistema.asignarOrden(segunda.getId(), orden.getCodigo()) == null, "La orden puede reprogramarse.");
        verificar(sistema.iniciarJornada(segunda.getId()) == null, "Inicia la segunda visita.");
        verificar(orden.getCargoVisitas() == 40.0, "La segunda visita agrega Q40.00 al iniciar.");
        verificar(sistema.registrarResultado(segunda.getId(), orden.getCodigo(), true) == null,
                "Se registra la orden completada.");
        verificar(orden.getEstado() == EstadoOrden.COMPLETADA, "La orden queda completada.");
    }

    private static void probarCompatibilidadYCapacidad() {
        SistemaTecniHogar sistema = new SistemaTecniHogar();
        OrdenServicio aire = sistema.buscarOrden("A-001");
        verificar(sistema.crearJornada("C-002") != null, "Se crea jornada para probar certificación.");
        Jornada jornada = sistema.getJornadas().get(0);
        verificar(sistema.asignarOrden(jornada.getId(), aire.getCodigo()) != null,
                "Una cuadrilla sin certificación no recibe mantenimiento de aire.");
        verificar(jornada.getOrdenes().isEmpty(), "Una asignación rechazada no cambia la jornada.");
        verificar(sistema.asignarOrden(999, "A-001") != null, "Se rechaza una jornada inexistente.");
        verificar(aire.getEstado() == EstadoOrden.PENDIENTE && aire.getVisitas() == 0,
                "Una operación rechazada no cambia la orden.");

        OrdenServicio larga = new Instalacion("I-LARGA", "Cliente", "Zona 1", 7.5, Zona.METROPOLITANA, 1);
        verificar(sistema.registrarOrden(larga) == null, "Se registra la instalación para probar capacidad.");
        verificar(sistema.asignarOrden(jornada.getId(), larga.getCodigo()) != null,
                "Se rechaza una orden que excede las horas disponibles.");
        verificar(jornada.horasAsignadas() == 0.0 && larga.getEstado() == EstadoOrden.PENDIENTE,
                "La asignación rechazada no cambia jornada ni orden.");
    }

    private static void probarCancelacionAlAgotarVisitas() {
        SistemaTecniHogar sistema = new SistemaTecniHogar();
        OrdenServicio orden = new Diagnostico("D-CANCEL", "Cliente", "Zona 1", 1.0, Zona.METROPOLITANA);
        verificar(sistema.registrarOrden(orden) == null, "Se registra la orden para probar el máximo de visitas.");
        for (int visita = 1; visita <= orden.maxVisitas(); visita++) {
            verificar(sistema.crearJornada("C-002") != null, "Se crea jornada para visita " + visita + ".");
            Jornada jornada = sistema.getJornadas().get(sistema.getJornadas().size() - 1);
            verificar(sistema.asignarOrden(jornada.getId(), orden.getCodigo()) == null, "Se asigna para visita " + visita + ".");
            verificar(sistema.iniciarJornada(jornada.getId()) == null, "Inicia visita " + visita + ".");
            verificar(sistema.registrarResultado(jornada.getId(), orden.getCodigo(), false) == null,
                    "Se registra como fallida la visita " + visita + ".");
        }
        verificar(orden.getEstado() == EstadoOrden.CANCELADA, "La orden se cancela al agotar sus tres visitas.");
        verificar(sistema.crearJornada("C-002") != null, "La cuadrilla queda libre al cerrar la última jornada.");
        Jornada ultima = sistema.getJornadas().get(sistema.getJornadas().size() - 1);
        verificar(sistema.asignarOrden(ultima.getId(), orden.getCodigo()) != null,
                "Una orden cancelada ya no se puede asignar.");
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) throw new IllegalStateException("Falló la prueba: " + mensaje);
        System.out.println("OK: " + mensaje);
    }
}
