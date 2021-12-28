import java.util.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.JLabel;


public class Gioco extends JFrame {

	private JPanel contentPane;
	
	private ImageIcon icona1 = caricaIcona("icona1.png");
	private ImageIcon icona2 = caricaIcona("icona2.png");
	private ImageIcon icona3 = caricaIcona("icona3.png");
	
        //Timer per gestire la discesa del dischetto
	private Timer timer;
	private int riga = 0; //riga attuale del timer
	
        //Variabili che mi servono per gestire la funzione verificaVittoria
	private int giocatore;
	private boolean partitaFinita;
	
	private JButton casella;
	private int rig; //riga attuale
	private int col;
	
	private JButton rigioca;
	
	private JButton[][] griglia = new JButton[6][7];
        
        //Contatori del punteggio con i relativi nomi
	private JLabel contRosso;
	private JLabel contGiallo;
        private JLabel giallo;
        private JLabel rosso;
        public JLabel getGiallo() {
            return giallo;
        }
        public JLabel getRosso() {
            return rosso;
        }
        
        private JButton tornaMenu;
        
        static Menu menu = new Menu();
        static Gioco gioco = new Gioco();
       
	
	public Gioco() {
		//Costruzione del frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 710, 414);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setForeground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setResizable(false);
		
		//Costruisco la griglia facendo una matrice di bottoni
		int orizz = 0;
		int vertic = 0;
		for (int i = 0; i < 6; i++) {
			vertic = 0;
			for (int j = 0; j < 7 ; j++) {
				JButton casella = new JButton("");
				casella.setToolTipText("libera");
				casella.setMnemonic(j);
				casella.setIcon(icona1);
				casella.setBounds(vertic, orizz, 64, 64);
				vertic += 64;
				contentPane.add(casella);
				griglia[i][j] = casella;
				griglia[i][j].addActionListener(new turno());
			}
			orizz += 64;
		}
		
		//Bottone per la funzione che fa ripartire una nuova partita
		rigioca = new JButton();
		rigioca.setBorder(new EmptyBorder(0, 0, 0, 0));
		rigioca.setBounds(469, 227, 221, 147);
		contentPane.add(rigioca);
		rigioca.setIcon(new ImageIcon(Gioco.class.getResource("rigioca3.png")));
		rigioca.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rigioca.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < 6; i++) {
					for (int j = 0; j < 7 ; j++) {
						griglia[i][j].setIcon(icona1);
						setStato(griglia[i][j],"libera");
					}
				}
				partitaFinita = false;
			}
		});
		
                //Bottone per tornare alla scelta dei nomi
		tornaMenu = new JButton("Menu");
		tornaMenu.setBorder(new EmptyBorder(0, 0, 0, 0));
		tornaMenu.setBounds(469, 11, 221, 67);
		contentPane.add(tornaMenu);
		tornaMenu.setBackground(UIManager.getColor("Button.background"));
		tornaMenu.setForeground(Color.BLACK);
		tornaMenu.setFont(new Font("Tahoma", Font.PLAIN, 28));
                tornaMenu.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        gioco.setVisible(false);
                        gioco = new Gioco();
                        gioco.setVisible(false);
                        Menu menu = new Menu();
                        menu.setVisible(true);
                    }
                });
		
                //Nomi e contatori punteggi
		giallo = new JLabel();
		giallo.setBounds(495, 99, 46, 14);
		contentPane.add(giallo);
		rosso = new JLabel();
		rosso.setBounds(603, 99, 46, 14);
		contentPane.add(rosso);
		contGiallo = new JLabel("0");
		contGiallo.setHorizontalAlignment(SwingConstants.CENTER);
		contGiallo.setFont(new Font("Tahoma", Font.PLAIN, 30));
		contGiallo.setBounds(483, 138, 58, 60);
		contentPane.add(contGiallo);
		contRosso = new JLabel("0");
		contRosso.setHorizontalAlignment(SwingConstants.CENTER);
		contRosso.setFont(new Font("Tahoma", Font.PLAIN, 30));
		contRosso.setBounds(591, 138, 58, 60);
		contentPane.add(contRosso);
		
		//Timer per l'animazione della pedina, a cui reagisce l'ascoltatore aggiungiPedina
		timer = new Timer(62,new aggiungiPedina());
		timer.setRepeats(true);
		
	}//Fine Costruttore
	
	private ImageIcon caricaIcona(String nomeFile) {	
		return new ImageIcon(getClass().getResource(nomeFile));
	}
	
	public int getColonna(JButton casella) {
		return casella.getMnemonic();
	}
	
        //Il tooltiptext mi dice se la casella é libera o meno
	public String getStato(JButton casella) {
		return casella.getToolTipText();
	}
	
	public void setStato(JButton casella,String stato) {
		casella.setToolTipText(stato);
	}
	
        //Vede qual'é la prima riga libera nella colonna selezionata, se non ce ne sono torna -1 
	private int selezionaRiga(int colonna) {
		if (getStato(griglia[0][colonna]).equals("gialla") || getStato(griglia[0][colonna]).equals("rossa")) return -1;
		for(int i = 5; i > -1; i--) 
			if (getStato(griglia[i][colonna]).equals("libera")) return i;
		return -1;
	}
	
	//Ascoltatori
	private class turno implements ActionListener 
	{   public void actionPerformed(ActionEvent e)
		{
		casella = (JButton)e.getSource();
		col = getColonna(casella);
		rig = selezionaRiga(col);
		if(rig >= 0) timer.start();
		}
	}
	
	private class aggiungiPedina implements ActionListener
	{	public void actionPerformed(ActionEvent e) 
		{
		
		//Giocatore rosso
		if(giocatore % 2 == 1) {
			
                    //Devo aggiornare l'icona soltanto della riga piú in alto
			if(rig == 0) {
				griglia[0][col].setIcon(icona2);
				setStato(griglia[rig][col],"rossa");
				timer.stop();
				vittoriaRossa();
				riga = -1;
				giocatore += 1;	
			}
			//Il timer richiamerá questo ramo finché non arriviamo alla casella dopo la quale ce n'é una giá occupata	
			if(rig > 0) {
				
				if(riga == 0) {
					griglia[0][col].setIcon(icona2);
				}
				
				else if(riga < rig && riga > 0) {
					griglia[riga-1][col].setIcon(icona1);
					griglia[riga][col].setIcon(icona2);
				}
				
				else if(riga == rig) {
					griglia[riga-1][col].setIcon(icona1);
					griglia[riga][col].setIcon(icona2);
					setStato(griglia[rig][col],"rossa");
					riga = -1;
					timer.stop();
					vittoriaRossa();
					giocatore += 1;
				}
				riga++; //riga viene incrementata per riempire ogni 62ms la casella successiva e svuotare quella attuale
			}
		}//Fine ramo if giocatore rosso
		
		//Giocatore giallo
		else {
			
			if(rig == 0) {
				griglia[0][col].setIcon(icona3);
				setStato(griglia[rig][col],"gialla");
				timer.stop();
				vittoriaGialla();
				riga = -1;
				giocatore += 1;	
			}
				
			if(rig > 0) {
				
				if(riga == 0) {
					griglia[0][col].setIcon(icona3);
				}
				
				else if(riga < rig && riga > 0) {
					griglia[riga-1][col].setIcon(icona1);
					griglia[riga][col].setIcon(icona3);
				}
				
				else if(riga == rig) {
					griglia[riga-1][col].setIcon(icona1);
					griglia[riga][col].setIcon(icona3);
					setStato(griglia[rig][col],"gialla");
					riga = -1;
					timer.stop();
					vittoriaGialla();
					giocatore += 1;
				}
				riga++;
			}
		}//Fine ramo else giocatore giallo
		
		}
	
	} //Fine ascoltatore
        
        //Verifico lo stato della partita con questi due metodi
        private void vittoriaGialla(){
            if(verificaVittoria() == "gialla" && giocatore % 2 == 0 && partitaFinita == false) {
                partitaFinita = true;
		Integer valore = Integer.parseInt(contGiallo.getText()) + 1;
		contGiallo.setText(valore.toString());
            }
        }
        
        private void vittoriaRossa(){
            if (verificaVittoria().equals("rossa") && giocatore % 2 == 1 && partitaFinita == false) {
		partitaFinita = true;
		Integer valore = Integer.parseInt(contRosso.getText()) + 1;
		contRosso.setText(valore.toString());
            }
        }
	
	//Logica del gioco
	private String verificaVittoria() {
		
		int n = griglia.length;
		int m = griglia[0].length;
		
	    //Orizzontale 
	    for (int i = n-1 ; i > -1; i--)
	        for (int j = 0; j < m-3; j++) {
	            String[] v_temp = new String[4];
	            v_temp[0] = getStato(griglia[i][j]);
	            v_temp[1] = getStato(griglia[i][j+1]);
	            v_temp[2] = getStato(griglia[i][j+2]);
	            v_temp[3] = getStato(griglia[i][j+3]);
	            List<String> v_stati = Arrays.asList(v_temp);
	            if (v_stati.contains("gialla") && !(v_stati.contains("libera")) && !(v_stati.contains("rossa"))) return "gialla";
	            else if(v_stati.contains("rossa") && !(v_stati.contains("libera")) && !(v_stati.contains("gialla"))) return "rossa";
	        }
	    
	    
	  //Verticale
	    for (int j = 0 ; j < m; j++)
	        for (int i = n-1; i > n-4; i--) {
	            String[] v_temp = new String[4];
	            v_temp[0] = getStato(griglia[i][j]);
	            v_temp[1] = getStato(griglia[i-1][j]);
	            v_temp[2] = getStato(griglia[i-2][j]);
	            v_temp[3] = getStato(griglia[i-3][j]);
	            List<String> v_stati = Arrays.asList(v_temp);
	            if (v_stati.contains("gialla") && !(v_stati.contains("libera")) && !(v_stati.contains("rossa"))) return "gialla";
	            else if(v_stati.contains("rossa") && !(v_stati.contains("libera")) && !(v_stati.contains("gialla"))) return "rossa";
	        }
	    
	  //Diagonale verso destra
	    for (int i = n-1 ; i > n-4; i--)
	        for (int j = 0; j < m-3; j++) {
	            String[] v_temp = new String[4];
	            v_temp[0] = getStato(griglia[i][j]);
	            v_temp[1] = getStato(griglia[i-1][j+1]);
	            v_temp[2] = getStato(griglia[i-2][j+2]);
	            v_temp[3] = getStato(griglia[i-3][j+3]);
	            List<String> v_stati = Arrays.asList(v_temp);
	            if (v_stati.contains("gialla") && !(v_stati.contains("libera")) && !(v_stati.contains("rossa"))) return "gialla";
	            else if(v_stati.contains("rossa") && !(v_stati.contains("libera")) && !(v_stati.contains("gialla"))) return "rossa";
	        }
	    
	  //Diagonale verso sinistra
	    for (int i = n-1 ; i > n-4; i--)
	        for (int j = m-1; j > m-5; j--) {
	            String[] v_temp = new String[4];
	            v_temp[0] = getStato(griglia[i][j]);
	            v_temp[1] = getStato(griglia[i-1][j-1]);
	            v_temp[2] = getStato(griglia[i-2][j-2]);
	            v_temp[3] = getStato(griglia[i-3][j-3]);
	            List<String> v_stati = Arrays.asList(v_temp);
	            if (v_stati.contains("gialla") && !(v_stati.contains("libera")) && !(v_stati.contains("rossa"))) return "gialla";
	            else if(v_stati.contains("rossa") && !(v_stati.contains("libera")) && !(v_stati.contains("gialla"))) return "rossa";
	        }
	    return "";
	}

	public static void main(String[] args) {
                menu.setVisible(true);
	}
}//Fine classe InterfacciaGrafica