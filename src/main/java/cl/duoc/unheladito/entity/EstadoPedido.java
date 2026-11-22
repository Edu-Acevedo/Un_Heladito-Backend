package cl.duoc.unheladito.entity;

public enum EstadoPedido {

    APROBADO_SIMULADO("Aprobado (simulado)"),
    RECHAZADO_SIMULADO("Rechazado (simulado)");

    private final String etiqueta;

    EstadoPedido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
