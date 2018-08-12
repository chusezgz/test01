package chat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClienteChat extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	Socket socket = null;
	// streams
	DataInputStream fentrada;
	DataOutputStream fsalida;
	String nombre;
	static JTextField mensaje = new JTextField();
	private JScrollPane scrollpane1;
	static JTextArea textarea1;
	JButton boton = new JButton("Enviar");
	JButton desconectar = new JButton("Salir");
	boolean repetir = true;

	// constructor
	public ClienteChat(Socket s, String nombre) {
		super("CONEXION DEL CLIENTE CHAT " + nombre);
		setLayout(null);
		mensaje.setBounds(10, 10, 400, 30);
		add(mensaje);
		textarea1 = new JTextArea();
		scrollpane1 = new JScrollPane(textarea1);
		scrollpane1.setBounds(10, 50, 400, 300);
		scrollpane1.getVerticalScrollBar().setValue(scrollpane1.getVerticalScrollBar().getMaximum());
		add(scrollpane1);
		boton.setBounds(420, 10, 100, 30);
		add(boton);
		desconectar.setBounds(420, 50, 100, 30);
		add(desconectar);
		textarea1.setEditable(false);
		boton.addActionListener(this);
		desconectar.addActionListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		socket = s;
		this.nombre = nombre;
		try {
			fentrada = new DataInputStream(socket.getInputStream());
			fsalida = new DataOutputStream(socket.getOutputStream());
			String texto = "> Entrada en el chat..." + nombre;
			fsalida.writeUTF(texto);
		} catch (IOException e) {
			System.out.println("Error de E/S");
			e.printStackTrace();
			System.exit(0);
		}
	}

	// AÒado acciÛn en el 'JTextField mensaje': Envia mensaje cuando pulsamos enter
	// y hay algo escrito. (Esta acciÛn se aÒade al JTextField al comienzo del
	// metodo ejecutar())

	Action action = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String texto = nombre + "> " + mensaje.getText();
			// limpio area de mensaje
			if (!mensaje.getText().equals("")) {
				try {
					fsalida.writeUTF(texto);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			mensaje.setText("");
		}
	};

	// cuando pulsamos botones
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == boton) {// se pulsa el boton ENVIAR
			String texto = nombre + "> " + mensaje.getText();
			try {
				// limpio area de mensaje
				if (!mensaje.getText().equals("")) {
					fsalida.writeUTF(texto);
				}
				mensaje.setText("");

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if (e.getSource() == desconectar) {// se pulsa el botno salir
			String texto = " > abandona el chat ... " + nombre;
			try {
				fsalida.writeUTF(texto);
				fsalida.writeUTF("*");
				repetir = false;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void ejecutar() {
		String texto = "";
		mensaje.addActionListener(action);
		while (repetir) {
			try {
				texto = fentrada.readUTF();
				textarea1.setText(texto);
			} catch (IOException e) {
				// este error sale cuando el servidorn se cierra
				JOptionPane.showMessageDialog(null, "IMPOSIBLE CONECTAR CON EL SERVIDOR", e.getMessage(),
						JOptionPane.ERROR_MESSAGE);
				// null, "IMPOSIBLE CONECTAR CON EL SERVIDOR", e.getMessage(), "<< MENSAJE DE
				// ERROR:2 >>", JOptionPane.ERROR_MESSAGE
				repetir = false;// salir del bucle
			}
		}
		try {
			socket.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		int puerto = 6001;
		String nombre = JOptionPane.showInputDialog("Introduce tu nombre o nick:");
		Socket s = null;
		try {
			// cliente y servidor se ejecuan en maquina local
			s = new Socket("192.168.1.37", puerto);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "IMPOSIBLE CONECTAR CON EL SERVIDOR", e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		if (!nombre.trim().equals("")) {
			ClienteChat cliente = new ClienteChat(s, nombre);
			cliente.setBounds(0, 0, 540, 400);
			cliente.setVisible(true);
			cliente.ejecutar();
		} else {
			errorName(s);
			System.out.println("El nombre esta vac√≠o");
		}
	}

	public static void errorName(Socket s) {
		for (int i = 0; i < 2; i++) {
			JOptionPane.showMessageDialog(null,
					"No has introducido ning˙n nombre, vuelve a intentarlo, tienes " + (2 - i) + " intento/s m·s");
			String nombre = JOptionPane.showInputDialog("Introduce tu nombre o nick:");
			if (!nombre.trim().equals("")) {
				ClienteChat cliente = new ClienteChat(s, nombre);
				cliente.setBounds(0, 0, 540, 400);
				cliente.setVisible(true);
				cliente.ejecutar();
				break;
			}
		}
	}

}
