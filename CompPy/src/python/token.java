package python;

public class token {
		private tipoCodToken codToken;
		private Object atributo;
		enum tipoCodToken {ID, PAL_RES, OP, DEL, ENTERO, REAL, STRING,FIN};
		
		public token(){
			
		}
		
		public token(tipoCodToken cod, Object palres){
			
			codToken=cod;
			atributo=palres;
		}
		
		public tipoCodToken getCod(){
			return codToken;
		}
		
		public Object getAtr(){
			return atributo;
		}
		
		public boolean equals(Object o){
			if (this == o)
				return true;
			if(o==null)
				return false;
			if (atributo.getClass()!=o.getClass() && atributo!=o){
				return false;
			}
			return true;
		}
		
}
