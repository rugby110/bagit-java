package gov.loc;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.processor.AddProcessor;
import gov.loc.processor.CreateProcessor;
import gov.loc.processor.HelpProcessor;
import gov.loc.processor.ListProcessor;
import gov.loc.processor.RemoveProcessor;
import gov.loc.processor.UsageProcessor;
import gov.loc.processor.VerifyProcessor;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    checkForCommand(args.length);
    
    String[] commandArgs = new String[args.length -1];
    System.arraycopy(args, 1, commandArgs, 0, args.length -1);
    
    switch (args[0]){
      case "create":
        CreateProcessor.create(commandArgs);
        break;
      case "verify":
        VerifyProcessor.verify(commandArgs);
        break;
      case "add":
        AddProcessor.add(commandArgs);
        break;
      case "remove":
      case "rm":
        RemoveProcessor.remove(commandArgs);
        break;
      case "list":
      case "ls":
        ListProcessor.list(commandArgs);
        break;
      case "help":
        HelpProcessor.help(commandArgs);
        break;
      default:
        logger.error("Command [{}] is unrecognized!", args[0]);
        UsageProcessor.printUsage();
        System.exit(-1);
    }
  }
  
  protected static void checkForCommand(int argsLength){
    if(argsLength == 0){
      UsageProcessor.printUsage();
      System.exit(-1);
    }
  }
  
  protected static void checkForDotBagDir(){
    File dotBagDir = new File(System.getProperty("user.dir"));
    if(!dotBagDir.exists() || !dotBagDir.isDirectory()){
      logger.error("Could not locate {} directory. If this is an older bag version, please convert to new .bag structure.", dotBagDir);
    }
  }
}
