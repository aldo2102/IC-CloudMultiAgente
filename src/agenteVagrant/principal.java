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
		// TODO Auto-generated method stub
		int r0;
		int port = 10000;

		ServerSocket socket = null;

		Random gerador = new Random();
		try {
			socket = new ServerSocket(0);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		port = socket.getLocalPort() + (gerador.nextInt(25) + 1);
		ProfileImpl p = new ProfileImpl();
		p.setParameter(Profile.MAIN_PORT, port + "");
		p.setParameter(Profile.CONTAINER_NAME, "Main-Container" + port);
		ContainerController cc = Runtime.instance().createMainContainer(p);
		PlatformController plataforma;
		// System.out.println("MENU M�QUINA VIRTUAIS");
		// System.out.println("(2)Criar uma m�quina");
		// System.out.println("(1) Destruir uma m�quina");

		JOptionPane.showMessageDialog(null, "SEJA BEM-VINDO \n SMA - SISTEMA MULTIAGENTE");
		String ler1 = JOptionPane.showInputDialog(null,
				"MENU M�QUINA VIRTUAIS\n(1)Criar uma m�quina\n(0)Sair do programa");
		r0 = Integer.parseInt(ler1);

		// r0 = 1;
		while (r0 != 0) {
			if (controle > 0) {
				String ler2 = JOptionPane.showInputDialog(null,
						"MENU M�QUINA VIRTUAIS\n(1)Criar uma m�quina\n(2)Destruir M�quina\n(0)Sair do programa");
				r0 = Integer.parseInt(ler2);
			}
			/*
			 * System.out.println("(0)Sair do programa");
			 * Scanner ler =new Scanner (System.in);
			 * r0=ler.nextInt();
			 */

			try {
				switch (r0) {
					case 1:
						plataforma = cc.getPlatformController();
						AgentController AgenteIniciar = plataforma.createNewAgent("AgenteIniciar",
								"agenteVagrant.AgenteIniciar", null);
						AgenteIniciar.start();

						plataforma = cc.getPlatformController();
						AgentController AgenteMonitor = plataforma.createNewAgent("AgenteMonitor",
								"agenteVagrant.AgenteMonitor", null);
						// criar agente
						AgenteMonitor.start();

						try {
							Thread.sleep(140000);
						} catch (InterruptedException e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						}
						break;
					case 2:
						plataforma = cc.getPlatformController();
						AgentController AgenteDestroy = plataforma.createNewAgent("AgenteDestroy",
								"agenteVagrant.AgenteDestroy", null);
						// criar agente
						AgenteDestroy.start();

						try {
							Thread.sleep(10000);
						} catch (InterruptedException e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						}
						break;
					case 0:
						JOptionPane.showMessageDialog(null, "SISTEMA ENCERRADO");

						break;
				}
				System.out.println("fim");
			} catch (ControllerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
