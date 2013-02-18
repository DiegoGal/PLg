package python;

import python.analizadorLexico.delim;
import python.analizadorLexico.palres;
import python.token.tipoCodToken;

public class analizadorSintactico {
	private analizadorLexico lex;
	private token tact; //token actual
	private token[] tarr; //array de tokens devueltos por scan
	private int i=0; //indice del array tarr
	
	analizadorSintactico(analizadorLexico lex){
		this.lex=lex;
	}
	
	/*<sentencia> ::= (NEWLINE |<sentencia_sim> | <sentencia_comp>)* 

<sentencia_sim> ::= (<expr_sen> | <del_sen> | <flow_sen> | <import_sen> | <global_sen> | <nonlocal_sen> ) NEWLINE
<expr_sen> ::=  NAME <asign> (<expr> | ‘None’ # equivalente a null de java
<asign> ::= (‘=’|'+=' | '-=' | '*=' | '/=' | '%=' | '&=' | '|=' | '^=' |'**=' | '//=')

<expr> ::= <comp_expr> (<comp_op> <comp_expr>)*
<comp_expr> ::= <arit_expr> (<arit_op> <arit_expr>)*
<arit_expr> ::= <logic_expr> (<logic_op> <logic_expr>)*
<logic_expr> ::= '-' <un_expr> | 'not' <un_expr>
<un_expr> ::= NAME | NUMBER | STRING | | ‘false’ | ‘true’ | ‘(‘ <expr> ‘)’ |  ‘[‘<expr> ( ‘,’ <expr>)* ‘]’
<arit_op> ::= ‘+’ | ‘-‘ | ‘*’ | ‘**’ | ‘/’ | ‘//’ | ‘%’
<comp_op> ::= ‘<’ | ‘>’ | ‘<=’ | ‘>=’ | ‘==’ | ‘!=’
<logic_op> ::= ‘and’ | ‘or’ | ‘xor’
<del_sen> ::= ‘del’ NAME ( ‘,’ NAME)*
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
		recon(tipoCodToken.FIN);
	}
	
	void sentencia(){
		if (tact.equals(delim.newline)){
			recon(tipoCodToken.DEL);
			sentencia();
		}
		else if(tact.getCod()==tipoCodToken.ID || tact.equals(palres.Del) ||
				tact.equals(palres.Break) ||	tact.equals(palres.Import) ||
				tact.equals(palres.Global) ||	tact.equals(palres.Nonlocal))
		{
			sent_sim();
		}
		else if (tact.equals(palres.If) ||	tact.equals(palres.While) ||
				tact.equals(palres.For) ||	tact.equals(palres.Def) ||
				tact.equals(palres.Class)){
			sent_comp();
		}
	}

	private void sent_sim() {
		if(tact.getCod()==tipoCodToken.ID){
			
		}
	}
	
	private void sent_comp() {
		
	}

	void recon(tipoCodToken codt){
		if (tact.getCod()==codt){
			tact=nextToken();
		}else{
			errorsintactico(codt);
		}
	}

	private void errorsintactico(tipoCodToken codt) {
		
	}

	private token nextToken() {
		if (tarr[i]!=null){
			i++;
			return tarr[i];
		}
		else {
			i=0;
			tarr=lex.scan();
			return tarr[i];
		}
	}
	
}
