package cl.duoc.unheladito.entity;

public enum RolUsuario {
    CLIENTE("Cliente"),
    VENDEDOR("Vendedor"),
    ADMINISTRADOR("Administrador");

    private final String etiqueta;
    RolUsuario(String etiqueta) {
        this.etiqueta = etiqueta;
    }
    public String getEtiqueta() {
        return etiqueta;
    }
}
