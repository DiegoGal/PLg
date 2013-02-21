package python;

public class gestorErrores {

	private error[] errores;
	int i=0; //iterador
	static final int defErr=10; //determina el tama�o del array de errores
	
	public gestorErrores(){
		errores= new error[defErr];
	}
	
	public void a�adirError(error r){
		if (i>=defErr){
			//duplica el tama�o del array si no caben los errores
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
						return "Error l�xico: Car�cter de entrada no permitido";
					case 2:
						return "Error l�xico: Constante num�rica mal formada";
					case 3:
						return "Error l�xico: Se esperaba el car�cter '=' ";
					case 4:
						return "Error l�xico: Car�cter inesperado en este contexto";
					default:
						return "";
				}
			}
			else{
				return mensaje;	
			}
		}
		
		//I: Error l�xico: C�racter de entrada no permitido
		//II: Error l�xico: Constante num�rica mal formada
		//III: Error l�xico: Se esperaba el car�cter �=�
		//IV: Error l�xico: Car�cter inesperado en este contexto
	}
	
}
