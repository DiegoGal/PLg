package python;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import python.token.tipoCodToken;


public class principal {
	private analizadorLexico lex;
	private gestorTablaSimbolos TS;
	private gestorErrores GE;
	private analizadorSintactico sin;
	
	private JTextArea tin;
	private JTable tout;
	private JFrame JF;
	private JFrame out;
	private JPanel derecho;
	private int fil=0;

public principal () {
	
	//inicialización de los atributos
	lex = new analizadorLexico();
	TS=new gestorTablaSimbolos();
	GE=new gestorErrores();
	sin=new analizadorSintactico(lex);
	
	lex.setTS(TS.getBloqueActual());
	lex.setGE(GE);
	
	out= new JFrame ("Tokens");
	out.setLayout(new BorderLayout());
	out.setBounds(900, 100, 400, 300);
	String[] s= {"Tipo Token","Atributo"};
	Object[][] o= new Object [][] {
             {null, null}, {null, null},{null, null}, {null, null},
             {null, null}, {null, null},{null, null}, {null, null},
             {null, null}, {null, null},{null, null}, {null, null},
             {null, null}, {null, null},{null, null}, {null, null},
             {null, null}, {null, null},{null, null}, {null, null},
             {null, null}, {null, null},{null, null}, {null, null},
             {null, null}, {null, null},{null, null}, {null, null},
             {null, null}, {null, null},{null, null}, {null, null}
         };
	tout= new JTable(o,s);
	JScrollPane scrl=new JScrollPane(tout);
	out.getContentPane().add(scrl,BorderLayout.CENTER);
	out.setVisible(true);
	JF = new JFrame ("Python");
	JF.setLayout(new BorderLayout());
	JF.setBounds(200, 100, 700, 300);
	// preparación de la ventana
	derecho = new JPanel();
	derecho.setLayout(new BoxLayout(derecho,BoxLayout.Y_AXIS));
	tin = new JTextArea();	
	JScrollPane scroll=new JScrollPane(tin);
	JButton b0 = new JButton("Start");
	b0.setEnabled(true);
	final JButton b1 = new JButton("Next token");
	JButton b2 = new JButton("Exit");
	b1.setEnabled(false);
	b2.setEnabled(true);
	derecho.add(b0);
    derecho.add(b1);
    derecho.add(b2);
    JF.getContentPane().add(scroll,BorderLayout.CENTER);
    JF.getContentPane().add(derecho,BorderLayout.EAST);
 
    
    b0.addActionListener(new ActionListener()
     {
     	public void actionPerformed(ActionEvent e) //Start
     	{
             b1.setEnabled(true);
             GE.vaciaErr();
             lex.setText(tin);
             for (int col=0;col<tout.getColumnCount();col++)
            	 for (int fil=0;fil<tout.getRowCount();fil++)
            		 tout.setValueAt(null, fil, col);
             fil=0;
        }
     });
    
     b1.addActionListener(new ActionListener() // next token
     {
     	public void actionPerformed(ActionEvent e)
     	{
     		 token[] t=lex.scan();
     		 int num=0;
			while (t[num]!=null){
			 if (t[num].getCod()==tipoCodToken.ID)
			 {
				 tout.setValueAt(t[num].getCod(), fil, 0);
	     		 tout.setValueAt("punt a TS", fil, 1); 
			 }
			 else 
			 {
				 tout.setValueAt(t[num].getCod(), fil, 0);
     		 	 tout.setValueAt(t[num].getAtr(), fil, 1);
			 }
     		 fil++;
     		 if (t[num].getCod()==tipoCodToken.FIN){
     			b1.setEnabled(false);
     		 }
     		 num++;
     		 }
     	}
     });
     
     b2.addActionListener(new ActionListener() // exit
     {
    	 public void actionPerformed(ActionEvent e)
      	{
             JF.dispose();
             out.dispose();
      	} 
     });
     
  // Se le dice a la ventana que termine el programa cuando se la cierre
     JF.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
     // Se le da un tamaño automático a la ventana para que quepa todo su
     // contenido.
     //JF.pack();
     
     // Se hace visible la ventana
     JF.setVisible(true);
}
	
	
public static void main(String[] args) {
	
	 new principal();
	}
}