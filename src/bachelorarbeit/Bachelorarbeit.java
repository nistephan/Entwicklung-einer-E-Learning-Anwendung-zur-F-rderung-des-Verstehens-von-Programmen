package bachelorarbeit;

class Bachelorarbeit {

	public static void main(final String[] args) {
		if(args.length == 1) {
			new Parser(args[0]);
		}else {
			System.out.println("Falsche Parameter übergeben. (Pfad der .java Datei angeben)");
		}
	}

}
