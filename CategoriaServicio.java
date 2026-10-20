public enum CategoriaServicio {
    DIAGNOSTICO("Diagnóstico"),
    INSTALACION("Instalación"),
    ELECTRICA_CERTIFICADA("Instalación eléctrica certificada"),
    MANTENIMIENTO_AC("Mantenimiento de aire acondicionado");

    private final String nombre;

    CategoriaServicio(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
