package AST.Visitor;

import AST.*;
import Symtab.*;

public class OLD_2_SemanticAnalysisVisitor implements ObjectVisitor {

	SymbolTable st = null;
	public int errors = 0;
	
	public void setSymtab(SymbolTable s)
	{
		st = s;
	}

	public SymbolTable getSymtab()
	{
		return st;
	}

	public void report_error(int line, String msg)
	{
		System.out.println(line+": "+msg);
		++errors;
	}
	
	// Display added for toy example language. Not used in regular MiniJava
	public Object visit(Display n) {
		n.e.accept(this);
		return null;
	}
	
	// MainClass m;
	// ClassDeclList cl;
	public Object visit(Program n) {
		n.m.accept(this);
		for (int i = 0; i < n.cl.size(); i++) {
			n.cl.get(i).accept(this);
		}
		return "This is a program";
	}

	// Identifier i1,i2;
	// Statement s;
	public Object visit(MainClass n) {
		n.i1.accept(this);
		n.i2.accept(this);
		n.s.accept(this);
		return null;
	}

	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Object visit(ClassDeclSimple n) {
		n.i.accept(this);
		st = st.findScope(n.i.s);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.get(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.get(i).accept(this);
		}
		st = st.exitScope();
		return null;
	}

	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Object visit(ClassDeclExtends n) {
		n.i.accept(this);
		n.j.accept(this);
		st = st.findScope(n.i.s);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.get(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.get(i).accept(this);
		}
		st = st.exitScope();
		return null;
	}

	// Type t;
	// Identifier i;
	public Object visit(VarDecl n) {
		n.t.accept(this);
		n.i.accept(this);
		return null;
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public Object visit(MethodDecl n) {
		n.t.accept(this);
		n.i.accept(this);
		st = st.findScope(n.i.s);
		for (int i = 0; i < n.fl.size(); i++) {
			n.fl.get(i).accept(this);
		}
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.get(i).accept(this);
		}
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.get(i).accept(this);
		}
		n.e.accept(this);
		st = st.exitScope();
		return null;
	}

	// Type t;
	// Identifier i;
	public Object visit(Formal n) {
		n.t.accept(this);
		n.i.accept(this);
		return null;
	}

	public Object visit(IntArrayType n) {
		return null;
	}

	public Object visit(BooleanType n) {
		return null;
	}

	public Object visit(IntegerType n) {
		return null;
	}

	// String s;
	public Object visit(IdentifierType n) {
		return null;
	}

	// StatementList sl;
	public Object visit(Block n) {
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.get(i).accept(this);
		}
		return null;
	}

	// Exp e;
	// Statement s1,s2;
	public Object visit(If n) {
		n.e.accept(this);
		n.s1.accept(this);
		if(n.s2 != null)
			n.s2.accept(this);
		return null;
	}

	// Exp e;
	// Statement s;
	public Object visit(While n) {
		n.e.accept(this);
		n.s.accept(this);
		return null;
	}

	// Exp e;
	public Object visit(Print n) {
		n.e.accept(this);
		return null;
	}

	// Identifier i;
	// Exp e;
	public Object visit(Assign n) {
		n.i.accept(this);
		n.e.accept(this);
		return null;
	}

	// Identifier i;
	// Exp e1,e2;
	public Object visit(ArrayAssign n) {
		n.i.accept(this);
		n.e1.accept(this);
		n.e2.accept(this);
		return null;
	}

	// Exp e1,e2;
	public Object visit(And n) {
		Object o1 = n.e1.accept(this);
		Object o2 = n.e2.accept(this);
		if(o1 == null || !(o1 instanceof String) || !o1.toString().equals("int")){
			report_error(n.e1.getLineNo(), "Expecting int type for LHS of And Expression");
		}
		if(o2 == null || !(o2 instanceof String) || !o2.toString().equals("int")){
			report_error(n.e2.getLineNo(), "Expecting int type for RHS of And Expression");
		}
		
		if(!(o1.toString().equals(o2.toString()))) {
			report_error(n.getLineNo(), "Mismatched LHS and RHS of And Expression");
		}
		return "boolean";
	}

	// Exp e1,e2;
	public Object visit(LessThan n) {
		Object o1 = n.e1.accept(this);
		Object o2 = n.e2.accept(this);
		if(o1 == null || !(o1 instanceof String) || !o1.toString().equals("int")){
			report_error(n.e1.getLineNo(), "Expecting int type for LHS of LT Expression");
		}
		if(o2 == null || !(o2 instanceof String) || !o2.toString().equals("int")){
			report_error(n.e2.getLineNo(), "Expecting int type for RHS of LT Expression");
		}
		
		if(!(o1.toString().equals(o2.toString()))) {
			report_error(n.getLineNo(), "Mismatched LHS and RHS of LT Expression");
		}
		return "boolean";
	}

	// Exp e1,e2;
	public Object visit(Plus n) {
		Object o1 = n.e1.accept(this);
		Object o2 = n.e2.accept(this);
		if(o1 == null || !(o1 instanceof String) || !o1.toString().equals("int")){
			report_error(n.e1.getLineNo(), "Expecting int type for LHS of Plus Expression");
		}
		if(o2 == null || !(o2 instanceof String) || !o2.toString().equals("int")){
			report_error(n.e2.getLineNo(), "Expecting int type for RHS of Plus Expression");
		}
		
		if(!(o1.toString().equals(o2.toString()))) {
			report_error(n.getLineNo(), "Mismatched LHS and RHS of Plus Expression");
		}
		return "int";
	}

	// Exp e1,e2;
	public Object visit(Minus n) {
		Object o1 = n.e1.accept(this);
		Object o2 = n.e2.accept(this);
		if(o1 == null || !(o1 instanceof String) || !o1.toString().equals("int")){
			report_error(n.e1.getLineNo(), "Expecting int type for LHS of Minus Expression");
		}
		if(o2 == null || !(o2 instanceof String) || !o2.toString().equals("int")){
			report_error(n.e2.getLineNo(), "Expecting int type for RHS of Minus Expression");
		}
		
		if(!(o1.toString().equals(o2.toString()))) {
			report_error(n.getLineNo(), "Mismatched LHS and RHS of Minus Expression");
		}
		return "int";
	}

	// Exp e1,e2;
	public Object visit(Times n) {
		Object o1 = n.e1.accept(this);
		Object o2 = n.e2.accept(this);
		if(o1 == null || !(o1 instanceof String) || !o1.toString().equals("int")){
			report_error(n.e1.getLineNo(), "Expecting int type for LHS of Times Expression");
		}
		if(o2 == null || !(o2 instanceof String) || !o2.toString().equals("int")){
			report_error(n.e2.getLineNo(), "Expecting int type for RHS of Times Expression");
		}
		
		if(!(o1.toString().equals(o2.toString()))) {
			report_error(n.getLineNo(), "Mismatched LHS and RHS of Times Expression");
		}
		return "int";
	}

	// Exp e1,e2;
	public Object visit(ArrayLookup n) {
		n.e1.accept(this);
		n.e2.accept(this);
		return null;
	}

	// Exp e;
	public Object visit(ArrayLength n) {
		n.e.accept(this);
		return null;
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public Object visit(Call n) {
		n.e.accept(this);
		n.i.accept(this);
		for (int i = 0; i < n.el.size(); i++) {
			n.el.get(i).accept(this);
		}
		
		
		return null;
	}

	// int i;
	public Object visit(IntegerLiteral n) {
		return "int";
	}

	public Object visit(True n) {
		return "boolean";
	}

	public Object visit(False n) {
		return "boolean";
	}

	// String s;
	public Object visit(IdentifierExp n) {
		Symbol s = st.lookupSymbol(n.s);
		if(s == null) {
			report_error(n.getLineNo(), "Identifier " + n.s + " is undefined.");
			return null;
		}
		return s.getType();
	}

	public Object visit(This n) {
		ASTNode a = st.getScope();
		

		return null;
	}

	// Exp e;
	public Object visit(NewArray n) {
		n.e.accept(this);
		Symbol s = st.lookupSymbol(n.e.toString());
		if(s == null) {
			report_error(n.getLineNo(), "Array " + n.e.toString() + " has not been declared.");
		}
		return null;
	}

	// Identifier i;
	public Object visit(NewObject n) {
		Symbol s = st.lookupSymbol(n.i.s);
		if(s == null) {
			report_error(n.getLineNo(), "Object " + n.i.s + " has not been declared.");
		}
		return null;
	}

	// Exp e;
	public Object visit(Not n) {
		n.e.accept(this);
		Symbol s = st.lookupSymbol(n.e.toString());
		if(s == null) {
			report_error(n.getLineNo(), "Negation " + n.e.toString() + " is undefined.");
		}
		return null;
	}

	// String s;
	public Object visit(Identifier n) {
		Symbol s = st.lookupSymbol(n.s);
		if(s == null) {
			report_error(n.getLineNo(), "Identifier " + n.s + " is undefined.");
		}
		return null;
	}
}