package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class HiloServidor extends Thread {
	DataInputStream fentrada;
	Socket socket = null;

	public HiloServidor(Socket s) {
		socket = s;
		try {
			// creo flujo de entrada
			fentrada = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("Error de E/S");
			e.printStackTrace();
		}
	}

	public void run() {
		ServidorChat.scrollpane1.getVerticalScrollBar().setValue(ServidorChat.scrollpane1.getVerticalScrollBar().getMaximum());
		ServidorChat.mensaje.setText("NUMERO DE CONEXIONES ACTUALES: " + ServidorChat.actuales);
		// nada mas conectarse el cliente le mando todos los mensajes
		String texto = ServidorChat.textarea.getText();
		EnviarMensajes(texto);
		while (true) {
			String cadena = "";
			try {
				cadena = fentrada.readUTF();// lee lo que el cliente escribe
				if (cadena.trim().equals("*")) {
					ServidorChat.actuales--;
					ServidorChat.mensaje.setText("NUMERO DE CONEXIONES ACTUALES: " + ServidorChat.actuales);
					break;
				}
				ServidorChat.textarea.append(cadena + "\n");
				texto = ServidorChat.textarea.getText();
				EnviarMensajes(texto);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

	private void EnviarMensajes(String texto) {
		int i;
		// recorremos tabla de sockets para enviar los mensajes
		for (i = 0; i < ServidorChat.conexiones; i++) {
			Socket s1 = ServidorChat.tabla[i];
			try {
				DataOutputStream fsalida = new DataOutputStream(s1.getOutputStream());
				fsalida.writeUTF(texto);
			} catch (SocketException se) {
				// esta excepcion ocurre cuando escribimos un socket de un cliente que ha
				// finalizado

			} catch (IOException e) {
				e.printStackTrace();

			}
		}
	}

}
