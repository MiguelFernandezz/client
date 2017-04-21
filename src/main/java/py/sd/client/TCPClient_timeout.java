package py.sd.client;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

public class TCPClient_timeout {
    
    
    private final static Logger logger = Logger.getLogger(TCPClient_timeout.class);

    public static void main(String[] args) throws Exception {
        
        PropertyConfigurator.configure("log4j.properties");
        

        String ipServidor = args[0];
        Integer puerto = Integer.parseInt(args[1]);
        Integer cantidad = Integer.parseInt(args[2]);
        String archivo = args[3];
        Integer tramas = 1;
        
        Gson gson = new Gson();

        Socket kkSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        int TimeOutConexion = 7000; //milisegundos
        int TimeOutRecepcion = 7000; //milisegundos
        long ini = 0;
        long fin = 0;
        String direccion = "";

        try {

            SocketAddress sockaddr = new InetSocketAddress(ipServidor, puerto);
            kkSocket = new Socket();

            ini = System.currentTimeMillis();
            logger.info("conectando con: "+ipServidor+":"+puerto);
            kkSocket.connect(sockaddr, TimeOutConexion);
            kkSocket.setSoTimeout(TimeOutRecepcion);
            
            direccion = kkSocket.getLocalAddress()+":"+kkSocket.getLocalPort();
            
            

            // enviamos nosotros
            out = new PrintWriter(kkSocket.getOutputStream(), true);

            //viene del servidor
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
            
            logger.info("conectado");
        } catch (SocketTimeoutException e) {
            logger.error("Fallo de Timeout de conexion en " + TimeOutConexion);
            fin = System.currentTimeMillis();
            System.err.println("Fallo de Timeout de conexion en " + TimeOutConexion);
            System.err.println("Duracion " + (fin - ini));
            System.exit(1);
        } catch (UnknownHostException e) {
            logger.error("Host desconocido");
            System.err.println("Host desconocido");
            System.exit(1);
        } catch (IOException e) {
            logger.error("Error de I/O en la conexion al host");
            System.err.println("Error de I/O en la conexion al host");
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;

        try {
            int i = 0;
            while ((fromServer = in.readLine()) != null) {
                logger.info("Servidor: " + fromServer);
                Respuesta resp = gson.fromJson(fromServer, Respuesta.class);

                if (resp.getEstado().equals(0)) {
                    break;
                }
                if (i < cantidad) {
                    File file = new File(archivo);
                    byte[] bytes = loadFile(file);
                    byte[] encoded = Base64.encodeBase64(bytes);
                    String encodedString = new String(encoded);
                    encodedString = Base64.encodeBase64String(bytes);
                    Objeto o = new Objeto();
                    o.setFile(encodedString);
                    o.setDireccion(direccion);
                    
                    fromUser = gson.toJson(o);

                } else {
                    fromUser = "Bye";
                }

                if (fromUser != null) {

                    //escribimos al servidor
                    out.println(fromUser);
                }
                i++;
            }
        } catch (SocketTimeoutException exTime) {
            logger.error("Tiempo de espera agotado para recepcion de datos del servidor ");
        }

        out.close();
        in.close();
        stdIn.close();
        kkSocket.close();
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }
}
