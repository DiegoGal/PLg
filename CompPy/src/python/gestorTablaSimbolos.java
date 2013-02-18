package python;

import java.util.HashMap;

import python.gestorTablaSimbolos.tablaSimbolos;
import python.token.tipoCodToken;

public class gestorTablaSimbolos {
	
		private entradaB[] matrizB;
		private tablaSimbolos bloqueActual;
		private int numB=1;
		
		public gestorTablaSimbolos(){
			matrizB= new entradaB[10];
			bloqueActual=new tablaSimbolos();
			matrizB[0]=new entradaB(numB,0,bloqueActual);
		}

		
public class entradaB{
	private int numBloque;
	private int continente;
	private tablaSimbolos tablaSimbolos;
	
	public entradaB(int numB, int i, tablaSimbolos ba) {
		numBloque=numB;
		continente=i;
		tablaSimbolos=ba;
	}
	
}

public class tablaSimbolos{
	private HashMap<String, entradaT> contenido;
	
	public tablaSimbolos(){
		contenido= new HashMap<String,entradaT>();
	}
	
	public entradaT inserta(String s,tipoCodToken tCT){
		if (contenido.containsKey(s)){
			return contenido.get(s);
		}
		else
		return null;//insertar en la tabla y devuelve lo mismo que ha insertado
		
	}
	
	public entradaT busca(String s){
		return contenido.get(s);	
	}
}

public class entradaT{
	
	private String tipo;
	private int numArgs;
	private int tipoArgs;
	private String otro;
	private String retorno;
	
	public entradaT(){
		
	}
	
	public entradaT(String t,int nA,int tA,String otro,String retorno){
		tipo=t;
		numArgs=nA;
		tipoArgs=tA;
		this.otro=otro;
		this.retorno=retorno;
	}
	
}

public tablaSimbolos getBloqueActual() {
	// TODO Auto-generated method stub
	return bloqueActual;
}
		
}
