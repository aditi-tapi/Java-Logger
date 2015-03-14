/**
 * 
 */
package org.thesoftwarecraftsman.logging.javaLogger.modules.output.handler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.thesoftwarecraftsman.logging.javaLogger.core.Level;
import org.thesoftwarecraftsman.logging.javaLogger.modules.output.formater.Formater;

/**
 * @author kadary
 *
 */
public class Printer {

	private void writeInFile(FileWriter fw, Map<String, String> log, File logFile, double limitSize, Formater formater) throws IOException {

		if (limitSize ==  0 || logFile.length() <= limitSize) {
			//System.out.println("Log file size: " + logFile.length() + "Limit Size: " + limitSize);
			if(log.get("DATE") != null) {
				fw.write(String.format("%-5s %1s", log.get("DATE"), formater.getSeparator()));
			}

			if(log.get("FQCN") != null) {
				fw.write(String.format(" %-52s %5s",log.get("FQCN"), formater.getSeparator()));
			}

			if(log.get("LEVEL") != null) {
				fw.write(String.format(" %-5s %1s", log.get("LEVEL"), formater.getSeparator()));
			}

			if(log.get("MESSAGE") != null) {
				fw.write(String.format(" %-6s%n", log.get("MESSAGE")));
			}
			fw.close();

			//System.out.println("Log file size: " + logFile.length() + "Limit Size: " + limitSize);
		}

		else {
			File folder = logFile.getParentFile();
			int counter = 0;
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.getName().contains(logFile.getName().split("\\.")[0])) {
					if (fileEntry.getName().contains("_")) {
						int max;
						String[] tokens = fileEntry.getName().split("_");
						String firstPart = tokens[tokens.length - 1];
						if (firstPart != null && !firstPart.isEmpty()) {
							if (firstPart.split("\\.")[0].matches("\\d+(\\.\\d+)?")) {
								max = Integer.parseInt(firstPart.split("\\.")[0]);
								if (counter < max) {
									counter = max;
								}
							}
						}
					}						
				}
			}
			counter += 1;
			StringBuffer newName = new StringBuffer();
			newName.append(logFile.getName().split("\\.")[0] + "_" + counter);  
			if (logFile.getName().split("\\.").length > 1) {
				newName.append("." + logFile.getName().split("\\.")[1]);
			}
			//System.out.println(newName.toString());
			File newlogFile = new File(logFile.getParent() + "/" + newName.toString());
			fw.close();
			logFile.renameTo(newlogFile);
		}
	}

	public void write(Level level, String message, String fqcn, String handler, Formater formater, String logFilePath, int logFileLimitSize) {

		Map<String, String> log = new HashMap<String, String>();

		log = this.initMap(log, level, fqcn, message, formater);		

		if (handler.equals(ConsoleHandler.class.getName())) {
			if(log.get("DATE") != null)
				System.out.printf("%-5s %1s", log.get("DATE"), formater.getSeparator());

			if(log.get("FQCN") != null)
				System.out.printf(" %-52s %5s", log.get("FQCN"), formater.getSeparator());

			if(log.get("LEVEL") != null)
				System.out.printf(" %-5s %1s", log.get("LEVEL"), formater.getSeparator());

			if(log.get("MESSAGE") != null)
				System.out.printf(" %-6s%n", log.get("MESSAGE"));
		}

		else if (handler.equals(FileHandler.class.getName())) {
			this.writeToFile(level, message, fqcn, FileHandler.class.getName(), formater, logFilePath, logFileLimitSize);
		}
	}

	private void writeToFile(Level level, String message, String fqcn, String name,
			Formater formater, String logFilePath, double logFileLimitSize) {

		Map<String, String> log = new HashMap<String, String>();

		log = this.initMap(log, level, fqcn, message, formater);	

		FileWriter fw = null;			
		final File logFile = new File (logFilePath);
		final File parent_directory = logFile.getParentFile();

		try {

			if (null != parent_directory) {
				parent_directory.mkdirs();
			}

			fw = new FileWriter (logFile, true);

			this.writeInFile(fw, log, logFile, logFileLimitSize, formater);
		}
		catch (IOException exception) {
			System.out.print("Unable to write in file => ");
			exception.printStackTrace();
		}

		finally {
			try {
				fw.close();
			} catch (IOException e) {
				System.out.println ("Unable to close File => ");
				e.printStackTrace();
			}
		}	
	}

	private Map<String, String> initMap(Map<String, String> log, Level level, String fqcn, String message, 
			Formater formater) {
		Date date = new Date();
		long now = date.getTime();
		SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yy HH:mm:ss");

		if(formater.isDateEnabled()) {
			log.put("DATE", dateFormater.format(now));
		}

		if(formater.isFQCNEnabled()) {
			log.put("FQCN", fqcn);
		}

		if (formater.isLevelEnabled()) {
			log.put("LEVEL", level.getName());
		}

		if (formater.isMessageEnabled()) {
			log.put("MESSAGE", message);
		}

		return log;
	}
}
