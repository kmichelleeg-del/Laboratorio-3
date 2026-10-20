import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final Scanner ENTRADA = new Scanner(System.in);

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        SistemaTecniHogar sistema = new SistemaTecniHogar();
        System.out.println("TecniHogar - Gestión de servicios técnicos");
        System.out.println("Se cargaron órdenes de ejemplo y cuadrillas. Los ingresos iniciales son Q0.00.");
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            int opcion = leerEntero("Seleccione una opción: ");
            try {
                switch (opcion) {
                    case 1: cotizarYRegistrar(sistema); break;
                    case 2: registrarCuadrilla(sistema); break;
                    case 3: crearJornada(sistema); break;
                    case 4: asignarOrden(sistema); break;
                    case 5: retirarOrden(sistema); break;
                    case 6: iniciarJornada(sistema); break;
                    case 7: registrarResultado(sistema); break;
                    case 8: consultarOrdenes(sistema); break;
                    case 9: consultarCuadrillas(sistema); break;
                    case 10: sistema.generarReporte().imprimir(); break;
                    case 0: salir = true; System.out.println("Programa finalizado."); break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("No se pudo completar la operación: " + ex.getMessage());
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("MENÚ");
        System.out.println("1. Cotizar y registrar una orden");
        System.out.println("2. Registrar cuadrilla");
        System.out.println("3. Crear jornada");
        System.out.println("4. Asignar orden a jornada");
        System.out.println("5. Retirar orden de jornada");
        System.out.println("6. Iniciar jornada");
        System.out.println("7. Registrar resultado de visita");
        System.out.println("8. Consultar órdenes");
        System.out.println("9. Consultar cuadrillas y jornadas");
        System.out.println("10. Reportes");
        System.out.println("0. Salir");
    }

    private static void cotizarYRegistrar(SistemaTecniHogar sistema) {
        OrdenServicio orden = crearOrdenDesdeEntrada();
        if (orden == null) return;
        System.out.println("Cotización: " + sistema.cotizar(orden));
        if (leerSiNo("¿El cliente acepta y desea registrar la orden? (s/n): ")) {
            mostrarResultado(sistema.registrarOrden(orden));
        } else {
            System.out.println("La cotización no se registró y no generó ingresos.");
        }
    }

    private static OrdenServicio crearOrdenDesdeEntrada() {
        System.out.println("Categoría: 1) Diagnóstico  2) Instalación  3) Eléctrica certificada  4) Aire acondicionado");
        int tipo = leerEntero("Seleccione: ");
        String codigo = leerTexto("Código de orden: ");
        String cliente = leerTexto("Nombre del cliente: ");
        String direccion = leerTexto("Dirección: ");
        double horas = leerDecimal("Horas estimadas: ");
        Zona zona = leerZona();
        try {
            switch (tipo) {
                case 1:
                    return new Diagnostico(codigo, cliente, direccion, horas, zona);
                case 2: {
                    int puntos = leerEntero("Cantidad de puntos: ");
                    return new Instalacion(codigo, cliente, direccion, horas, zona, puntos);
                }
                case 3: {
                    int puntos = leerEntero("Cantidad de puntos: ");
                    double materiales = leerDecimalNoNegativo("Valor declarado de materiales: Q");
                    return new ElectricaCertificada(codigo, cliente, direccion, horas, zona, puntos, materiales);
                }
                case 4: {
                    int btu = leerEntero("Capacidad del equipo en BTU: ");
                    return new MantenimientoAC(codigo, cliente, direccion, horas, zona, btu);
                }
                default:
                    System.out.println("Categoría inválida.");
                    return null;
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Datos inválidos: " + ex.getMessage());
            return null;
        }
    }

    private static void registrarCuadrilla(SistemaTecniHogar sistema) {
        String codigo = leerTexto("Código de cuadrilla: ");
        double capacidad = leerDecimal("Capacidad máxima de horas: ");
        boolean certificada = leerSiNo("¿Tiene certificación en refrigeración? (s/n): ");
        try {
            mostrarResultado(sistema.registrarCuadrilla(new Cuadrilla(codigo, capacidad, certificada)));
        } catch (IllegalArgumentException ex) {
            System.out.println("Datos inválidos: " + ex.getMessage());
        }
    }

    private static void crearJornada(SistemaTecniHogar sistema) {
        consultarCuadrillas(sistema);
        mostrarResultado(sistema.crearJornada(leerTexto("Código de cuadrilla: ")));
    }

    private static void asignarOrden(SistemaTecniHogar sistema) {
        consultarJornadas(sistema);
        int id = leerEntero("Número de jornada: ");
        consultarOrdenes(sistema);
        mostrarResultado(sistema.asignarOrden(id, leerTexto("Código de orden: ")));
    }

    private static void retirarOrden(SistemaTecniHogar sistema) {
        consultarJornadas(sistema);
        int id = leerEntero("Número de jornada: ");
        mostrarResultado(sistema.retirarOrden(id, leerTexto("Código de orden: ")));
    }

    private static void iniciarJornada(SistemaTecniHogar sistema) {
        consultarJornadas(sistema);
        mostrarResultado(sistema.iniciarJornada(leerEntero("Número de jornada: ")));
    }

    private static void registrarResultado(SistemaTecniHogar sistema) {
        consultarJornadas(sistema);
        int id = leerEntero("Número de jornada: ");
        String codigo = leerTexto("Código de orden: ");
        int opcion;
        do {
            opcion = leerEntero("Resultado: 1) Servicio completado  2) Visita fallida: ");
            if (opcion != 1 && opcion != 2) System.out.println("Seleccione 1 o 2.");
        } while (opcion != 1 && opcion != 2);
        boolean exito = opcion == 1;
        mostrarResultado(sistema.registrarResultado(id, codigo, exito));
    }

    private static void consultarOrdenes(SistemaTecniHogar sistema) {
        System.out.println("\n--- ÓRDENES ---");
        List<OrdenServicio> ordenes = sistema.getOrdenes();
        if (ordenes.isEmpty()) System.out.println("No hay órdenes registradas.");
        for (OrdenServicio orden : ordenes) {
            System.out.printf("%s | Registro: Q%.2f | Visitas adicionales: Q%.2f%n",
                    orden, orden.getMontoRegistro(), orden.getCargoVisitas());
        }
    }

    private static void consultarCuadrillas(SistemaTecniHogar sistema) {
        System.out.println("\n--- CUADRILLAS ---");
        if (sistema.getCuadrillas().isEmpty()) System.out.println("No hay cuadrillas registradas.");
        for (Cuadrilla cuadrilla : sistema.getCuadrillas()) System.out.println(cuadrilla);
    }

    private static void consultarJornadas(SistemaTecniHogar sistema) {
        System.out.println("JORNADAS");
        if (sistema.getJornadas().isEmpty()) System.out.println("No hay jornadas.");
        for (Jornada jornada : sistema.getJornadas()) {
            System.out.println(jornada);
            for (OrdenServicio orden : jornada.getOrdenes()) {
                System.out.println("  " + orden.getCodigo() + " - " + orden.getEstado());
            }
        }
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return ENTRADA.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje);
            try { return Integer.parseInt(valor); }
            catch (NumberFormatException ex) { System.out.println("Ingrese un número entero válido."); }
        }
    }

    private static double leerDecimal(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje).replace(',', '.');
            try {
                double numero = Double.parseDouble(valor);
                if (Double.isFinite(numero)) return numero;
            } catch (NumberFormatException ignored) { }
            System.out.println("Ingrese un número válido.");
        }
    }

    private static double leerDecimalNoNegativo(String mensaje) {
        while (true) {
            double valor = leerDecimal(mensaje);
            if (valor >= 0) return valor;
            System.out.println("El valor no puede ser negativo.");
        }
    }

    private static Zona leerZona() {
        while (true) {
            int zona = leerEntero("Zona: 1) Metropolitana  2) Periférica: ");
            if (zona == 1) return Zona.METROPOLITANA;
            if (zona == 2) return Zona.PERIFERICA;
            System.out.println("Seleccione 1 o 2.");
        }
    }

    private static boolean leerSiNo(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje).toLowerCase(Locale.ROOT);
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) return true;
            if (respuesta.equals("n") || respuesta.equals("no")) return false;
            System.out.println("Responda s o n.");
        }
    }

    private static void mostrarResultado(String resultado) {
        System.out.println(resultado == null ? "Operación realizada correctamente." : resultado);
    }
}
