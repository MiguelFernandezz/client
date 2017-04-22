/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.sd.client;

/**
 *
 * @author Miguel Fernandez <miguel.fernandez@konecta.com.py>
 */
public class Respuesta {
    
    Integer estado;
    String mensaje;
    String file;

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Respuesta{" + "estado=" + estado + ", mensaje=" + mensaje + ", file=" + file + '}';
    }


    
    
}
