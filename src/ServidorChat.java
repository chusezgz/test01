package chat;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServidorChat extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	static ServerSocket servidor;
	static final int puerto = 6001;
	static int conexiones = 0;
	static int actuales = 0;
	static int maximo = 10;

	static JTextField mensaje = new JTextField("");
	static JTextField mensaje2 = new JTextField("");
	static JScrollPane scrollpane1;
	static JTextArea textarea;
	JButton salir = new JButton("Salir");

	static Socket tabla[] = new Socket[10];

	// constructor
	public ServidorChat() {
		super(" VENTANA DEL SEVIDOR DE CHAT ");
		setLayout(null);
		mensaje.setBounds(10, 10, 400, 30);
		add(mensaje);
		mensaje.setEditable(false);
		mensaje2.setBounds(10, 348, 400, 30);
		add(mensaje2);
		mensaje2.setEditable(false);
		textarea = new JTextArea();
		scrollpane1 = new JScrollPane(textarea);

		scrollpane1.setBounds(10, 50, 400, 300);
		add(scrollpane1);
		salir.setBounds(420, 10, 100, 30);
		add(salir);

		textarea.setEditable(false);
		salir.addActionListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	// accion al pulsar el boton salir
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == salir) {
			try {
				servidor.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}
	}

	public static void main(String args[]) throws IOException {
		servidor = new ServerSocket(puerto);
		System.out.println("SERVIDOR INICIADO...");
		ServidorChat pantalla = new ServidorChat();
		pantalla.setBounds(0, 0, 540, 400);
		pantalla.setVisible(true);
		mensaje.setText("NUMERO DE CONEXIONES ACTUALES: " + 0);

		// se admiten hasta 10 conexiones
		while (conexiones < maximo) {
			Socket s = new Socket();
			try {
				s = servidor.accept();// se espera al cliente
			} catch (SocketException ns) {
				// pasa por aqui si se pulsa el botno salir
				// no se ejecuta todo el bucle
				break;
			}

			tabla[conexiones] = s;
			conexiones++;
			actuales++;
			HiloServidor hilo = new HiloServidor(s);
			hilo.start();
		}
		if (!servidor.isClosed()) {
			try {
				// sale cuando llega al maximo de conexiones
				mensaje2.setForeground(Color.red);
				mensaje2.setText("MAXIMO Nº DE CONEXIONES ESTABLECIDAS: " + conexiones);
				servidor.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println("SERVIDOR FINALIZADO");
	}

}
