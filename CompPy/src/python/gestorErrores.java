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
	
	public void vaciaErr(){
		if (i>0)//si hay elementos
			errores= new error[defErr];
	}
	
	
	
	public class error{
		int tipo;
		int fila;
		int columna;
		
		public error(int t,int f,int c){
			tipo=t;
			fila=f;
			columna=c;
		}
		
		//I: Error léxico: Cáracter de entrada no permitido
		//II: Error léxico: Constante numérica mal formada
		//III: Error léxico: Se esperaba el carácter “=”
		//IV: Error léxico: Carácter inesperado en este contexto
	}
	
}
