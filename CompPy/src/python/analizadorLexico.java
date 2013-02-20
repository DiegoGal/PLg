package python;

import java.util.Hashtable;
import java.util.Stack;

import javax.swing.JTextArea;

import python.gestorTablaSimbolos.tablaSimbolos;
import python.token.tipoCodToken;
import python.gestorErrores.error;

public class analizadorLexico {
	
		private String texto;
		private static Hashtable<String,palres> tablaPalRes;
		private static Hashtable<String,oper> tablaOP;
		private static Hashtable<String,delim> tablaDEL;
		
		private static int maxtokens=10;
		private int fil=0;
		private int col=0;
		private int i=0;
		private char preanalisis;
		private String lexema;
		double parteEntera=0;
		double parteDecimal=0;
		double base = 10; 
		double pesoDecimal = 1 / base;
		private token[] Token= new token[maxtokens];
		private Stack<Integer> indentStack = new Stack<Integer>();
		private int nivelIndent;
		private boolean ignoraEOL=false;
		private int num=0;
		
		private tablaSimbolos TS;
		private gestorErrores GE;
		
		enum oper {mul,sum,res,div,divent,exp,mod,and,or,mayor,mayori,menor,
			menori,igual,distinto,asignacion,xor};
		enum delim {Aparentesis,Cparentesis,Acorchete,Ccorchete,coma,punto,puntoycoma,dospuntos
			,newline,indent, dedent, espacio, EOF, tab,masigual,menosigual,porigual,divigual,diventeraigual,modigual,
			andigual,origual,xorigual};
		enum palres {False,True,Class,Return,None,Continue,For,Def,Nonlocal,While,
				And,Del,Global,Not,Elif,If,Or,Else,Import,Break};
		enum estado {fin,q0,q1,q3,q4,q5,q8,q10,q12,q15b,q17,q19, q24, q40, q31, q20};
		
	public analizadorLexico(){
		
		indentStack.push(0);
		
		//carga en la tabla los operadores
		tablaOP = new Hashtable<String,oper>();
		tablaOP.put("*", oper.mul);tablaOP.put("+", oper.sum);tablaOP.put("-", oper.res);
		tablaOP.put("/", oper.div);tablaOP.put("//", oper.divent);tablaOP.put("**", oper.exp);
		tablaOP.put("%", oper.mod);tablaOP.put("&", oper.and);tablaOP.put("|", oper.or);
		tablaOP.put(">", oper.mayor);tablaOP.put(">=", oper.mayori);tablaOP.put("<", oper.menor);
		tablaOP.put("<=", oper.menori);tablaOP.put("==", oper.igual);tablaOP.put("!=", oper.distinto);
		tablaOP.put("=", oper.asignacion);tablaOP.put("^", oper.xor);
		//carga en la tabla los delimitadores
		tablaDEL = new Hashtable<String,delim>();
		tablaDEL.put("(", delim.Aparentesis);tablaDEL.put(")", delim.Cparentesis);
		tablaDEL.put("[", delim.Acorchete);tablaDEL.put("]", delim.Ccorchete);
		tablaDEL.put(",", delim.coma);tablaDEL.put(".", delim.punto);
		tablaDEL.put(";", delim.puntoycoma);tablaDEL.put(":", delim.dospuntos);
		tablaDEL.put(" ", delim.espacio);tablaDEL.put("\n", delim.EOF);//estas también delimitan
		tablaDEL.put("\t", delim.tab);tablaDEL.put("+=", delim.masigual);
		tablaDEL.put("-=", delim.menosigual);tablaDEL.put("*=", delim.porigual);
		tablaDEL.put("/=", delim.divigual);tablaDEL.put("//=", delim.diventeraigual);
		tablaDEL.put("%=", delim.modigual);tablaDEL.put("&=", delim.andigual);
		tablaDEL.put("|=", delim.andigual);tablaDEL.put("^=", delim.xorigual);
		//carga la tabla de palabras reservadas
		tablaPalRes = new Hashtable<String,palres>();
		tablaPalRes.put("False", palres.False);tablaPalRes.put("True", palres.True);
		tablaPalRes.put("class", palres.Class);tablaPalRes.put("return", palres.Return);
		tablaPalRes.put("None", palres.None);tablaPalRes.put("continue", palres.Continue);
		tablaPalRes.put("for", palres.For);tablaPalRes.put("def", palres.Def);
		tablaPalRes.put("nonlocal", palres.Nonlocal);tablaPalRes.put("while", palres.While);
		tablaPalRes.put("and", palres.And);tablaPalRes.put("del", palres.Del);
		tablaPalRes.put("global", palres.Global);tablaPalRes.put("not", palres.Not);
		tablaPalRes.put("elif", palres.Elif);tablaPalRes.put("if", palres.If);
		tablaPalRes.put("or", palres.Or);tablaPalRes.put("del", palres.Else);
		tablaPalRes.put("import", palres.Import);tablaPalRes.put("break", palres.Break);
		
	}
	
	
	// método para agregar la tabla de simbolos
	public void setTS(tablaSimbolos ts){
		TS=ts;
	}
	
	// método para agregar el gestor de errores
	public void setGE(gestorErrores ge){
		GE=ge;
	}
	
	// método para coger el texto a compilar
	public void setText(String ta){
		texto = ta;
		i=fil=col=0;
	}
	
	private char getch(){
		if (i<texto.length()){
			col ++;
			i++;
			return texto.charAt(i-1);}
		else{i =texto.length()+1;
			return ' ';}
	}
	
	private void ungetch(){
		i --;col--;
	}
	
	//metodo de interfaz con el analizador sintáctico
	public token[] scan(){
			estado actual=estado.q0;
			inicializaTokens();
			preanalisis=getch();
			lexema="";
			while (actual!=estado.fin){
			switch (actual){
				case q0:
					if (i==texto.length()+1){ //EOF
						K();lexema="";
						fil=col=0;
						actual=estado.fin; // todos los estados finales
						break;}
					if (preanalisis== ' '){
						A();
						break;}
					if (preanalisis=='\\'){ // tiene que llegar un fin de linea
						P(); //añadir al automata
						break;}
					if (isNumber(preanalisis)){
						D();A();
						actual=estado.q3;
						break;}
					if (isLetter(preanalisis)){
						B();A();
						actual=estado.q1;
						break;}
					if (preanalisis=='\''){
						A();
						actual=estado.q10;
						break;}
					if (preanalisis=='"'){
						A();
						actual=estado.q12;
						break;}
					if (preanalisis=='\n'){
						if (ignoraEOL) {A();ignoraEOL=false;break;}
						else{L();
						A();
						actual=estado.q8;
						break;}
						}
					if (preanalisis=='#'){
						Q();
						break;}
					if ((preanalisis=='+')|(preanalisis=='-')|(preanalisis=='%')|
							(preanalisis=='&')|(preanalisis=='|')){
						B(); A();
						actual=estado.q15b;//q15b,q16,q21,q22,q23
						break;}
					if (preanalisis=='*'){
						B();A();
						actual=estado.q17;
						break;}
					if (preanalisis=='/'){
						B();A();
						actual=estado.q19;
						break;}
					if (preanalisis=='^'){
						B();A();
						actual=estado.q24;
						break;}
					if ((preanalisis=='=')|(preanalisis=='<')|(preanalisis=='>')){
						B();A();
						actual=estado.q40; // q40,q27,q29
						break;}
					if (preanalisis=='!'){
						B();A();
						actual=estado.q31;
						break;}
					if ((preanalisis==';')|(preanalisis==':')){
						B();H();
						actual=estado.fin;//q33,q34
						break;}
					if ((preanalisis=='(')|(preanalisis==')')|(preanalisis=='[')|
							(preanalisis==']')|(preanalisis==',')){
						B();H();
						actual=estado.fin; // q35,q36,q37,q38,q39
						break;}
					//Error  4 y 1
					if (preanalisis=='.'){
						GE.añadirError(GE.new error(4, fil, col)); 
						actual=estado.fin;
						break;}
					else 
						{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;
						}
				case q1:
					if (isNumber(preanalisis)|isLetter(preanalisis)){
						B();A();
						break;}
					if(isDelim(String.valueOf(preanalisis))|isOperator(String.valueOf(preanalisis))){
						C();ungetch();
						actual=estado.fin;
						break;}
					// error
					ungetch();
					if(isKeyword(preanalisis)){
						GE.añadirError(GE.new error(4,fil,col));
						actual=estado.fin;
						break;}
					else{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;}
				case q3:
					if(isNumber(preanalisis)){
						D();A();
						break;}
					if(preanalisis=='.'){
						A();
						actual=estado.q4;
						break;}
					if(isDelim(String.valueOf(preanalisis))|isOperator(String.valueOf(preanalisis))){
						G();ungetch();
						actual=estado.fin;
						break;}
					// error
					ungetch();
					if(isLetter(preanalisis)|isKeyword(preanalisis)){
						GE.añadirError(GE.new error(2,fil,col));
						actual=estado.fin;
						break;}
					else{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;}
				case q4:
					if(isNumber(preanalisis)){
						E();A();
						actual=estado.q5;
						break;}
					// error
					if(isLetter(preanalisis)|isDelim(String.valueOf(preanalisis))|isOperator(String.valueOf(preanalisis))
							|isKeyword(preanalisis)){
						GE.añadirError(GE.new error(2,fil,col));
						actual=estado.fin;
						break;}
					else{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;}
				case q5:
					if(isNumber(preanalisis)){
						E();A();
						actual=estado.q5;
						break;}
					if(isDelim(String.valueOf(preanalisis))|isOperator(String.valueOf(preanalisis))){
						F();
						actual=estado.fin;
						break;}
					// error
					if(isLetter(preanalisis)|isKeyword(preanalisis)){
						GE.añadirError(GE.new error(2,fil,col));
						actual=estado.fin;
						break;}
					else{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;}
				case q8:
					if(preanalisis=='\t'){
						M();A();
						break;}
					if(isNumber(preanalisis)|isLetter(preanalisis)|isDelim(String.valueOf(preanalisis))|
							isOperator(String.valueOf(preanalisis))){
						N();ungetch();
						actual=estado.fin;
						break;}
					// error
					else{
						GE.añadirError(GE.new error(1, fil, col));
						ungetch();
						actual=estado.fin;
						break;}
				case q10:
					if(preanalisis!='\'' & i!=texto.length()+1 & (isNumber(preanalisis)|isLetter(preanalisis)|isDelim(String.valueOf(preanalisis))|
							isOperator(String.valueOf(preanalisis)))){
						B();A();
						break;}
					if(preanalisis=='\''){O();
						actual=estado.fin;
						break;}
					//error
					if(preanalisis=='\n'|i==texto.length()+1){ //EOF
						GE.añadirError(GE.new error(4, fil, col));
						actual=estado.fin;
						break;}
					else{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;}
				case q12:
					if(preanalisis!='"' & i!=texto.length()+1 & (isNumber(preanalisis)|isLetter(preanalisis)|isDelim(String.valueOf(preanalisis))|
					isOperator(String.valueOf(preanalisis)))){
						B();A();
						break;}
					if(preanalisis=='"'){O();
					actual=estado.fin;
					break;}
					//error
					if(preanalisis=='\n'|i==texto.length()+1){ // EOF
						GE.añadirError(GE.new error(4, fil, col));
						actual=estado.fin;
						break;}
					else{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;}
				case q15b: //15b, 16, 21,22,23
					if(preanalisis=='='){
						B();H();
						actual=estado.fin;
						break;}
					if(preanalisis==' '|isLetter(preanalisis)|isNumber(preanalisis)){
						H();
						actual=estado.fin;
						ungetch();
						break;}
					//error
					ungetch();
					if(isKeyword(preanalisis)|isDelim(String.valueOf(preanalisis))|isOperator(String.valueOf(preanalisis))){
						GE.añadirError(GE.new error(3, fil, col));
						actual=estado.fin;
						break;}
					else{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;}
				case q17:
					if(preanalisis=='*'|preanalisis=='='){
						B();H();
						actual=estado.fin;
						break;}
					if(preanalisis==' '|isLetter(preanalisis)|isNumber(preanalisis)){
						H();
						actual=estado.fin;
						ungetch();
						break;}
					//error
					ungetch();
					if(isKeyword(preanalisis)|isDelim(String.valueOf(preanalisis))|isOperator(String.valueOf(preanalisis))){
						GE.añadirError(GE.new error(3, fil, col));
						actual=estado.fin;
						break;}
					else{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;}
				case q19:
					if(preanalisis=='='){
						B();H();
						actual=estado.fin;
						break;}
					if (preanalisis=='/'){
						B();A();
						actual=estado.q20;
						break;}
					if(preanalisis==' '|isLetter(preanalisis)|isNumber(preanalisis)){
						H();
						ungetch();
						actual=estado.fin;
						break;}
					//error
					ungetch();
					if(isKeyword(preanalisis)|isDelim(String.valueOf(preanalisis))|isOperator(String.valueOf(preanalisis))){
						GE.añadirError(GE.new error(3, fil, col));
						actual=estado.fin;
						break;}
					else{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;}
				case q20:
					if(preanalisis=='='){
						B();H();
						actual=estado.fin;
						break;}
					if(preanalisis==' '|isLetter(preanalisis)|isNumber(preanalisis)){
						H();
						ungetch();
						actual=estado.fin;
						break;}
					//error
					ungetch();
					if(isKeyword(preanalisis)|isDelim(String.valueOf(preanalisis))|isOperator(String.valueOf(preanalisis))){
						GE.añadirError(GE.new error(3, fil, col));
						actual=estado.fin;
						break;}
					else{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;}
				case q24:
					if(preanalisis=='='){
						B();H();
						actual=estado.fin;
						break;}
					if(preanalisis==' '|isLetter(preanalisis)|isNumber(preanalisis)){
						H();
						ungetch();
						actual=estado.fin;
						break;}
					//error
					ungetch();
					if(isKeyword(preanalisis)|isDelim(String.valueOf(preanalisis))|isOperator(String.valueOf(preanalisis))){
						GE.añadirError(GE.new error(3, fil, col));
						actual=estado.fin;
						break;}
					else{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;}
				case q40:
					if(preanalisis=='='){
						B();H();
						actual=estado.fin;
						break;}
					if(preanalisis==' '|isLetter(preanalisis)|isNumber(preanalisis)){
						H();
						ungetch();
						actual=estado.fin;
						break;}
					//error
					ungetch();
					if(isKeyword(preanalisis)|isDelim(String.valueOf(preanalisis))|isOperator(String.valueOf(preanalisis))){
						GE.añadirError(GE.new error(3, fil, col));
						actual=estado.fin;
						break;}
					else{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;}
				case q31:
					if(preanalisis=='='){
						B();H();
						break;}
					//error
					if(isKeyword(preanalisis)|isDelim(String.valueOf(preanalisis))|isOperator(String.valueOf(preanalisis))
							|isLetter(preanalisis)){
						GE.añadirError(GE.new error(3, fil, col));
						actual=estado.fin;
						break;}
					else{
						GE.añadirError(GE.new error(1, fil, col));
						actual=estado.fin;
						break;}
				default:
					return null;
				}
			}
		return Token;
	}


	private boolean isKeyword(char pre) {
		// Palabras clave
		if (pre=='\''|pre=='"'|pre=='#'|pre=='\\')
			return true;
		else return false;
	}


	private void inicializaTokens() {
		for (int i=0;i<maxtokens;i++){
			Token[i]=null;
		}
		num=0;
	}

	private boolean isOperator(String string) {
		return tablaOP.containsKey(string);
	}

	private boolean isDelim(String string) {
		return tablaDEL.containsKey(string);
	}

	private boolean isLetter(char c) {
		
		if ((c == 'a')|(c == 'A')|(c == 'b')|(c == 'B')|(c == 'c')|
				(c == 'C')|(c == 'd')|(c == 'D')|(c == 'e')|(c == 'E')|(c == 'f')|(c == 'F')|
				(c == 'g')|(c == 'G')|(c == 'h')|(c == 'H')|(c == 'i')|(c == 'I')|(c == 'j')|
				(c == 'J')|(c == 'k')|(c == 'K')|(c == 'l')|(c == 'L')|(c == 'm')|(c == 'M')|
				(c == 'n')|(c == 'N')|(c == 'ñ')|(c == 'Ñ')|(c == 'o')|(c == 'O')|(c == 'p')|
				(c == 'P')|(c == 'q')|(c == 'Q')|(c == 'r')|(c == 'R')|(c == 's')|(c == 'S')|
				(c == 't')|(c == 'T')|(c == 'u')|(c == 'U')|(c == 'v')|(c == 'V')|(c == 'w')|
				(c == 'W')|(c == 'x')|(c == 'X')|(c == 'y')|(c == 'Y')|(c == 'z')|(c == 'Z'))
			return true;
		else
			return false;
	}

	private boolean isNumber(char c) {
		 if ((c=='0')|(c=='1')|(c=='2')|(c=='3')|(c=='4')|(c=='5')|(c=='6')
				 |(c=='7')|(c=='8')|(c=='9'))
			 return true;
		 else
			 return false;
	}

	//////////////////////////////////////////////////////
	////             REGLAS SINTÁCTICAS               ////
	//////////////////////////////////////////////////////
	
	// A
	private void A(){
		preanalisis=getch();
	}
	// A
	
	// B
	private void B(){
		lexema+=preanalisis;
	}
	// B
	
	// C
	private void C(){
	if (tablaPalRes.containsKey(lexema)){
		Token[num]= new token(tipoCodToken.PAL_RES,tablaPalRes.get(lexema),fil,col);num++;
		}
	else {
		if (TS.busca(lexema)==null)
		Token[num]= new token(tipoCodToken.ID,TS.inserta(lexema, tipoCodToken.ID),fil,col);num++;
		}
	}
	// C;
	
	// D
	private void D(){
		parteEntera = base * parteEntera + Digito(preanalisis);
	}
	//D
	
	private double Digito(char c) {
		double a=0;
		switch (c){
		case '0':a=0;break;case '1':a=1;break;
		case '2':a=2;break;case '3':a=3;break;
		case '4':a=4;break;case '5':a=5;break;
		case '6':a=6;break;case '7':a=7;break;
		case '8':a=8;break;case '9':a=9;break;
		}
		return a;
	}
	
	//E
	private void E(){
		  parteDecimal += (Digito(preanalisis)*pesoDecimal);
		  pesoDecimal=pesoDecimal/base;
	} 
	//E
	
	//F
	private void F(){
		Token[num] = new token(tipoCodToken.REAL, parteEntera + parteDecimal,fil,col);num++;
		parteEntera=parteDecimal=0;
	}
	//F
	
	//G
	private void G(){
		Token[num] = new token(tipoCodToken.ENTERO,parteEntera,fil,col);num++;
		parteEntera=0;
	}
	//G
	
	private void H(){
		// H
		if (tablaOP.containsKey(lexema))
			{Token[num]= new token(tipoCodToken.OP,tablaOP.get(lexema),fil,col);num++;}
		if (tablaDEL.containsKey(lexema))
			{Token[num]= new token(tipoCodToken.DEL,tablaDEL.get(lexema),fil,col);num++;}
		// H
	}
	
	//K
	private void K(){
		   while (indentStack.peek()!=0){
			   indentStack.pop();
			   Token[num] = new token(tipoCodToken.DEL,delim.dedent,fil,col);num++;
		   }
		   nivelIndent=0;
			Token[num] = new token(tipoCodToken.FIN,delim.EOF,fil,col);num++;
	}
	//K
	
	//L
	private void L(){
		fil++;
		col=0;
		nivelIndent=0;
		Token[num]=new token(tipoCodToken.DEL, delim.newline,fil,col);num++;
	}
	//L
	
	//M
	private void M(){
		nivelIndent++;
	}
	//M
	
	//N
	private void N(){
		int cima=-1;
		  while (nivelIndent!=cima){
		  cima = indentStack.peek();
		  if (nivelIndent > cima){
			  indentStack.push(nivelIndent);
			  Token[num] = new token(tipoCodToken.DEL, delim.indent,fil,col);num++;
		  }
		  if (nivelIndent < cima){
			  indentStack.pop();
			  Token[num] = new token(tipoCodToken.DEL, delim.dedent,fil,col);num++;
		  }
		}
	}
	//N
	
	//O
	private void O(){
		Token[num] = new token(tipoCodToken.STRING, lexema,fil,col);num++;
		lexema = "";
	}
	//O
	
	//P
	private void P(){
		ignoraEOL =true;
	}
	//P
	
	//Q
	private void Q(){
		while (preanalisis!='\n')
			preanalisis=getch();
	}
	//Q
	
}
