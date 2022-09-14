package AST.Visitor;

import java.util.HashMap;
import java.util.Iterator;

import AST.*;
import Symtab.*;


public class OLD_SymTableVisitor implements Visitor {

	SymbolTable st = new SymbolTable();
	public int errors = 0;
	
	public SymbolTable getSymtab() {
		return st;
	}
	
	public void print()
	{
		st.print(0);
	}
	
	
	public String getTypeString(Type t) {
		/* TO DO */
		if( t instanceof IntegerType) {
			return "int";
		}
		else if( t instanceof BooleanType) {
			return "bool";
		}
		else if( t instanceof IntArrayType) {
			return "int[]";
		}
		else if( t instanceof IdentifierType) {
			return ((IdentifierType)t).s;
		}
		return "";
	}
	
	public void report_error(int line, String msg)
	{
		System.out.println(line+": "+msg);
		++errors;
	}	
	
	// MainClass m;
	// ClassDeclList cl;
	public void visit(Program n) {
		if ( n.m != null ) n.m.accept(this);
		if(n.cl != null) {
			for (int i = 0; i < n.cl.size(); i++) {
				n.cl.get(i).accept(this);
			}
		}
	}

	// Identifier i1,i2;
	// Statement s;
	public void visit(MainClass n) {
		/* TO DO */
		if(n == null) return;
		ClassSymbol si1 = new ClassSymbol(n.i1.toString(), "");
		st.addSymbol(si1);
		
		st = st.enterScope(n.i1.toString(), n);
		MethodSymbol ms = new MethodSymbol("main", "void");
		st.addSymbol(ms);

		st = st.enterScope("main", n);
		VarSymbol sta = new VarSymbol(n.i2.toString(), "String[]");
		st.addSymbol(sta);	
		if(n.s != null) n.s.accept(this);
		st = st.exitScope();
		
		for (Iterator<String> i = st.getMethodTable().keySet().iterator(); i.hasNext();) {
			String id = (String)i.next();
			Symbol sym = st.getMethodTable().get(id);
			if(sym instanceof MethodSymbol) {
				si1.addMethod((MethodSymbol)sym);
			}
		}
		
		for (Iterator<String> i = st.getVarTable().keySet().iterator(); i.hasNext();) {
			String id = (String)i.next();
			Symbol sym = st.getVarTable().get(id);
			if(sym instanceof VarSymbol) {
				si1.addVariable((VarSymbol)sym);
			}
		}
		st = st.exitScope();
	}

	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public void visit(ClassDeclSimple n) {
		/* TO DO */
		if(n == null) return;
		
		String className = n.i.toString();
		ClassSymbol cs = new ClassSymbol(className);
		st.addSymbol(cs);
		st = st.enterScope(className, n);
		
		// process variables
		if(n.vl != null) {
			for(int i = 0; i < n.vl.size(); i++) {
				VarDecl v = n.vl.get(i);
				v.accept(this);
				Symbol s = st.getSymbol(v.i.toString());
				if(s != null && s instanceof VarSymbol) {
					cs.addVariable((VarSymbol)s);
				}
			}
		}
		//process methods
		if(n.ml != null) {
			for(int i = 0; i < n.ml.size(); i++) {
				MethodDecl m = n.ml.get(i);
				m.accept(this);
				Symbol s = st.getSymbol(m.i.toString());
				if(s != null && s instanceof MethodSymbol) {
					cs.addMethod((MethodSymbol)s);
				}
			}
		}
		st = st.exitScope();
	}

	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public void visit(ClassDeclExtends n) {
		/* TO DO */
	if(n == null) return;
		
		String className = n.i.toString();
		ClassSymbol cs = new ClassSymbol(className);
		//ClassSymbol cs2 = new ClassSymbol(className2);
		st.addSymbol(cs);
		//st.addSymbol(cs2);
		ClassSymbol cs2 = (ClassSymbol)st.lookupSymbol(n.j.toString(), "ClassSymbol");
		
		if(cs2 != null) cs2.extendsClass(cs);

		st = st.enterScope(className, n);

		
		// process variables
		if(n.vl != null) {
			for(int i = 0; i < n.vl.size(); i++) {
				VarDecl v = n.vl.get(i);
				v.accept(this);
				Symbol s = st.getSymbol(v.i.toString());
				if(s != null && s instanceof VarSymbol) {
					cs.addVariable((VarSymbol)s);
				}
			}
		}
		//process methods
		if(n.ml != null) {
			for(int i = 0; i < n.ml.size(); i++) {
				MethodDecl m = n.ml.get(i);
				m.accept(this);
				Symbol s = st.getSymbol(m.i.toString());
				if(s != null && s instanceof MethodSymbol) {
					MethodSymbol ms = (MethodSymbol)s;
					String mname = ms.getName();
					MethodSymbol ms_ext = cs.getMethod(mname);
					if(ms_ext != null) {
						int p = cs.getMethods().indexOf(ms_ext);
						cs.getMethods().set(p, ms);
					}
					else {
						cs.addMethod(ms);
					}
				}
			}
		}
		st = st.exitScope();
	}

	// Type t;
	// Identifier i;
	public void visit(VarDecl n) {
		/* TO DO */
		String vname = n.i.toString();
		String vtype = getTypeString(n.t);
		VarSymbol vs = new VarSymbol(vname,vtype);
		st.addSymbol(vs); 
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public void visit(MethodDecl n) {
		/* TO DO */
		String mname = n.i.toString();
		String mtype = getTypeString(n.t);
		MethodSymbol ms = new MethodSymbol(mname,mtype);
		
		for(int i = 0; i < n.fl.size(); i++) {
			Formal f = n.fl.get(i);
			VarSymbol s = new VarSymbol(f.i.toString(), getTypeString(f.t));
			if(f != null) {
				ms.addParameter(s);
			}
		}
		st.addSymbol(ms);
		st.enterScope(n.i.toString(), n);
		for(int i = 0; i < n.fl.size(); i++) {
			n.fl.get(i).accept(this);
		}
		for(int i = 0; i < n.vl.size(); i++) {
			n.vl.get(i).accept(this);
		}
		for(int i = 0; i < n.sl.size(); i++) {
			n.sl.get(i).accept(this);
		}
		st.exitScope();
	}

	// Type t;
	// Identifier i;
	public void visit(Formal n) {
	    String fname = n.i.toString();
		String ftype = getTypeString(n.t);
		VarSymbol fs = new VarSymbol(fname,ftype);
		st.addSymbol(fs); 
	}

	// StatementList sl;
	public void visit(Block n) {
	}

	// Exp e;
	// Statement s1,s2;
	public void visit(If n) {
	}

	// Exp e;
	// Statement s;
	public void visit(While n) {
	}

	// Exp e;
	public void visit(Print n) {
	}

	// Identifier i;
	// Exp e;
	public void visit(Assign n) {
	}

	// Identifier i;
	// Exp e1,e2;
	public void visit(ArrayAssign n) {
	}

	// Exp e1,e2;
	public void visit(And n) {
	}

	// Exp e1,e2;
	public void visit(LessThan n) {
	}

	// Exp e1,e2;
	public void visit(Plus n) {
	}

	// Exp e1,e2;
	public void visit(Minus n) {
	}

	// Exp e1,e2;
	public void visit(Times n) {
	}

	// Exp e1,e2;
	public void visit(ArrayLookup n) {
	}

	// Exp e;
	public void visit(ArrayLength n) {
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public void visit(Call n) {
	}

	// int i;
	public void visit(IntegerLiteral n) {
	}

	public void visit(True n) {
	}

	public void visit(False n) {
	}

	public void visit(This n) {
	}

	// Exp e;
	public void visit(NewArray n) {
	}

	// Identifier i = new Identifier();
	public void visit(NewObject n) {
	}

	// Exp e;
	public void visit(Not n) {
	}

	// String s;
	public void visit(IdentifierExp n) {
	}

	// String s;
	public void visit(Identifier n) {
	}
	
	// int[] i;
	public void visit(IntArrayType n) {
	}

	// Bool b;
	public void visit(BooleanType n) {
	}

	// Int i;
	public void visit(IntegerType n) {
	}

	// String s;
	public void visit(IdentifierType n) {
	}	

	// Display added for toy example language. Not used in regular MiniJava
	public void visit(Display n) {
	}
}
