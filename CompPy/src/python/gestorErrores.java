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
		
		//I: Error l�xico: C�racter de entrada no permitido
		//II: Error l�xico: Constante num�rica mal formada
		//III: Error l�xico: Se esperaba el car�cter �=�
		//IV: Error l�xico: Car�cter inesperado en este contexto
	}
	
}
