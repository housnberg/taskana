package pro.taskana.data.generation.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.data.generation.builder.WorkbasketStructureBuilder;


public class DomainPrinter {

	private static final Logger LOGGER = LoggerFactory.getLogger(DomainPrinter.class);
	private static final String LAYER_SEPERATOR = "---"; 
	
	public static void printStructureOfDomain(WorkbasketStructureBuilder domainBuilder) {
		printLayer("", domainBuilder.getLastGeneratedLayer());
	}
	
	private static void printLayer(String prefix, List<WorkbasketWrapper> wbOfLayer) {
		if(wbOfLayer != null) {
			for (WorkbasketWrapper wbLvl1 : wbOfLayer) {
				LOGGER.info(prefix + wbLvl1.toString());
				printLayer(prefix + LAYER_SEPERATOR, wbLvl1.getDirectChildren());
			}
		}
	}
}
