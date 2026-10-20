public class ElectricaCertificada extends Instalacion {
    private final double valorMateriales;

    public ElectricaCertificada(String codigo, String cliente, String direccion,
                                double horas, Zona zona, int puntos, double valorMateriales) {
        super(codigo, cliente, direccion, horas, zona, puntos);
        if (!Double.isFinite(valorMateriales) || valorMateriales < 0) {
            throw new IllegalArgumentException("El valor de materiales no puede ser negativo.");
        }
        this.valorMateriales = valorMateriales;
    }

    @Override
    public double calcularCosto() {
        return super.calcularCosto() + 120.0 + valorMateriales * 0.05;
    }

    @Override
    public int diasEstimados() { return super.diasEstimados() + 2; }

    @Override
    public int maxVisitas() { return 2; }

    @Override
    public CategoriaServicio categoria() { return CategoriaServicio.ELECTRICA_CERTIFICADA; }

    public double getValorMateriales() { return valorMateriales; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Materiales: Q%.2f", valorMateriales);
    }
}
