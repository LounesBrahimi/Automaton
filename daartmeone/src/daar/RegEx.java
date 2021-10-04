package daar;

import java.util.ArrayList;

public class RegEx {
	
	//MACROS
	static final int CONCAT = 0xC04CA7;
	static final int ETOILE = 0xE7011E;
	static final int ALTERN = 0xA17E54;
	static final int PROTECTION = 0xBADDAD;
	static final int PARENTHESEOUVRANT = 0x16641664;
	static final int PARENTHESEFERMANT = 0x51515151;
	static final int DOT = 0xD07;
	
	// expression reguliere
	private static String regEx;
	  
	/*
	 * constructeur
	 * */
	public RegEx(String regEx)	{
		this.regEx = regEx;
	}	
	
	/*
	 * Convertie regEx to arbre syntaxique
	 * */
	public static arbreSyntaxique parse() throws Exception {
		//BEGIN DEBUG: set conditionnal to true for debug example
		//if (false) throw new Exception();
	   // arbreSyntaxique example = exampleAhoUllman();
	   // if (false) return example;
	    //END DEBUG

	    ArrayList<arbreSyntaxique> result = new ArrayList<arbreSyntaxique>();
	    for (int i=0; i < regEx.length(); i++) {
	    	if (regEx.charAt(i) != '.')
	    	result.add(new arbreSyntaxique(charToRacine(regEx.charAt(i)),new ArrayList<arbreSyntaxique>()));
	    }
	    return parse(result);
	}
	
	private static int charToRacine(char c) {
		    if (c=='.') return DOT;
		    if (c=='*') return ETOILE;
		    if (c=='|') return ALTERN;
		    if (c=='(') return PARENTHESEOUVRANT;
		    if (c==')') return PARENTHESEFERMANT;
		    return (int)c;
	}

	private static arbreSyntaxique parse(ArrayList<arbreSyntaxique> result) throws Exception {
		while (containParenthese(result)) {
			result = processParenthese(result);
		}
		while (containEtoile(result)) {
			result = processEtoile(result);
		}
		while (containConcat(result)) {
			result = processConcat(result);
		}
		while (containAltern(result)) {
			result=processAltern(result);
		}

		if (result.size()>1) 
			throw new Exception();
		
		return removeProtection(result.get(0));
	}
	
	private static boolean containParenthese(ArrayList<arbreSyntaxique> trees) {
		for (arbreSyntaxique t: trees) {
			if (t.racine == PARENTHESEFERMANT || t.racine == PARENTHESEOUVRANT) 
		  		return true;
		    }
		return false;
	}
	
	private static ArrayList<arbreSyntaxique> processParenthese(ArrayList<arbreSyntaxique> trees) throws Exception {
		ArrayList<arbreSyntaxique> result = new ArrayList<arbreSyntaxique>();
		boolean found = false;
		for (arbreSyntaxique t: trees) {
			if (!found && t.racine == PARENTHESEFERMANT) {
				boolean done = false;
		        ArrayList<arbreSyntaxique> content = new ArrayList<arbreSyntaxique>();
		        while (!done && !result.isEmpty()) {
		          if (result.get(result.size()-1).racine==PARENTHESEOUVRANT) { 
		        	  done = true; result.remove(result.size()-1); 
		          }	else {
		        	  content.add(0,result.remove(result.size()-1));
		          }
		        }
		        if (!done) throw new Exception();
		        found = true;
		        ArrayList<arbreSyntaxique> subTrees = new ArrayList<arbreSyntaxique>();
		        subTrees.add(parse(content));
		        result.add(new arbreSyntaxique(PROTECTION, subTrees));
			} else {
		        result.add(t);
		    }
		}
	    if (!found) throw new Exception();
	    return result;
	}
	
	private static boolean containEtoile(ArrayList<arbreSyntaxique> trees) {
		for (arbreSyntaxique t: trees) {
			if (t.racine == ETOILE && t.sousArbre.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	private static ArrayList<arbreSyntaxique> processEtoile(ArrayList<arbreSyntaxique> trees) throws Exception {
		ArrayList<arbreSyntaxique> result = new ArrayList<arbreSyntaxique>();
		boolean found = false;
		for (arbreSyntaxique t: trees) {
			if (!found && t.racine == ETOILE && t.sousArbre.isEmpty()) {
		        if (result.isEmpty()) throw new Exception();
		        found = true;
		        arbreSyntaxique last = result.remove(result.size()-1);
		        ArrayList<arbreSyntaxique> subTrees = new ArrayList<arbreSyntaxique>();
		        subTrees.add(last);
		        result.add(new arbreSyntaxique(ETOILE, subTrees));
		    } else {
		        result.add(t);
		    }
		}
		return result;
	}
	
	private static boolean containConcat(ArrayList<arbreSyntaxique> trees) {
		boolean firstFound = false;
	    for (arbreSyntaxique t: trees) {
	    	if (!firstFound && t.racine!=ALTERN) {
	    		firstFound = true; 
	    		continue; 
	    	}
	    	if (firstFound) {
	    		if (t.racine!=ALTERN) 
	    			return true; 
	    		else 
	    			firstFound = false;
	    	}
	    }
	    return false;
	}
	
	private static ArrayList<arbreSyntaxique> processConcat(ArrayList<arbreSyntaxique> trees) throws Exception {
		ArrayList<arbreSyntaxique> result = new ArrayList<arbreSyntaxique>();
		boolean found = false;
		boolean firstFound = false;
		for (arbreSyntaxique t: trees) {
			if (!found && !firstFound && t.racine!=ALTERN) {
				firstFound = true;
		        result.add(t);
		        continue;
		    }
		    if (!found && firstFound && t.racine==ALTERN) {
		        firstFound = false;
		        result.add(t);
		        continue;
		    }
		    if (!found && firstFound && t.racine!=ALTERN) {
		        found = true;
		        arbreSyntaxique last = result.remove(result.size()-1);
		        ArrayList<arbreSyntaxique> subTrees = new ArrayList<arbreSyntaxique>();
		        subTrees.add(last);
		        subTrees.add(t);
		        result.add(new arbreSyntaxique(CONCAT, subTrees));
		    } else {
		        result.add(t);
		    }
		}
	    return result;
	  }
	
	private static boolean containAltern(ArrayList<arbreSyntaxique> trees) {
	    for (arbreSyntaxique t: trees) {
	    	if (t.racine == ALTERN && t.sousArbre.isEmpty()) {
	    		return true;
	    	}
	    }
	    return false;
	}
	
	private static ArrayList<arbreSyntaxique> processAltern(ArrayList<arbreSyntaxique> trees) throws Exception {
		ArrayList<arbreSyntaxique> result = new ArrayList<arbreSyntaxique>();
	    boolean found = false;
	    arbreSyntaxique gauche = null;
	    boolean done = false;
	    for (arbreSyntaxique t: trees) {
	    	if (!found && t.racine==ALTERN && t.sousArbre.isEmpty()) {
		        if (result.isEmpty()) {
		        	throw new Exception();
		        }
		        found = true;
		        gauche = result.remove(result.size()-1);
		        continue;
		    }
		    if (found && !done) {
		        if (gauche==null) {
		        	throw new Exception();
		        }
		        done=true;
		        ArrayList<arbreSyntaxique> subTrees = new ArrayList<arbreSyntaxique>();
		        subTrees.add(gauche);
		        subTrees.add(t);
		        result.add(new arbreSyntaxique(ALTERN, subTrees));
		    } else {
		        result.add(t);
		    }
	    }
		return result;
	}

	private static arbreSyntaxique removeProtection(arbreSyntaxique tree) throws Exception {
		if (tree.racine == PROTECTION && tree.sousArbre.size() != 1) {
	    	throw new Exception();
	    }
	    if (tree.sousArbre.isEmpty()) {
	    	return tree;
	    }
	    if (tree.racine == PROTECTION) {
	    	return removeProtection(tree.sousArbre.get(0));
	    }
	    ArrayList<arbreSyntaxique> subTrees = new ArrayList<arbreSyntaxique>();
	    for (arbreSyntaxique t: tree.sousArbre) {
	    	subTrees.add(removeProtection(t));
	    }
		return new arbreSyntaxique(tree.racine, subTrees);
	}
	
}
