import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jornada {
    private final int id;
    private final Cuadrilla cuadrilla;
    private final List<OrdenServicio> ordenes;
    private final Map<OrdenServicio, Boolean> resultados;
    private EstadoJornada estado;

    public Jornada(int id, Cuadrilla cuadrilla) {
        if (id <= 0 || cuadrilla == null) {
            throw new IllegalArgumentException("La jornada necesita un identificador y una cuadrilla.");
        }
        this.id = id;
        this.cuadrilla = cuadrilla;
        this.ordenes = new ArrayList<OrdenServicio>();
        this.resultados = new HashMap<OrdenServicio, Boolean>();
        this.estado = EstadoJornada.EN_PREPARACION;
    }

    public String validarAsignacion(OrdenServicio orden) {
        if (estado != EstadoJornada.EN_PREPARACION) return "La jornada ya no está en preparación.";
        if (orden == null) return "No se encontró la orden.";
        if (orden.getEstado() != EstadoOrden.PENDIENTE) return "La orden no está pendiente.";
        if (ordenes.contains(orden)) return "La orden ya está en esta jornada.";
        if (!orden.compatible(cuadrilla)) return "La orden no es compatible con esta cuadrilla.";
        if (horasAsignadas() + orden.getHorasEstimadas() > cuadrilla.getCapacidadHoras() + 1e-9) {
            return "La asignación supera la capacidad de la cuadrilla.";
        }
        return null;
    }

    public boolean asignar(OrdenServicio orden) {
        if (validarAsignacion(orden) != null) return false;
        ordenes.add(orden);
        return true;
    }

    public boolean retirar(OrdenServicio orden) {
        return estado == EstadoJornada.EN_PREPARACION && ordenes.remove(orden);
    }

    public String iniciar() {
        if (estado != EstadoJornada.EN_PREPARACION) return "La jornada no está en preparación.";
        if (ordenes.isEmpty()) return "No se puede iniciar una jornada vacía.";
        for (OrdenServicio orden : ordenes) {
            if (orden.getEstado() != EstadoOrden.PENDIENTE) {
                return "Todas las órdenes deben estar pendientes para iniciar.";
            }
        }
        for (OrdenServicio orden : ordenes) orden.iniciarAtencion();
        estado = EstadoJornada.EN_CURSO;
        return null;
    }

    public String registrarResultado(OrdenServicio orden, boolean exito) {
        if (estado != EstadoJornada.EN_CURSO) return "La jornada no está en curso.";
        if (!ordenes.contains(orden)) return "La orden no pertenece a esta jornada.";
        if (resultados.containsKey(orden)) return "El resultado de esta orden ya fue registrado.";
        if (orden.getEstado() != EstadoOrden.EN_ATENCION) return "La orden no está en atención.";
        orden.registrarResultado(exito);
        resultados.put(orden, exito);
        if (resultados.size() == ordenes.size()) estado = EstadoJornada.CERRADA;
        return null;
    }

    public double horasAsignadas() {
        double total = 0.0;
        for (OrdenServicio orden : ordenes) total += orden.getHorasEstimadas();
        return total;
    }

    public double horasDisponibles() {
        return Math.max(0.0, cuadrilla.getCapacidadHoras() - horasAsignadas());
    }

    public int getId() { return id; }
    public Cuadrilla getCuadrilla() { return cuadrilla; }
    public EstadoJornada getEstado() { return estado; }
    public List<OrdenServicio> getOrdenes() { return Collections.unmodifiableList(ordenes); }
    public boolean contiene(OrdenServicio orden) { return ordenes.contains(orden); }

    @Override
    public String toString() {
        return String.format("Jornada %d | Cuadrilla %s | %s | %.2f/%.2f h",
                id, cuadrilla.getCodigo(), estado, horasAsignadas(), cuadrilla.getCapacidadHoras());
    }
}
