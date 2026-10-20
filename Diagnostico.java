public class Diagnostico extends OrdenServicio {
    public Diagnostico(String codigo, String cliente, String direccion,
                       double horas, Zona zona) {
        super(codigo, cliente, direccion, horas, zona);
        if (horas > 1.5) {
            throw new IllegalArgumentException("Un diagnóstico no puede superar 1.5 horas.");
        }
    }

    @Override
    public double calcularCosto() {
        return getZona() == Zona.METROPOLITANA ? 150.0 : 225.0;
    }

    @Override
    public int diasEstimados() {
        return getZona() == Zona.METROPOLITANA ? 1 : 2;
    }

    @Override
    public int maxVisitas() { return 3; }

    @Override
    public CategoriaServicio categoria() { return CategoriaServicio.DIAGNOSTICO; }
}
