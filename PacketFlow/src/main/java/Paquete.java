public class Paquete implements Comparable<Paquete> {
    private int idMensaje;
    private int numero;
    private int cantPaquetes;
    private String contenido;
    private EstadoPaquete estado;
    private Mensaje mensaje;

    //Constructor
    public Paquete(int idMensaje, int numero, int cantPaquetes, String contenido, EstadoPaquete estado, Mensaje mensaje) {
        this.idMensaje    = idMensaje;
        this.numero       = numero;
        this.cantPaquetes = cantPaquetes;
        this.contenido    = contenido;
        this.estado       = estado;
        this.mensaje      = mensaje;
    }

    // Getters
    public int getIdMensaje()        { return idMensaje; }
    public int getNumero()           { return numero; }
    public int getCantPaquetes()     { return cantPaquetes; }
    public String getContenido()     { return contenido; }
    public EstadoPaquete getEstado() { return estado; }
    public Mensaje getMensaje()      { return mensaje; }

    // Setters
    public void setIdMensaje(int idMensaje)            { this.idMensaje    = idMensaje; }
    public void setNumero(int numero)                  { this.numero       = numero; }
    public void setCantPaquetes(int cantPaquetes)      { this.cantPaquetes = cantPaquetes; }
    public void setContenido(String contenido)         { this.contenido    = contenido; }
    public void setEstado(EstadoPaquete estado)        { this.estado       = estado; }
    public void setMensaje(Mensaje mensaje)            { this.mensaje      = mensaje; }

    @Override
    public int compareTo(Paquete otro) {
        return otro.getMensaje().getPrioridad() - this.getMensaje().getPrioridad();
    }

}
