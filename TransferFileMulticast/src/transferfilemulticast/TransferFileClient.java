package transferfilemulticast;

import java.io.*;
import java.net.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

class TransferFileClient {
	
	public static void main(String args[]) throws Exception
	{
		byte[] sendData = new byte[2048];
		byte ttl = (byte) 10;

		int porta = 5000;
		MulticastSocket clientSocket = new MulticastSocket();
		InetAddress endereco = InetAddress.getByName("230.55.77.99");
		clientSocket.joinGroup(endereco);
		                
                
                File arquivo = abrirArquivo();
                if (arquivo != null ) {
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(arquivo));

                    bis.read(sendData, 0, sendData.length);
                       
                    sendData = "TEXT".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, endereco, porta);
                    clientSocket.setTimeToLive(ttl);
                    clientSocket.send(sendPacket);
                    String serverName = receiveServerName();
                    JOptionPane.showMessageDialog(null, serverName);
    //		while (true) {
    //			System.out.print ("Mensagem: ");
    //			String sentence = stdIn.readLine();
    //			if (sentence == null) break;
    //			sendData = sentence.getBytes();
    //			DatagramPacket sendPacket;
    //			sendPacket = new DatagramPacket (sendData,
    //					sendData.length, endereco, porta);
    //			clientSocket.setTimeToLive (ttl);
    //			clientSocket.send (sendPacket);
    //			sentence = null;
    //			sendPacket = null;
    //		}

                    clientSocket.leaveGroup(endereco);
                    clientSocket.close();
                }
	}
        
        private static File abrirArquivo() {
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
}
