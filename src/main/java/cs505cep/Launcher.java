package cs505cep;

import cs505cep.CEP.CEPEngine;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;


public class Launcher {

    public static final int WEB_PORT = 9001;
    public static String inputStreamName = null;
    public static long accessCount = -1;


    public static CEPEngine cepEngine = null;

    public static void main(String[] args) throws IOException {


        System.out.println("Starting CEP...");
        //Embedded database initialization
        cepEngine = new CEPEngine();

        inputStreamName = "AccessStream";
        String inputStreamAttributesString = "remote_ip string, timestamp long";

        String outputStreamName = "CountStream";
        String outputStreamAttributesString = "count long";

        String queryString = " " +
                "from " + inputStreamName + "#window.timeBatch(10 sec) " +


                " select convert(avg(count()),'long') as count " +

                //" output snapshot every 1 sec " +

                " insert into " + outputStreamName + "; ";


        System.out.println(queryString);

        cepEngine.createCEP(inputStreamName, outputStreamName, inputStreamAttributesString, outputStreamAttributesString, queryString);

        System.out.println("CEP Started...");


        //Embedded HTTP initialization
        startServer();


        try {
            while (true) {
                Thread.sleep(5000);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void startServer() throws IOException {

        final ResourceConfig rc = new ResourceConfig()
        .packages("cs505cep.httpcontrollers");

        System.out.println("Starting Web Server...");
        URI BASE_URI = UriBuilder.fromUri("http://0.0.0.0/").port(WEB_PORT).build();
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);

        try {
            httpServer.start();
            System.out.println("Web Server Started...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
