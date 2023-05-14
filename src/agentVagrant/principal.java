package agenteVagrant;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

import javax.swing.JOptionPane;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

public class principal {
	static int controle = 0;

	public static void main(String[] args) {
		int r0;
		int port = 10000;

		ServerSocket socket = null;

		Random gerador = new Random();
		try {
			socket = new ServerSocket(0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		port = socket.getLocalPort() + (gerador.nextInt(25) + 1);
		ProfileImpl p = new ProfileImpl();
		p.setParameter(Profile.MAIN_PORT, port + "");
		p.setParameter(Profile.CONTAINER_NAME, "Main-Container" + port);
		ContainerController cc = Runtime.instance().createMainContainer(p);
		PlatformController plataforma;
		JOptionPane.showMessageDialog(null, "SEJA BEM-VINDO \n SMA - SISTEMA MULTIAGENTE");
		String ler1 = JOptionPane.showInputDialog(null,
				"MENU MÁQUINA VIRTUAIS\n(1)Criar uma máquina\n(3)Sair do programa");
		r0 = Integer.parseInt(ler1);
		while (r0 != 3) {
			String ler2 = JOptionPane.showInputDialog(null,
					"MENU MÁQUINA VIRTUAIS\n(1)Criar uma máquina\n(2)Destruir Máquina\n(3)Sair do programa");
			r0 = Integer.parseInt(ler2);
			try {
				if (r0 == 1) {
					plataforma = cc.getPlatformController();
					AgentController AgenteIniciar = plataforma.createNewAgent("AgenteIniciar",
							"agenteVagrant.AgenteIniciar", null);
					AgenteIniciar.start();

					plataforma = cc.getPlatformController();
					AgentController AgenteMonitor = plataforma.createNewAgent("AgenteMonitor",
							"agenteVagrant.AgenteMonitor", null);
					AgenteMonitor.start();

					try {
						Thread.sleep(140000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				} else if (r0 == 2) {
					plataforma = cc.getPlatformController();
					AgentController AgenteDestroy = plataforma.createNewAgent("AgenteDestroy",
							"agenteVagrant.AgenteDestroy", null);
					AgenteDestroy.start();

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				} else if (r0 == 3) {
					JOptionPane.showMessageDialog(null, "SISTEMA ENCERRADO");
					System.exit(3);
				}

			} catch (ControllerException e1) {
				e1.printStackTrace();
			}
		}
	}

}
