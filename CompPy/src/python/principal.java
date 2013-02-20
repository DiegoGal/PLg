package python;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import python.token.tipoCodToken;


public class principal {
	private analizadorLexico lex;
	private gestorTablaSimbolos TS;
	private gestorErrores GE;
	private analizadorSintactico sin;
	
	private JFrame frmCompiladorPython;
	private int fil=0;


public principal () {
	
	//inicialización de los atributos
		lex = new analizadorLexico();
		TS=new gestorTablaSimbolos();
		GE=new gestorErrores();
		sin=new analizadorSintactico(lex,GE);
		lex.setTS(TS.getBloqueActual());
		lex.setGE(GE);
	
	frmCompiladorPython = new JFrame();
	frmCompiladorPython.setTitle("Compilador python");
	frmCompiladorPython.setBounds(100, 100, 1200, 500);
	frmCompiladorPython.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frmCompiladorPython.getContentPane().setLayout(new BoxLayout(frmCompiladorPython.getContentPane(), BoxLayout.X_AXIS));
	
	JInternalFrame internalFrame = new JInternalFrame("Texto a compilar");
	frmCompiladorPython.getContentPane().add(internalFrame);
	internalFrame.getContentPane().setLayout(new BorderLayout(0, 0));
	
	JScrollPane scrollPane = new JScrollPane();
	internalFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
	
	//texto de entrada
	final JTextPane tin = new JTextPane();
	scrollPane.setViewportView(tin);
	
	JPanel panel = new JPanel();
	frmCompiladorPython.getContentPane().add(panel);
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	
	JButton Bcompilar = new JButton("Compilar");
	panel.add(Bcompilar);
	
	JButton Bsalir = new JButton("Salir");
	panel.add(Bsalir);
	
	//texto de salida
	JInternalFrame internalFrame_1 = new JInternalFrame("Tokens reconocidos");
	frmCompiladorPython.getContentPane().add(internalFrame_1);
	
	JScrollPane scrollPane_1 = new JScrollPane();
	internalFrame_1.getContentPane().add(scrollPane_1, BorderLayout.CENTER);
	
	 final JTextPane toutToken = new JTextPane();
	scrollPane_1.setViewportView(toutToken);
	//Hace falta attrs para insertar el texto bien
	final SimpleAttributeSet attrs = new SimpleAttributeSet();
	StyleConstants.setBold(attrs, true);

	
	
	JInternalFrame internalFrame_2 = new JInternalFrame("Errores");
	frmCompiladorPython.getContentPane().add(internalFrame_2);
	
	JScrollPane scrollPane_2 = new JScrollPane();
	internalFrame_2.getContentPane().add(scrollPane_2, BorderLayout.CENTER);
	
	JTextPane toutError = new JTextPane();
	scrollPane_2.setViewportView(toutError);
	
	internalFrame_2.setVisible(true);
	internalFrame_1.setVisible(true);
	internalFrame.setVisible(true);
	
	
	
	
    
    
     Bcompilar.addActionListener(new ActionListener() // next token
     {
     	public void actionPerformed(ActionEvent e)
     	{
     		lex.setText(tin.getText().toString());
     		token[] tok=lex.scan();
     		int num=0;
     		while (tok[num].getCod()!=tipoCodToken.FIN){
     		tok=lex.scan();
     		num=0;
			while (tok[num]!=null){
			 if (tok[num].getCod()==tipoCodToken.ID)
			 {
				// Se inserta
					try {
						toutToken.getStyledDocument().insertString(
						   toutToken.getStyledDocument().getLength(), tok[num].getCod().toString() + " (punt a TS)" + "\n", attrs);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			 }
			 else 
			 {
				// Se inserta
					try {
						toutToken.getStyledDocument().insertString(
						   toutToken.getStyledDocument().getLength(), tok[num].getCod().toString() + " ("+tok[num].getAtr().toString()+")" + "\n", attrs);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

			 }
     		 num++;
     		 }
     		}
     	}
     });
     
     Bsalir.addActionListener(new ActionListener() // exit
     {
    	 public void actionPerformed(ActionEvent e)
      	{
             frmCompiladorPython.dispose();
      	} 
     });
     
  // Se le dice a la ventana que termine el programa cuando se la cierre
     frmCompiladorPython.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
     // Se le da un tamaño automático a la ventana para que quepa todo su
     // contenido.
     //JF.pack();
     
     // Se hace visible la ventana
     frmCompiladorPython.setVisible(true);
}
	
	
public static void main(String[] args) {
	
	 new principal();
	}
}