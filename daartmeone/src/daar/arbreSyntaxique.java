package daar;

import java.util.ArrayList;


public class arbreSyntaxique {

	protected int racine;
	protected ArrayList<arbreSyntaxique> sousArbre;
	
	/*
	 * Constructeur
	 * */
	public arbreSyntaxique(int racine, ArrayList<arbreSyntaxique> sousArbre) {
		  this.racine = racine;
		  this.sousArbre = sousArbre;
	}
	
	//FROM TREE TO PARENTHESIS
	public String toString() {
	  if (sousArbre.isEmpty()) return racineToString();
	  String result = racineToString()+"("+sousArbre.get(0).toString();
	  for (int i=1; i<sousArbre.size(); i++) result+=","+sousArbre.get(i).toString();
	  return result+")";
	}
	
	private String racineToString() {
		  if (this.racine == RegEx.CONCAT) return ".";
		  if (this.racine == RegEx.ETOILE) return "*";
		  if (this.racine == RegEx.ALTERN) return "|";
		  if (this.racine == RegEx.DOT) return ".";
		  return Character.toString((char)racine);
		}
}
