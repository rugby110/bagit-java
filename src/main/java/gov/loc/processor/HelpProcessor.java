package gov.loc.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpProcessor{
  private static final Logger logger = LoggerFactory.getLogger(HelpProcessor.class);
  
  public static void help(String[] args){
    if(args.length == 0){
      logger.error("help command requires the name of a command as the argument!");
      System.exit(-1);
    }
    
    if(args.length > 1){
      logger.error("help command can only display one command at a time!");
    }
    
    switch(args[0]){
    case "create":
      printCreateHelp();
      break;
    case "verify":
      break;
    case "add":
      break;
    case "remove":
    case "rm":
      break;
    case "list":
    case "ls":
      break;
    case "help":
      break;
    default:
        logger.error("Unrecognized command [{}]!", args[0]);
    }
  }
  
  protected static void printCreateHelp(){
    String createUsage = "Usage: bagit create [--include <REGEX>] [--exclude <REGEX>]\n"
        +                "  creates a bag in the current directory\n"
        +                "  --include - An optional argument for only including files that match the given REGEX. Overrides any given excludes.\n"
        +                "  --exclude - An optional argument for excluding files that match the given REGEX.";
    logger.info(createUsage);
  }
  
  protected static void printVerifyHelp(){
    String verifyUsage = "Usage: bagit verify [--all] [--files] [--tags]\n"
        +                "  verifies that the bag files or tags have not changed. Defaults to --all\n"
        +                "  --all - An optional argument that tells bagit to check both files and tags\n"
        +                "  --files - An optional argument that tells bagit to check only files\n"
        +                "  --tags - An optional argument that tells bagit to check only the tag manifest";
    logger.info(verifyUsage);
  }
  
  protected static void printAddHelp(){
    String addUsage = "Usage: bagit add [--files FILE1 DIR1...] [--info <KEY>=<VALUE>]\n"
        +             "  adds files or key value pair information to the bag. You MUST choose one of the following:\n"
        +             "  --files - specify which files or directories to add to the bag. If you choose a directory all files and subdirectories are added from that directory.\n"
        +             "  --info - specify one or more key value pairs to be added to the bag information.";
    logger.info(addUsage);
  }
  
  protected static void printRemoveHelp(){
    String removeUsage = "Usage: bagit remove [--files FILE1 DIR1...] [--info <KEY>=<VALUE>]\n"
        +                "  Alias: rm\n"
        +                "  removes files or key value pair information from the bag. You MUST choose one of the following:\n"
        +                "  --files - specify which files or directories to be removed from the bag. If you choose a directory all files and subdirectories are removed from that directory.\n"
        +                "  --info - specify one or more key value pairs to be removed from the bag information.";
    logger.info(removeUsage);
  }
  
  protected static void printListHelp(){
    //TODO
    String listUsage = "";
    logger.info(listUsage);
  }
  
  protected static void printHelpHelp(){
    //TODO
    String helpUsage = "";
    logger.info(helpUsage);
  }
}
