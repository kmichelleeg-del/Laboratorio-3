public class Instalacion extends OrdenServicio {
    private final int puntos;

    public Instalacion(String codigo, String cliente, String direccion,
                       double horas, Zona zona, int puntos) {
        super(codigo, cliente, direccion, horas, zona);
        if (puntos <= 0) {
            throw new IllegalArgumentException("La cantidad de puntos debe ser un entero positivo.");
        }
        this.puntos = puntos;
    }

    public int horasFacturables() {
        double horasPorPuntos = puntos * 0.75;
        return (int) Math.ceil(Math.max(getHorasEstimadas(), horasPorPuntos));
    }

    protected double tarifaBase() {
        return getZona() == Zona.METROPOLITANA ? 100.0 : 175.0;
    }

    @Override
    public double calcularCosto() {
        return tarifaBase() + horasFacturables() * 90.0;
    }

    @Override
    public int diasEstimados() {
        return getZona() == Zona.METROPOLITANA ? 3 : 5;
    }

    @Override
    public int maxVisitas() { return 3; }

    @Override
    public CategoriaServicio categoria() { return CategoriaServicio.INSTALACION; }

    public int getPuntos() { return puntos; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Puntos: %d | Horas facturables: %d", puntos, horasFacturables());
    }
}
