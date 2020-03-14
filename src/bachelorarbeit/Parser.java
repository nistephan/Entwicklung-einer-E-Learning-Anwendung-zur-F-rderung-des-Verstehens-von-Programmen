package bachelorarbeit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;
import org.apache.commons.text.StringEscapeUtils;

public class Parser {

	private static final int COUNT_INVOKES = 1000;
	private static final int COUNT_LINES = 10000;

	// 0: linenumber, 1: invoketype, 2: methodename, 3: signatur, 4: index in
	// constant pool
	private String[][] invokes = new String[COUNT_INVOKES][COUNT_INVOKES];
	private int invokeCount = 0;
	private String[][] invokesReadable = new String[COUNT_INVOKES][COUNT_INVOKES];
	private String[] linesJavaSource = new String[COUNT_LINES];
	private String classname;

	public Parser(String filename) {
		readFile(filename);
		parse(filename);
		convertInvokesReadable();
		createHTML(filename);
	}
	
	/**
	* reads the .java file and compiles it
	* 
	* @param filename path to the .java file
	*
	*/
	private void readFile(String filename) {
		readLineNumbers(filename);
		try {
			compile(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	* parses the .class file which was created with the .java file
	* 
	* @param filename path to the .java file
	*
	*/
	private void parse(String filename) {
		try {
			ClassParser parser = new ClassParser(filename.substring(0, filename.lastIndexOf(".")) + ".class");
			JavaClass clazz = parser.parse();		
			classname = clazz.getClassName();
			classname = classname.substring(classname.lastIndexOf(".") + 1);
			parsingMethods(clazz.getMethods());
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	/**
	* reads all lines in a .java file
	* 
	* @param filename path to the .java file
	*
	*/
	private void readLineNumbers(String filename) {
		FileReader fr = null;
		LineNumberReader lnr = null;
		String str;
		int countLines = 0;
		try {
			// create new reader
			fr = new FileReader(filename);
			lnr = new LineNumberReader(fr);
			// read lines till the end of the stream
			while ((str = lnr.readLine()) != null) {
				linesJavaSource[countLines++] = str;
			}
		} catch (FileNotFoundException e) {
			System.out.println("Das System kann die angegebene Datei nicht finden.");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// closes the stream and releases system resources
			if (fr != null)
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (lnr != null)
				try {
					lnr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	* compiles a .java file
	* 
	* @param filename path to the .java file
	*
	*/
	private void compile(String filename) throws IOException {
		Process pro = Runtime.getRuntime()
				.exec("javac " + filename.substring(0, filename.lastIndexOf("\\") + 1) + "*.java");
		BufferedReader stdError = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
		try {
			pro.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (pro.exitValue() != 0) {
			String s = null;
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
		}
	}

	/**
	* parses all methods in a .java file
	* 
	* @param filename path to the .java file
	*
	*/
	private void parsingMethods(Method[] m) {
		for (int i = 0; i < m.length; i++) {
			Code c = m[i].getCode();
			String codeString = c.toString();
			String[] lines = codeString.split("\n");
			String[][] instructions = new String[lines.length][100];

			// Startet bei 1, damit Code Zeile nicht enthalten ist
			for (int k = 1; k < lines.length; k++) {
				lines[k] = lines[k].replace(" ", ",").replace("\t", ",").replace(",,,,", ",").replace(",,,", ",")
						.replace(",,", ",").replace(":", "");
				String[] lineSplit = lines[k].split(",");
				for (int l = 0; l < lineSplit.length; l++) {
					instructions[k][l] = lineSplit[l];
				}

			}

			for (int j = 0; j < lines.length; j++) {
				if (instructions[j][1] != null) {
					if (instructions[j][1].equals("invokestatic") || instructions[j][1].equals("invokevirtual") || instructions[j][1].equals("invokespecial")) {
						invokes[invokeCount][0] = ""
								+ c.getLineNumberTable().getSourceLine(Integer.parseInt(instructions[j][0]));
						for (int k = 1; k < instructions[j].length; k++) {
							if (instructions[j][k] != null) {
								invokes[invokeCount][k] = instructions[j][k];
							}
						}
						invokeCount++;
					}
				}
			}

		}

	}

	/**
	* creates a HTML file
	* 
	* @param filename path to the .java file
	*
	*/
	private void createHTML(String filename) {
		StringBuilder html = new StringBuilder("");

		html.append("<!DOCTYPE html>\r\n" + "<html lang=\"de\">\r\n" + "<head>\r\n" + "<meta charset=\"utf-8\">\r\n"
				+ "<title>Java Invokes</title>\r\n"
				+ "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\" integrity=\"sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u\" crossorigin=\"anonymous\">\r\n"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\">\r\n"
				+ "<script src=\"main.js\"></script>\r\n" + "</head>\r\n" + "<body onload=\"onLoadPage();\">\r\n"
				+ "	<nav class=\"navbar navbar-inverse navbar-fixed-top\">\r\n"
				+ "		<div class=\"container-fluid\">\r\n" + "			<div class=\"navbar-header\">\r\n"
				+ "				<a class=\"navbar-brand\" href=\"#\">Java Invokes</a>\r\n" + "			</div>\r\n"
				+ "			<ul class=\"nav navbar-nav\">\r\n" + "				<li class=\"nav-item\">\r\n"
				+ "					<select class=\"form-control\" id=\"cb_mode\" onChange=\"setMode()\">\r\n"
				+ "						<option value=\"Mode_1\" id=\"Mode_1\" selected=\"selected\">Browse</option>\r\n"
				+ "						<option value=\"Mode_2\" id=\"Mode_2\">Quiz</option>\r\n"
				+ "					</select>\r\n" + "\r\n" + "				</li>\r\n"
				+ "				<li class=\"nav-item\">\r\n"
				+ "					<a class=\"nav-link\" href=\"help.html\">Hilfe</a>\r\n" + "				</li>\r\n"
				+ "			</ul>\r\n" + "		</div>\r\n" + "	</nav>\r\n" + "	<div class=\"content\">\r\n"
				+ "		");

		for (int i = 0; i < linesJavaSource.length; i++) {
			if (linesJavaSource[i] != null) {
				String escapedLineJavaSource = StringEscapeUtils.escapeHtml4(linesJavaSource[i]);
				html.append("<code class=\"accordion\" id =\"" + (i + 1) + "\" onclick=\"clickedLine(this)\">"
						+ escapedLineJavaSource + "</code><div id=\"" + (i + 1) + "panel\" class=\"panel\"></div>\r\n");
			}
		}

		html.append("\r\n" + "	</div>\r\n");

		html.append("<div class=\"result\"><p id=\"resultInvokes\">");

		for (int i = 0; i < invokes.length; i++) {
			for (int j = 0; j < invokes[i].length; j++) {
				if (invokes[i][j] != null) {
					html.append(StringEscapeUtils.escapeHtml4(invokesReadable[i][j] + ","));
				}
			}
			if (invokes[i][0] != null) {
				html.append("\n");
			}
		}

		html.append("</p></div>");

		html.append("\r\n" + "<script>\r\n" + "		var acc = document.getElementsByClassName(\"accordion\");\r\n"
				+ "		var i;\r\n" + "\r\n" + "		for (i = 0; i < acc.length; i++) {\r\n"
				+ "			acc[i].addEventListener(\"click\", function () {\r\n"
				+ "				var panel = this.nextElementSibling;\r\n"
				+ "				if (panel.innerHTML != \"\") {\r\n"
				+ "					this.classList.toggle(\"active\");\r\n"
				+ "					if (panel.style.display === \"block\") {\r\n"
				+ "						panel.style.display = \"none\";\r\n" + "					} else {\r\n"
				+ "						panel.style.display = \"block\";\r\n" + "					}\r\n"
				+ "				}\r\n" + "			});\r\n" + "		}\r\n" + "	</script></body>\r\n" + "</html>");

		String pathOfJar;
		FileOutputStream fileStream;
		try {
			pathOfJar = new File(Parser.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
			fileStream = new FileOutputStream(new File(pathOfJar.substring(0, pathOfJar.lastIndexOf("\\") + 1)
					+ filename.substring(filename.lastIndexOf("\\") + 1, filename.lastIndexOf(".")) + ".html"));
			OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");
			writer.write(html.toString());
			writer.close();
		} catch (FileNotFoundException e1) {
			System.out.println("Das System kann die angegebene Datei nicht finden.");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	* converts the invokes for human readable
	*
	*/
	private void convertInvokesReadable() {
		// deep copy of invokes (String[][])
		for (int i = 0; i < invokes.length; i++) {
			invokesReadable[i] = Arrays.copyOf(invokes[i], invokes[i].length);
		}
		for (int i = 0; i < invokesReadable.length; i++) {
			if (invokesReadable[i][0] != null) {
				if (invokesReadable[i][1] != null) {
					if (invokesReadable[i][1].contains("invokestatic")) {
						invokesReadable[i][1] = "Klassenmethode";
					} else if (invokesReadable[i][1].contains("invokevirtual")) {
						invokesReadable[i][1] = "Instanzmethode";
					} else if (invokesReadable[i][1].contains("invokespecial")) {
						if (invokesReadable[i][2].contains("<init>")) {
							invokesReadable[i][1] = "Konstruktor";
						} else {
							invokesReadable[i][1] = "Spezieller Aufruf";
						}
					}
				}
				if (invokesReadable[i][2] != null) {
					if(invokes[i][2].contains(".")) {
						invokesReadable[i][2] = invokesReadable[i][2].substring(0, invokes[i][2].lastIndexOf("."));
					}
				}
				if (invokesReadable[i][3] != null) {
					invokesReadable[i][3] = invokes[i][2].substring(invokes[i][2].lastIndexOf(".") + 1,
							invokes[i][2].length()) + Utility.signatureToString(invokesReadable[i][3]);
					if (invokesReadable[i][3].contains("<init>")) {
						if (invokesReadable[i][2].substring(invokesReadable[i][2].lastIndexOf(".") + 1)
								.equals("Object")) {
							invokesReadable[i][3] = invokesReadable[i][3].replace("<init>", classname);
						} else {
							invokesReadable[i][3] = invokesReadable[i][3].replace("<init>",
									invokesReadable[i][2].substring(invokesReadable[i][2].lastIndexOf(".") + 1));
						}

					}
					// deleted return value
					invokesReadable[i][3] = invokesReadable[i][3].substring(0,
							invokesReadable[i][3].lastIndexOf(")") + 1);
					// replace , with / so theres no problem with html syntax (will be replaced back
					// in javascript)
					invokesReadable[i][3] = invokesReadable[i][3].replaceAll(",", "/");
				}
			}
		}
	}
}
