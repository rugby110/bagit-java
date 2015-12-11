package gov.loc.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.error.ArgumentException;

/**
 * Handles displaying generic and advanced help messages.
 */
public class HelpProcessor{
  private static final Logger logger = LoggerFactory.getLogger(HelpProcessor.class);
  
  public static void help(String[] args){
    if(args.length == 0){
      throw new ArgumentException("help command requires the name of a command as the argument!");
    }
    
    if(args.length > 1){
      throw new ArgumentException("help command can only display one command at a time!");
    }
    
    switch(args[0]){
      case "add":
        printAddHelp();
      break;
      case "create":
        printCreateHelp();
        break;
      case "convert":
        printConvertHelp();
        break;
      case "help":
        printHelpHelp();
        break;
      case "list":
      case "ls":
        printListHelp();
        break;
      case "remove":
      case "rm":
        printRemoveHelp();
        break;
      case "verify":
        printVerifyHelp();
        break;
      default:
        throw new ArgumentException("Unrecognized command " + args[0]);
    }
  }
  
  protected static void printCreateHelp(){
    String createUsage = "Usage: bagit create [--include <REGEX>] [--exclude <REGEX>]\n"
        +                "  creates a bag in the current directory\n"
        +                "  You may only pick ONE of the following:\n"
        +                "  --include - An optional argument for only including files that match the given REGEX.\n"
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
        +                "  --info - specify one or more keys in the key value pairs to be removed from the bag information.";
    logger.info(removeUsage);
  }
  
  protected static void printListHelp(){
    String listUsage = "Usage: bagit list [--files] [--info] [--missing]\n"
        +              "  Alias: ls\n"
        +              "  List files or key value pair information from the bag.\n"
        +              "  You may also list files in the current directory(and subdirectories) that are not currently included in the bag.\n"
        +              "  Defaults to --files\n"
        +              "  --files - list all files that are currently included in the bag\n"
        +              "  --info - list all the key value pair information in the bag\n"
        +              "  --missing - list all the files that are NOT currently included in the bag";
    logger.info(listUsage);
  }
  
  protected static void printHelpHelp(){
    String helpUsage = "Usage: bagit help COMMAND\n"
        +              "  Show more details for any of the commands\n"
        +              "  Where COMMAND is command you wish to know detailed help about.\n"
        +              "  Available commands are: create, verify, add, remove, list, help";
    logger.info(helpUsage);
  }
  
  protected static void printConvertHelp(){
    String helpUsage = "Usage: bagit convert\n"
        +              "  Convert old versions of bagit bags into the new .bag folder stucture\n"
        +              "  Available versions to convert are 0.93, 0.94, 0.95, 0.96, and 0.97";
    logger.info(helpUsage);
  }
  
  public static void printUsage(){
    String usage = "Usage: bagit <COMMAND> [ARGS]\n"
        +          "       Where <COMMAND> is any of the below commands and [ARGS] are option arguments for those commands.\n"
        +          "         <add> [--files --info] - add files/info to bag.\n"
        +          "         <convert> - convert version 0.93-0.97 into 1.0\n"
        +          "         <create> [--include --exclude] - create a bag\n"
        +          "         <help> <COMMAND> - show more details for any of the commands.\n"
        +          "         <list | ls> [--files --info --missing] - list files/tags in bag. Also can list files not in bag.\n"
        +          "         <remove | rm> [--files --info] - remove files/info from bag.\n"
        +          "         <verify> [--all --files --tags] - verify the files/tags match their hash.";
    
    logger.info(usage);
  }
}
