/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transferfilemulticast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author usuario
 */
public class TransferFileServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String serverType = "TEXT";//args[0];
        int porta = 5000;
        String ip = "230.55.77.99";
        MulticastSocket socket = new MulticastSocket(porta);
        InetAddress endereco = InetAddress.getByName(ip);
        socket.joinGroup(endereco);
        
        while(true) {
       	      byte[] recvData = new byte[1024];
              DatagramPacket recvPacket;
	      recvPacket = new DatagramPacket (recvData,
			      recvData.length);
              socket.receive (recvPacket);
	      
              String sentence;
	      sentence = new String(recvPacket.getData());
	      System.out.print(recvPacket.getAddress().toString() +": ");
              System.out.println (sentence);            
              if (serverType.equals(sentence.trim())) {                 
                sendMyName(recvPacket.getAddress().toString().substring(1));  
                startSocket(recvPacket.getAddress().toString().substring(1));                      
              }
	      sentence= null;
	      recvPacket = null;
        }
        
    }
    
    public static void sendMyName(String ip) {
        DatagramSocket socket = null;
        DatagramPacket request = null;
        DatagramPacket reply = null;
        int serverPort = 5005;
        byte[] buf = new byte[1024];
        
        try {
            /* Pegar parametros */
            String mensagemEnviar = "IP: " + InetAddress.getLocalHost().getHostAddress();

        
            /* Inicializacao de sockets UDP com Datagrama */
            socket = new DatagramSocket();

            /* Configuracao a partir dos parametros */
            InetAddress host = InetAddress.getByName(ip);
            byte[] m = mensagemEnviar.getBytes();

            /* Criacao do Pacote Datagrama para Envio */
            request = new DatagramPacket(m, m.length, host, serverPort);

            /* Envio propriamente dito */
            socket.send(request);

            /* Preparacao do Pacote Datagrama para Recepcao */
            /* Finaliza tudo */
            socket.close ();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    public static void startSocket(String ip) {
        Integer numeroPorta;
        ServerSocket serverSocket;
        Socket clientSocket;
        PrintWriter out;
        BufferedReader in;
        String comando;

        /* Parametros */
        numeroPorta = 6000;

        try {
            /* Inicializacao do server socket TCP */
            serverSocket = new ServerSocket(numeroPorta);
            while (true){
                /* Espera por um cliente */
                clientSocket = serverSocket.accept();
                System.out.println ("Novo cliente: "+serverSocket.toString());

                /* Preparacao dos fluxos de entrada e saida */
                out = new PrintWriter(clientSocket.getOutputStream(),
                                true);
                in = new BufferedReader(new InputStreamReader(
                                clientSocket.getInputStream()));

                /* Recuperacao dos comandos */
                while ((comando = in.readLine()) != null) {	
                        System.out.println ("Comando recebido: ["+ comando+"]");
                        /* Se comando for "HORA" */
                        if (comando.equals ("HORA")){
                                /* Prepara a hora para envio */
                                String hora = new SimpleDateFormat("d MMM yyyy HH:mm:ss").format(new Date ());
                                /* Escreve na saida a 'hora' */
                                out.println (hora);
                        }else if (comando.equals ("FIM")){
                                break;
                        }else{
                                out.println ("Comando Desconhecido");
                        }
                }
                /* Finaliza tudo */
                System.out.print ("Cliente desconectando... ");
                out.close();
                in.close();
                clientSocket.close();
                System.out.println ("ok");
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
}
