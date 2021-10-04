package daar;

import java.util.List;
import java.util.Scanner;

public class SuppressionEpsilons {

	public static void printMatrix(List<Integer>[][] ndfaMatrix) {
		for (int i = 0; i < ndfaMatrix.length; i++) {
		    for (int j = 96; j < ndfaMatrix[i].length; j++) {
		        System.out.print(ndfaMatrix[i][j] + " ");
		    }
		    System.out.println();
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Welcome to Bogota, Mr. Thomas Anderson.");
		
		String regEx;
		
		Scanner scanner = new Scanner(System.in);
	    System.out.print("  >> Please enter a regEx: ");
	    regEx = scanner.next();
	    System.out.println("  >> Parsing regEx \""+regEx+"\".");
	    System.out.println("  >> ...");
	    
	    if (regEx.length()<1) {
	      System.err.println("  >> ERROR: empty regEx.");
	    } else {
	    	RegEx r = new RegEx(regEx);
	    	System.out.print("  >> ASCII codes: ["+(int)regEx.charAt(0));
	    	for (int i=1;i<regEx.length();i++) System.out.print(","+(int)regEx.charAt(i));
	    	System.out.println("].");
	    	try {
	        arbreSyntaxique ret = r.parse();
	        System.out.println("================================================");
	        System.out.println("  >> Tree result: \n"+ret.toString());
		    System.out.println("================================================");
		    NDFA n = new NDFA(25);
		    n.arbreToNDFA(ret);
		   // printMatrix(n.ndfaMatrix);
		    System.out.println("############");
		   NDFARemoveEpsilon nSansEpsilons = new NDFARemoveEpsilon(n.getNdfaMatrix(), n.getnLignes(), n.getnColonnes(), n.getNumeroEtat());
		    nSansEpsilons.supression();
		    printMatrix(nSansEpsilons.nouveauNdfaMatrix);
	    } catch (Exception e) {
	        System.err.println("  >> ERROR: syntax error for regEx \""+regEx+"\".");
	      }
	    }
	}
}
