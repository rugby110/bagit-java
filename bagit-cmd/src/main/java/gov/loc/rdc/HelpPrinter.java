package gov.loc.rdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpPrinter {
  private static final Logger logger = LoggerFactory.getLogger(HelpPrinter.class);
  private static final String USAGE = "Usage: bagit <COMMAND>" + System.lineSeparator()
      +          "       Where <COMMAND> is any of the below commands." + System.lineSeparator()
      +          "         <upconvert> - convert version 0.93-0.97 into 0.98" + System.lineSeparator()
      +          "         <downconvert> [VERSION]- convert version 0.98 into 0.93-0.97" + System.lineSeparator()
      +          "         <help> [COMMAND]- get more detailed help about a command (other than help)" + System.lineSeparator();
  
  private static final String UP_CONVERT = "Usage: bagit upconvert" + System.lineSeparator()
  +          "       convert the bag in the current directory to version 0.98 (.bagit)." + System.lineSeparator();
  
  private static final String DOWN_CONVERT = "Usage: bagit downconvert <VERSION>" + System.lineSeparator()
  +          "       convert the .bagit bag in the current directory to version specified by <VERSION>." + System.lineSeparator();
  
  public static void help(String[] args){
    if(args.length == 0){
      logger.error("help command requires the name of a command as the argument!");
      System.exit(-1);
    }
    
    if(args.length > 1){
      logger.error("help command can only display one command at a time!");
      System.exit(-1);
    }
    
    switch(args[0]){
      case "upconvert":
        printUpconvertHelp();
        break;
      case "downconvert":
        printDownconvertHelp();
        break;
      default:
        logger.error("Unrecognized command [{}]", args[0]);
        printUsage();
    }
  }
  
  protected static void printUpconvertHelp(){
    logger.info(UP_CONVERT);
  }
  
  protected static void printDownconvertHelp(){
    logger.info(DOWN_CONVERT);
  }
  
  public static void printUsage(){
    logger.info(USAGE);
  }
}
