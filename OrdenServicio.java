public abstract class OrdenServicio {
    private static final double CARGO_VISITA_ADICIONAL = 40.0;

    private final String codigo;
    private final String nombreCliente;
    private final String direccion;
    private final double horasEstimadas;
    private final Zona zona;
    private EstadoOrden estado;
    private int visitas;
    private double montoRegistro;
    private double cargoVisitas;
    private boolean registrada;

    protected OrdenServicio(String codigo, String nombreCliente, String direccion,
                            double horasEstimadas, Zona zona) {
        this.codigo = textoRequerido(codigo, "El código de la orden");
        this.nombreCliente = textoRequerido(nombreCliente, "El nombre del cliente");
        this.direccion = textoRequerido(direccion, "La dirección");
        if (!Double.isFinite(horasEstimadas) || horasEstimadas <= 0) {
            throw new IllegalArgumentException("Las horas estimadas deben ser mayores que cero.");
        }
        if (zona == null) {
            throw new IllegalArgumentException("Debe seleccionar una zona.");
        }
        this.horasEstimadas = horasEstimadas;
        this.zona = zona;
        this.estado = EstadoOrden.PENDIENTE;
        this.visitas = 0;
        this.montoRegistro = 0.0;
        this.cargoVisitas = 0.0;
        this.registrada = false;
    }

    private static String textoRequerido(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return valor.trim();
    }

    public abstract double calcularCosto();

    public abstract int diasEstimados();

    public abstract int maxVisitas();

    public abstract CategoriaServicio categoria();

    public boolean compatible(Cuadrilla cuadrilla) {
        return cuadrilla != null;
    }

    void registrarCobroInicial() {
        if (registrada) {
            throw new IllegalStateException("La orden ya fue registrada.");
        }
        montoRegistro = calcularCosto();
        registrada = true;
    }

    void agregarOrdenInicialSinIngreso() {
        registrada = true;
        montoRegistro = 0.0;
    }

    double iniciarAtencion() {
        if (estado != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException("Solo se puede iniciar una orden pendiente.");
        }
        visitas++;
        double cargo = visitas > 1 ? CARGO_VISITA_ADICIONAL : 0.0;
        cargoVisitas += cargo;
        estado = EstadoOrden.EN_ATENCION;
        return cargo;
    }

    void registrarResultado(boolean exito) {
        if (estado != EstadoOrden.EN_ATENCION) {
            throw new IllegalStateException("La orden no está en atención.");
        }
        if (exito) {
            estado = EstadoOrden.COMPLETADA;
        } else if (visitas >= maxVisitas()) {
            estado = EstadoOrden.CANCELADA;
        } else {
            estado = EstadoOrden.PENDIENTE;
        }
    }

    public String getCodigo() { return codigo; }
    public String getNombreCliente() { return nombreCliente; }
    public String getDireccion() { return direccion; }
    public double getHorasEstimadas() { return horasEstimadas; }
    public Zona getZona() { return zona; }
    public EstadoOrden getEstado() { return estado; }
    public int getVisitas() { return visitas; }
    public double getMontoRegistro() { return montoRegistro; }
    public double getCargoVisitas() { return cargoVisitas; }
    public boolean estaRegistrada() { return registrada; }

    @Override
    public String toString() {
        return String.format("%s | %s | Cliente: %s | Zona: %s | %.2f h | Estado: %s | Visitas: %d",
                codigo, categoria().getNombre(), nombreCliente, zona, horasEstimadas, estado, visitas);
    }
}
