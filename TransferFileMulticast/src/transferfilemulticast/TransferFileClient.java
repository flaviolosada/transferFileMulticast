package transferfilemulticast;

import java.io.*;
import java.net.*;
import javax.swing.JFileChooser;

class TransferFileClient {
	
	public static void main(String args[]) throws Exception
	{
		byte[] sendData = new byte[2048];
		byte ttl = (byte) 10;

		int porta = 5000;
		MulticastSocket clientSocket = new MulticastSocket();
		InetAddress endereco = InetAddress.getByName("227.55.77.99");
		clientSocket.joinGroup(endereco);
                File file = openFile();
                if (file != null) {
                    sendData = "[TEXT]".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, endereco, porta);
                    clientSocket.setTimeToLive(ttl);
                    clientSocket.send(sendPacket);
                    String serverName = receiveServerName();
                    clientSocket.leaveGroup(endereco);
                    clientSocket.close();
                    sendFileSocket(serverName, file);
                }
	}
        
        private static File openFile() {
            JFileChooser abreArquivo = new JFileChooser();
            abreArquivo.setAcceptAllFileFilterUsed(false);
            abreArquivo.setDialogType(JFileChooser.OPEN_DIALOG);
            if (abreArquivo.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File arquivo = abreArquivo.getSelectedFile();
                return arquivo;
            }
            return null;
        }
        
        private static String receiveServerName() {
            byte[] buf = new byte[1024];
            DatagramPacket request = null;
            try {
                DatagramSocket socket = new DatagramSocket(5005);
                request = new DatagramPacket (buf, buf.length);
                String result = "";
                while ("".equals(result.trim())) {
                    socket.receive(request);
                    result = new String(request.getData(),0,request.getLength());
                    if (!"".equals(result.trim())) {
                        return result.substring(4);
                    }
                }
                return "";
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            return "";
        }
        
        private static void sendFileSocket(String ipServer, File file) {
            Socket socket;
            byte[] sendData = new byte[1024];
            try {                
                /* Inicializacao dos fluxos de entrada e saida */
                if (file != null ) {
                    /* Inicializacao de socket TCP */
                    socket = new Socket(ipServer, 2500);
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    bis.read(sendData);
                    socket.getOutputStream().write(sendData);
                    bis.close();
                    socket.close();
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
}
