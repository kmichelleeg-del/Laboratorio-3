import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Reporte {
    private final List<OrdenServicio> ordenes;
    private final List<Jornada> jornadas;

    public Reporte(List<OrdenServicio> ordenes, List<Jornada> jornadas) {
        this.ordenes = ordenes;
        this.jornadas = jornadas;
    }

    public void imprimir() {
        System.out.println("\n=== ÓRDENES POR CATEGORÍA Y ESTADO ===");
        Map<CategoriaServicio, Map<EstadoOrden, Integer>> conteos =
                new EnumMap<CategoriaServicio, Map<EstadoOrden, Integer>>(CategoriaServicio.class);
        for (CategoriaServicio categoria : CategoriaServicio.values()) {
            Map<EstadoOrden, Integer> estados = new EnumMap<EstadoOrden, Integer>(EstadoOrden.class);
            for (EstadoOrden estado : EstadoOrden.values()) estados.put(estado, 0);
            conteos.put(categoria, estados);
        }
        for (OrdenServicio orden : ordenes) {
            Map<EstadoOrden, Integer> estados = conteos.get(orden.categoria());
            estados.put(orden.getEstado(), estados.get(orden.getEstado()) + 1);
        }
        for (CategoriaServicio categoria : CategoriaServicio.values()) {
            System.out.println("\n" + categoria.getNombre());
            for (EstadoOrden estado : EstadoOrden.values()) {
                System.out.println("  " + estado + ": " + conteos.get(categoria).get(estado));
            }
        }

        double registro = 0.0;
        double visitas = 0.0;
        Map<CategoriaServicio, double[]> ingresosCategoria =
                new EnumMap<CategoriaServicio, double[]>(CategoriaServicio.class);
        for (CategoriaServicio categoria : CategoriaServicio.values()) ingresosCategoria.put(categoria, new double[2]);
        for (OrdenServicio orden : ordenes) {
            registro += orden.getMontoRegistro();
            visitas += orden.getCargoVisitas();
            double[] valores = ingresosCategoria.get(orden.categoria());
            valores[0] += orden.getMontoRegistro();
            valores[1] += orden.getCargoVisitas();
        }
        System.out.println("\n=== INGRESOS ===");
        System.out.println(String.format(Locale.US, "Por registro: Q%.2f", registro));
        System.out.println(String.format(Locale.US, "Por visitas adicionales: Q%.2f", visitas));
        System.out.println(String.format(Locale.US, "Total: Q%.2f", registro + visitas));
        System.out.println("Por categoría:");
        for (CategoriaServicio categoria : CategoriaServicio.values()) {
            double[] valores = ingresosCategoria.get(categoria);
            System.out.println(String.format(Locale.US, "  %s: Q%.2f", categoria.getNombre(), valores[0] + valores[1]));
        }

        System.out.println("\n=== JORNADAS ABIERTAS ===");
        boolean hayAbiertas = false;
        for (Jornada jornada : jornadas) {
            if (jornada.getEstado() != EstadoJornada.CERRADA) {
                hayAbiertas = true;
                System.out.println(jornada);
                System.out.println(String.format(Locale.US, "  Asignadas: %.2f h | Disponibles: %.2f h",
                        jornada.horasAsignadas(), jornada.horasDisponibles()));
            }
        }
        if (!hayAbiertas) System.out.println("No hay jornadas abiertas.");
    }
}
