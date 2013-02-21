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

import python.gestorErrores.error;
import python.token.tipoCodToken;


public class principal {
	private analizadorLexico lex;
	private gestorTablaSimbolos TS;
	private gestorErrores GE;
	private analizadorSintactico sin;
	
	private JFrame frmCompiladorPython;


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
	
	final JTextPane toutError = new JTextPane();
	scrollPane_2.setViewportView(toutError);
	
	internalFrame_2.setVisible(true);
	internalFrame_1.setVisible(true);
	internalFrame.setVisible(true);
	
	
	
	
    
    
     Bcompilar.addActionListener(new ActionListener() // compilar
     {
     	public void actionPerformed(ActionEvent e)
     	{
     		GE.vaciaErr(); //Vaciamos los errores que había
     		toutToken.setText(""); //se vacia el texto
     		lex.setText(tin.getText().toString()); //cojemos el texto a compilar
     		token[] tok=null;
     		boolean fin=false;
     		int num=0;
     		while (!fin){ // mientras no encontremos el token fin escaneamos la cadena de entrada 
     			tok=lex.scan();
     			num=0;
     			int ultToken=lex.getnumTokens()-1;
     			if (ultToken<0)
     				ultToken=0;
     			if ((tok[ultToken]!=null) && (tok[ultToken].getCod()==tipoCodToken.FIN)) // si encontramos el token fin en el último de los tokens activamos el flag
     				fin=true;
     			while (tok[num]!=null){ //mientras haya tokens en el array
     				if (tok[num].getCod()==tipoCodToken.ID)
     				{
     					// 	Se inserta en el texto de salida toutToken el tipo y atributo del token
     					try {
     						toutToken.getStyledDocument().insertString(
     								toutToken.getStyledDocument().getLength(), tok[num].getCod().toString() + " (punt a TS)" + "\n", attrs);
     					} catch (BadLocationException e1) {
						// 	TODO Auto-generated catch block
     						e1.printStackTrace();
     					}
     				}
     				else 
     				{
				// 	Se inserta
     					try {
     						toutToken.getStyledDocument().insertString(
     								toutToken.getStyledDocument().getLength(), tok[num].getAtr().toString() + " ("+tok[num].getCod().toString()+")" + "\n", attrs);
     					} catch (BadLocationException e1) {
						// 	TODO Auto-generated catch block
     						e1.printStackTrace();
     					}
     					
     				}
     				num++;
     			}
     		}
     		
     		sin.parser();
     		
     		
     		//escribimos los errores que ha habido en toutError
     		error[] err=GE.getErrores();
     		toutError.setText("");
     		int i=0;
     		while (err[i]!=null){
     			try {
					toutError.getStyledDocument().insertString(
								toutError.getStyledDocument().getLength(), "("+err[i].getFila()+","+err[i].getColumna()+")" + err[i].getMensaje() + "\n", attrs);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
     			i++;

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