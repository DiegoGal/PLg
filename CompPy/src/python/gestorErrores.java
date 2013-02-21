package python;

public class gestorErrores {

	private error[] errores;
	int i=0; //iterador
	static final int defErr=10; //determina el tamaño del array de errores
	
	public gestorErrores(){
		errores= new error[defErr];
	}
	
	public void añadirError(error r){
		if (i>=defErr){
			//duplica el tamaño del array si no caben los errores
			error[] aux= new error[defErr*2];
			for (int j=0;j<i;j++)
				aux[j]=errores[j];
			errores=aux;
		}
		else{
			errores[i]=r;
			i++;
		}
	}
	
	public error[] getErrores(){
		return errores;
	}
	
	
	public void vaciaErr(){
		if (i>0)//si hay elementos
		{
			errores= new error[defErr];
			i=0;
		}
	}
	
	
	
	public class error{
		int tipo;
		int fila;
		int columna;
		String mensaje;
		
		public error(int t,int f,int c){
			tipo=t;
			fila=f;
			columna=c;
			mensaje=null;
		}
		
		public error(String s,int f,int c){
			tipo=0;
			fila=f;
			columna=c;
			mensaje=s;
		}
		
		public int getFila(){
			return fila;
		}
		
		public int getColumna(){
			return columna;
		}
		
		public String getMensaje(){
			if (tipo!=0){
				switch (tipo){
					case 1:
						return "Error léxico: Carácter de entrada no permitido";
					case 2:
						return "Error léxico: Constante numérica mal formada";
					case 3:
						return "Error léxico: Se esperaba el carácter '=' ";
					case 4:
						return "Error léxico: Carácter inesperado en este contexto";
					default:
						return "";
				}
			}
			else{
				return mensaje;	
			}
		}
		
		//I: Error léxico: Cáracter de entrada no permitido
		//II: Error léxico: Constante numérica mal formada
		//III: Error léxico: Se esperaba el carácter “=”
		//IV: Error léxico: Carácter inesperado en este contexto
	}
	
}
