public class MantenimientoAC extends OrdenServicio {
    private static final double TARIFA_BASE_METROPOLITANA = 100.0; // Suposición: el enunciado no especifica otra tarifa.
    private final int capacidadBTU;

    public MantenimientoAC(String codigo, String cliente, String direccion,
                           double horas, Zona zona, int capacidadBTU) {
        super(codigo, cliente, direccion, horas, zona);
        if (zona != Zona.METROPOLITANA) {
            throw new IllegalArgumentException("El mantenimiento de aire acondicionado solo se acepta en zona metropolitana.");
        }
        if (capacidadBTU < 9000 || capacidadBTU > 60000) {
            throw new IllegalArgumentException("La capacidad debe estar entre 9000 y 60000 BTU.");
        }
        this.capacidadBTU = capacidadBTU;
    }

    @Override
    public double calcularCosto() {
        return TARIFA_BASE_METROPOLITANA + getHorasEstimadas() * 110.0 + 80.0;
    }

    @Override
    public int diasEstimados() { return 2; }

    @Override
    public int maxVisitas() { return 1; }

    @Override
    public CategoriaServicio categoria() { return CategoriaServicio.MANTENIMIENTO_AC; }

    @Override
    public boolean compatible(Cuadrilla cuadrilla) {
        return cuadrilla != null && cuadrilla.estaCertificadaRefrigeracion();
    }

    public int getCapacidadBTU() { return capacidadBTU; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Equipo: %d BTU", capacidadBTU);
    }
}
