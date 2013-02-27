package python;

import python.analizadorLexico.delim;
import python.analizadorLexico.oper;
import python.analizadorLexico.palres;
import python.gestorErrores.error;
import python.token.tipoCodToken;

public class analizadorSintactico {
	private analizadorLexico lex;
	private gestorErrores ge;
	private token tact; //token actual
	private int indice=0;
	
	analizadorSintactico(analizadorLexico lex, gestorErrores ge){
		this.lex=lex;
		this.ge=ge;
	}
	
	/*<sentencia> ::= (NEWLINE |<sentencia_sim> | <sentencia_comp>)* 

<sentencia_sim> ::= (<expr_sen> | <del_sen> | <flow_sen> | <import_sen> | <global_sen> | <nonlocal_sen> ) NEWLINE
<expr_sen> ::=  NAME <asign> (<expr> | ‘None’ # equivalente a null de java
//<asign> ::= (‘=’|'+=' | '-=' | '*=' | '/=' | '%=' | '&=' | '|=' | '^=' | '//=')

<expr> ::= <comp_expr> (<comp_op> <comp_expr>)*
<comp_expr> ::= <arit_expr> (<arit_op> <arit_expr>)*
<arit_expr> ::= <logic_expr> (<logic_op> <logic_expr>)*
<logic_expr> ::= '-' <un_expr> | 'not' <un_expr>
<un_expr> ::= NAME | NUMBER | STRING | | ‘false’ | ‘true’ | ‘(‘ <expr> ‘)’ |  ‘[‘<expr> ( ‘,’ <expr>)* ‘]’
<arit_op> ::= ‘+’ | ‘-‘ | ‘*’ | ‘**’ | ‘/’ | ‘//’ | ‘%’
<comp_op> ::= ‘<’ | ‘>’ | ‘<=’ | ‘>=’ | ‘==’ | ‘!=’
<logic_op> ::= ‘and’ | ‘or’ | ‘xor’
//<del_sen> ::= ‘del’ NAME ( ‘,’ NAME)*
<flow_sen> ::=  ‘break’ | ‘continue’ | ‘return’ <expr_sen> 
<import_sen> ::= ‘import’ NAME
<global_sen> ::= ‘global’ NAME ( ‘,’ NAME)* # el identificador NAME es de ámbito global 
<nonlocal_sen>::=  ‘nonlocal’ NAME ( ‘,’ NAME)*

<sentencia_comp> ::= (<if_sen> | <while_sen> | <for_sen> | <fundef> | <classdef>) NEWLINE
<if_sen> ::=  ‘if’ <condición> ‘:’ <nuevo_cont> (‘elif’ <condición> : <nuevo_cont>)* [‘else’ ‘:’ <nuevo_cont>]
<nuevo_cont> ::= (<sentencia_sim> | NEWLINE INDENT <sentencia> DEDENT)
<while_sen> ::= ‘while’ <condición> ‘:’ <nuevo_cont> [ ‘else’ ‘:’ <nuevo_cont>]
<for_sen> ::=  ‘for’ NAME ‘in’ NAME ‘:’ <nuevo_cont> [ ‘else’ ‘:’ <nuevo_cont>]
<fundef> ::= ‘def’ NAME ‘(‘ [NAME ( ‘,’ NAME )] ‘)’ ‘:’ <nuevo_cont>
<classdef> ::= ‘class’ NAME ‘(’ [NAME ( ‘,’ NAME )]  ‘)’ ‘:’ <nuevo_cont>*/
	
	void parser(){
		tact=nextToken();
		sentencia();
		recon(delim.EOF);
	}
	
	void sentencia(){
		if (tact.equals(delim.newline)){
			recon(delim.newline);
			sentencia();
		}
		else 
		{
			sent_sim();
		}
	}

	private void sent_sim() {
		if(tact.getCod()==tipoCodToken.ID){
			expr_sen();
		}else if( tact.equals(palres.Del)){
			del_sen();
		}else if(tact.equals(palres.Break) || tact.equals(palres.Continue) || tact.equals(palres.Return)){
			flow_sen();
		}else if(tact.equals(palres.Import)){
			import_sen();
		}else if(tact.equals(palres.Global)){
			global_sen();
		}else if(tact.equals(palres.Nonlocal)){
			nonlocal_sen();
		}
		recon(delim.newline);
	}
	
	//<nonlocal_sen>::=  ‘nonlocal’ NAME ( ‘,’ NAME)*
	private void nonlocal_sen() {
			recon(palres.Nonlocal);
			recon(tipoCodToken.ID);
			nonlocal_senR();
	}

	private void nonlocal_senR() {
		if(tact.equals(delim.coma)){
			recon(delim.coma);
			recon(tipoCodToken.ID);
			nonlocal_senR();
		}
	}

	//<global_sen> ::= ‘global’ NAME ( ‘,’ NAME)*
	private void global_sen() {
			recon(palres.Global);
			recon(tipoCodToken.ID);
			global_senR();
	}

	private void global_senR() {
		if(tact.equals(delim.coma)){
			recon(delim.coma);
			recon(tipoCodToken.ID);
			global_senR();
		}
	}

	//<import_sen> ::= ‘import’ NAME
	private void import_sen() {
			recon(palres.Import);
			recon(tipoCodToken.ID);
	}

	//<flow_sen> ::=  ‘break’ | ‘continue’ | ‘return’ <expr_sen>
	private void flow_sen() {
		if (tact.equals(palres.Break)){
			recon(palres.Break);
		}else if (tact.equals(palres.Continue)){
			recon(palres.Continue);
		}else if(tact.equals(palres.Return)){
			recon(palres.Return);
			expr_sen();
		}
	}

	//<del_sen> ::= ‘del’ NAME ( ‘,’ NAME)*
	private void del_sen() {
			recon(palres.Del);
			recon(tipoCodToken.ID);
			del_senR();
	}

	private void del_senR() {
		if(tact.equals(delim.coma)){
			recon(delim.coma);
			recon(tipoCodToken.ID);
			del_senR();
		}
	}

	private void expr_sen() {
		recon(tipoCodToken.ID);
		asig();
		if (tact.equals(palres.None)){
			recon(palres.None);
		}
		else{
			expr();
		}
	}

	//<asign> ::= (‘=’|'+=' | '-=' | '*=' | '/=' | '%=' | '&=' | '|=' | '^=' | '//=')
	private void asig() {
		if (tact.equals(oper.asignacion)){
			recon(oper.asignacion);
		}else if (tact.equals(delim.masigual)){
			recon(delim.masigual);
		}else if (tact.equals(delim.menosigual)){
			recon(delim.menosigual);
		}else if (tact.equals(delim.porigual)){
			recon(delim.porigual);
		}else if (tact.equals(delim.divigual)){
			recon(delim.divigual);
		}else if (tact.equals(delim.modigual)){
			recon(delim.modigual);
		}else if (tact.equals(delim.andigual)){
			recon(delim.andigual);
		}else if (tact.equals(delim.origual)){
			recon(delim.origual);
		}else if (tact.equals(delim.xorigual)){
			recon(delim.xorigual);
		}else if (tact.equals(delim.diventeraigual)){
			recon(delim.diventeraigual);
		}
		else
			errorsintactico("ERROR SINTACTICO: incluir asignación ");
	}

	//<expr> ::= <comp_expr> (<comp_op> <comp_expr>)*
	private void expr() {
		comp_expr();
		comp_exprR();
	}

	private void comp_exprR() {
		if (es_comp_op()){
			comp_expr();
			comp_exprR();
		}
	}
	
	//<comp_op> ::= ‘<’ | ‘>’ | ‘<=’ | ‘>=’ | ‘==’ | ‘!=’
	private boolean es_comp_op() {
		if (tact.equals(oper.menor)){
			recon(oper.menor);
			return true;
		}else if (tact.equals(oper.mayor)){
			recon(oper.mayor);
			return true;
		}else if (tact.equals(oper.menori)){
			recon(oper.menori);
			return true;
		}else if (tact.equals(oper.mayori)){
			recon(oper.mayori);
			return true;
		}else if (tact.equals(oper.igual)){
			recon(oper.igual);
			return true;
		}else if (tact.equals(oper.distinto)){
			recon(oper.distinto);
			return true;
		}
		return false;
	}

	//<comp_expr> ::= <arit_expr> (<arit_op> <arit_expr>)*
	private void comp_expr() {
		arit_expr();
		arit_exprR();
	}

	private void arit_exprR() {
		if (es_arit_op()){
			arit_expr();
			arit_exprR();
		}
	}
	
	//<arit_op> ::= ‘+’ | ‘-‘ | ‘*’ | ‘**’ | ‘/’ | ‘//’ | ‘%’
	private boolean es_arit_op() {
		if (tact.equals(oper.sum)){
			recon(oper.sum);
			return true;
		}else if (tact.equals(oper.res)){
			recon(oper.res);
			return true;
		}else if (tact.equals(oper.mul)){
			recon(oper.mul);
			return true;
		}else if (tact.equals(oper.exp)){
			recon(oper.exp);
			return true;
		}else if (tact.equals(oper.div)){
			recon(oper.div);
			return true;
		}else if (tact.equals(oper.divent)){
			recon(oper.divent);
			return true;
		}else if (tact.equals(oper.mod)){
			recon(oper.mod);
			return true;
		}
		return false;
	}

	//<arit_expr> ::= <logic_expr> (<logic_op> <logic_expr>)*
	private void arit_expr() {
		logic_expr();
		logic_exprR();
	}

	private void logic_exprR() {
		if (es_logic_op()){
			logic_expr();
			logic_exprR();
		}
	}
	
	//<logic_op> ::= ‘and’ | ‘or’ | ‘xor’
	
	private boolean es_logic_op() {
		if (tact.equals(oper.and)){
			recon(oper.and);
			return true;
		}else if (tact.equals(oper.or)){
			recon(oper.or);
			return true;
		}else if (tact.equals(oper.xor)){
			recon(oper.xor);
			return true;
		}
		return false;
	}

	//<logic_expr> ::= '-' <un_expr> | 'not' <un_expr>
	private void logic_expr() {
		if(tact.equals(oper.res)){
			recon(oper.res);
			un_expr();
		} else if (tact.equals(palres.Not)){
			recon(palres.Not);
			un_expr();
		}
	}


	
	//<un_expr> ::= NAME | NUMBER | STRING | | ‘false’ | ‘true’ | ‘(‘ <expr> ‘)’ |  ‘[‘<expr> ( ‘,’ <expr>)* ‘]’
	private void un_expr() {
		if (tact.equals(tipoCodToken.ID)){
			recon(tipoCodToken.ID);
		}else if(tact.equals(tipoCodToken.STRING)){
			recon(tipoCodToken.STRING);
		}else if(tact.equals(tipoCodToken.ENTERO)){
			recon(tipoCodToken.ENTERO);
		}else if(tact.equals(tipoCodToken.REAL)){
			recon(tipoCodToken.REAL);
		}else if(tact.equals(palres.True)){
			recon(palres.True);
		}else if(tact.equals(palres.False)){
			recon(palres.False);
		}else if(tact.equals(delim.Aparentesis)){
			recon(delim.Aparentesis);
			expr();
			recon(delim.Cparentesis);
		}else if(tact.equals(delim.Acorchete)){
			recon(delim.Acorchete);
			expr();
			exprR();
			recon(delim.Ccorchete);
		}
		else
			errorsintactico("ERROR SINTACTICO: se esperaba una expresion o un terminal");
	}

	private void exprR() {
		if(tact.equals(delim.coma)){
			recon(delim.coma);
			expr();
			exprR();
		}
	}

	private void sent_comp() {
		
	}

	void recon(Object o){
		if (tact!=null){
		if (tact.getAtr()==o || tact.getCod()==o){
			tact=nextToken();
		}else{
			if (o.getClass() == tipoCodToken.class)
				errorsintactico("ERROR SINTACTICO: encontrado " + tact.getCod() + " se esperaba " + o);
			else
			{
				errorsintactico("ERROR SINTACTICO: encontrado " + tact.getAtr() + " se esperaba " + o);
			}
			tact=nextToken();
		}
		}
	}

	private void errorsintactico(String s) {
		ge.añadirError(ge.new error(s,tact.getFila(),tact.getColumna()));
	}

	private token nextToken() {
		return lex.getNextToken();
		
	}
	
}
