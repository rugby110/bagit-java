package gov.loc.rdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    checkForCommand(args.length);

    String[] commandArgs = new String[args.length - 1];
    System.arraycopy(args, 1, commandArgs, 0, args.length - 1);

    try {
      doCommand(args[0], commandArgs);
    } catch (Exception e) {
      logger.error("Error! {}", e.toString());
    }
  }
  
  protected static void doCommand(String command, String[] commandArgs){
    try{
      if("upconvert".equalsIgnoreCase(command)){
        Converter.upConvert();
      }
      else if("downconvert".equalsIgnoreCase(command)){
        Converter.downConvert(commandArgs);
      }
      else if("help".equalsIgnoreCase(command)){
        HelpPrinter.help(commandArgs);
      }
      else{
        logger.error("Command [{}] is unrecognized!", command);
        HelpPrinter.printUsage();
        System.exit(-1);
      }
    }
    catch(Exception e){
      logger.error("Error while processing bag!", e);
      System.exit(-1);
    }
  }

  protected static void checkForCommand(int argsLength) {
    if (argsLength == 0) {
      HelpPrinter.printUsage();
      System.exit(-1);
    }
  }
}
