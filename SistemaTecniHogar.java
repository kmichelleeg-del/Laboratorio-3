import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SistemaTecniHogar {
    private final Map<String, OrdenServicio> ordenes;
    private final Map<String, Cuadrilla> cuadrillas;
    private final List<Jornada> jornadas;
    private int siguienteIdJornada;

    public SistemaTecniHogar() {
        ordenes = new LinkedHashMap<String, OrdenServicio>();
        cuadrillas = new LinkedHashMap<String, Cuadrilla>();
        jornadas = new ArrayList<Jornada>();
        siguienteIdJornada = 1;
        cargarDatosIniciales();
    }

    public String cotizar(OrdenServicio orden) {
        if (orden == null) return "No se pudo crear la cotización.";
        return String.format("Costo: Q%.2f | Atención estimada: %d día(s)",
                orden.calcularCosto(), orden.diasEstimados());
    }

    public String registrarOrden(OrdenServicio orden) {
        if (orden == null) return "No se encontró la orden.";
        if (ordenes.containsKey(orden.getCodigo())) return "Ya existe una orden con ese código.";
        try {
            orden.registrarCobroInicial();
            ordenes.put(orden.getCodigo(), orden);
            return null;
        } catch (IllegalStateException ex) {
            return ex.getMessage();
        }
    }

    private String agregarOrdenInicial(OrdenServicio orden) {
        if (ordenes.containsKey(orden.getCodigo())) return "Código de orden repetido.";
        orden.agregarOrdenInicialSinIngreso();
        ordenes.put(orden.getCodigo(), orden);
        return null;
    }

    public String registrarCuadrilla(Cuadrilla cuadrilla) {
        if (cuadrilla == null) return "No se encontró la cuadrilla.";
        if (this.cuadrillas.containsKey(cuadrilla.getCodigo())) return "Ya existe una cuadrilla con ese código.";
        this.cuadrillas.put(cuadrilla.getCodigo(), cuadrilla);
        return null;
    }

    public String crearJornada(String codigoCuadrilla) {
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);
        if (cuadrilla == null) return "No se encontró esa cuadrilla.";
        for (Jornada jornada : jornadas) {
            if (jornada.getCuadrilla() == cuadrilla && jornada.getEstado() != EstadoJornada.CERRADA) {
                return "La cuadrilla ya tiene una jornada abierta.";
            }
        }
        Jornada jornada = new Jornada(siguienteIdJornada++, cuadrilla);
        jornadas.add(jornada);
        return "Jornada " + jornada.getId() + " creada.";
    }

    public String asignarOrden(int idJornada, String codigoOrden) {
        Jornada jornada = buscarJornada(idJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);
        if (jornada == null) return "No se encontró la jornada.";
        if (orden == null) return "No se encontró la orden.";
        if (jornada.getEstado() != EstadoJornada.EN_PREPARACION) return "La jornada no está en preparación.";
        if (orden.getEstado() != EstadoOrden.PENDIENTE) return "La orden no está pendiente.";
        for (Jornada otra : jornadas) {
            if (otra != jornada && otra.getEstado() != EstadoJornada.CERRADA && otra.contiene(orden)) {
                return "La orden ya está asignada a otra jornada abierta.";
            }
        }
        String error = jornada.validarAsignacion(orden);
        if (error != null) return error;
        jornada.asignar(orden);
        return null;
    }

    public String retirarOrden(int idJornada, String codigoOrden) {
        Jornada jornada = buscarJornada(idJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);
        if (jornada == null) return "No se encontró la jornada.";
        if (orden == null) return "No se encontró la orden.";
        if (jornada.getEstado() != EstadoJornada.EN_PREPARACION) return "Solo se retiran órdenes durante la preparación.";
        if (!jornada.retirar(orden)) return "La orden no está asignada a esa jornada.";
        return null;
    }

    public String iniciarJornada(int idJornada) {
        Jornada jornada = buscarJornada(idJornada);
        if (jornada == null) return "No se encontró la jornada.";
        return jornada.iniciar();
    }

    public String registrarResultado(int idJornada, String codigoOrden, boolean exito) {
        Jornada jornada = buscarJornada(idJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);
        if (jornada == null) return "No se encontró la jornada.";
        if (orden == null) return "No se encontró la orden.";
        return jornada.registrarResultado(orden, exito);
    }

    public OrdenServicio buscarOrden(String codigo) {
        if (codigo == null) return null;
        return ordenes.get(codigo.trim());
    }

    public Cuadrilla buscarCuadrilla(String codigo) {
        if (codigo == null) return null;
        return cuadrillas.get(codigo.trim());
    }

    public Jornada buscarJornada(int id) {
        for (Jornada jornada : jornadas) if (jornada.getId() == id) return jornada;
        return null;
    }

    public List<OrdenServicio> getOrdenes() {
        return Collections.unmodifiableList(new ArrayList<OrdenServicio>(ordenes.values()));
    }

    public List<Cuadrilla> getCuadrillas() {
        return Collections.unmodifiableList(new ArrayList<Cuadrilla>(cuadrillas.values()));
    }

    public List<Jornada> getJornadas() {
        return Collections.unmodifiableList(new ArrayList<Jornada>(jornadas));
    }

    public Reporte generarReporte() {
        return new Reporte(getOrdenes(), getJornadas());
    }

    private void cargarDatosIniciales() {
        agregarOrdenInicial(new Diagnostico("D-001", "Ana López", "Zona 10", 1.0, Zona.METROPOLITANA));
        agregarOrdenInicial(new Diagnostico("D-002", "Luis Pérez", "Mixco", 1.5, Zona.PERIFERICA));
        agregarOrdenInicial(new Instalacion("I-001", "Marta Díaz", "Zona 4", 2.5, Zona.METROPOLITANA, 5));
        agregarOrdenInicial(new Instalacion("I-002", "Pedro Solís", "Villa Nueva", 3.0, Zona.PERIFERICA, 2));
        agregarOrdenInicial(new ElectricaCertificada("E-001", "Rosa Méndez", "Zona 15", 3.0, Zona.METROPOLITANA, 4, 2000.0));
        agregarOrdenInicial(new ElectricaCertificada("E-002", "Mario Cifuentes", "San Cristóbal", 2.0, Zona.PERIFERICA, 3, 1500.0));
        agregarOrdenInicial(new MantenimientoAC("A-001", "Elena García", "Zona 13", 2.0, Zona.METROPOLITANA, 24000));
        agregarOrdenInicial(new MantenimientoAC("A-002", "Carlos Juárez", "Zona 1", 1.5, Zona.METROPOLITANA, 12000));

        registrarCuadrilla(new Cuadrilla("C-001", 8.0, true));
        registrarCuadrilla(new Cuadrilla("C-002", 7.0, false));
        registrarCuadrilla(new Cuadrilla("C-003", 6.0, true));
    }
}
