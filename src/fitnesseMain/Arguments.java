 // Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesseMain;

import java.util.Properties;

import fitnesse.ContextConfigurator;
import util.CommandLine;

import static fitnesse.ConfigurationParameter.*;
import static fitnesse.ContextConfigurator.*;

public class Arguments {

  private final CommandLine commandLine = new CommandLine(
          "[-v][-p port][-d dir][-r root][-l logDir][-f config][-e days][-o][-i][-a credentials][-c command][-b output]");

  private String rootPath;
  private Integer port;
  private String rootDirectory;
  private String logDirectory;
  private boolean omitUpdate;
  private Integer daysTillVersionsExpire;
  private String credentials;
  private boolean installOnly;
  private String command;
  private String output;
  private String configFile;
  private boolean verboseLogging;

  public Arguments () {}
  
  public Arguments(String... args) {
   // if (!commandLine.parse(args)) {
     // throw new IllegalArgumentException("Can not parse command line");
   // }
    String port = commandLine.getOptionArgument("p", "port");
    this.port = port != null ? Integer.valueOf(port) : null;
    this.rootPath = commandLine.getOptionArgument("d", "dir");
    this.rootDirectory = commandLine.getOptionArgument("r", "root");
    this.logDirectory = commandLine.getOptionArgument("l", "logDir");
    final String days = commandLine.getOptionArgument("e", "days");
    this.daysTillVersionsExpire = days != null ? Integer.valueOf(days) : null;
    this.credentials = commandLine.getOptionArgument("a", "credentials");
    this.command = commandLine.getOptionArgument("c", "command");
    this.output = commandLine.getOptionArgument("b", "output");
    this.configFile = commandLine.getOptionArgument("f", "config");
    this.verboseLogging = commandLine.hasOption("v");
     this.omitUpdate = commandLine.hasOption("o");
    this.installOnly = commandLine.hasOption("i");
  }

 

/**
 * @return the rootPath
 */
public String getRootPath() {
	return rootPath;
}



/**
 * @param rootPath the rootPath to set
 */
public void setRootPath(String rootPath) {
	this.rootPath = rootPath;
}



/**
 * @return the port
 */
public Integer getPort() {
	return port;
}



/**
 * @param port the port to set
 */
public void setPort(Integer port) {
	this.port = port;
}



/**
 * @return the rootDirectory
 */
public String getRootDirectory() {
	return rootDirectory;
}



/**
 * @param rootDirectory the rootDirectory to set
 */
public void setRootDirectory(String rootDirectory) {
	this.rootDirectory = rootDirectory;
}



/**
 * @return the logDirectory
 */
public String getLogDirectory() {
	return logDirectory;
}



/**
 * @param logDirectory the logDirectory to set
 */
public void setLogDirectory(String logDirectory) {
	this.logDirectory = logDirectory;
}



/**
 * @return the omitUpdate
 */
public boolean isOmitUpdate() {
	return omitUpdate;
}



/**
 * @param omitUpdate the omitUpdate to set
 */
public void setOmitUpdate(boolean omitUpdate) {
	this.omitUpdate = omitUpdate;
}



/**
 * @return the daysTillVersionsExpire
 */
public Integer getDaysTillVersionsExpire() {
	return daysTillVersionsExpire;
}



/**
 * @param daysTillVersionsExpire the daysTillVersionsExpire to set
 */
public void setDaysTillVersionsExpire(Integer daysTillVersionsExpire) {
	this.daysTillVersionsExpire = daysTillVersionsExpire;
}



/**
 * @return the credentials
 */
public String getCredentials() {
	return credentials;
}



/**
 * @param credentials the credentials to set
 */
public void setCredentials(String credentials) {
	this.credentials = credentials;
}



/**
 * @return the installOnly
 */
public boolean isInstallOnly() {
	return installOnly;
}



/**
 * @param installOnly the installOnly to set
 */
public void setInstallOnly(boolean installOnly) {
	this.installOnly = installOnly;
}



/**
 * @return the command
 */
public String getCommand() {
	return command;
}



/**
 * @param command the command to set
 */
public void setCommand(String command) {
	this.command = command;
}



/**
 * @return the output
 */
public String getOutput() {
	return output;
}



/**
 * @param output the output to set
 */
public void setOutput(String output) {
	this.output = output;
}



/**
 * @return the configFile
 */
public String getConfigFile() {
	return configFile;
}



/**
 * @param configFile the configFile to set
 */
public void setConfigFile(String configFile) {
	this.configFile = configFile;
}



/**
 * @return the verboseLogging
 */
public boolean isVerboseLogging() {
	return verboseLogging;
}



/**
 * @param verboseLogging the verboseLogging to set
 */
public void setVerboseLogging(boolean verboseLogging) {
	this.verboseLogging = verboseLogging;
}



static void printUsage() {
    ContextConfigurator defaults = ContextConfigurator.systemDefaults();

    System.err.println("Usage: java -jar fitnesse.jar [-vpdrleoab]");
    System.err.println("\t-p <port number> {" + DEFAULT_PORT + "}");
    System.err.println("\t-d <working directory> {" +
      defaults.get(ROOT_PATH) + "}");
    System.err.println("\t-r <page root directory> {" +
      defaults.get(ROOT_DIRECTORY) + "}");
    System.err.println("\t-l <log directory> {no logging}");
    System.err.println("\t-f <config properties file> {" +
      defaults.get(CONFIG_FILE) + "}");
    System.err.println("\t-e <days> {" + defaults.getVersionDays() +
      "} Number of days before page versions expire");
    System.err.println("\t-o omit updates");
    System.err
      .println("\t-a {user:pwd | user-file-name} enable authentication.");
    System.err.println("\t-i Install only, then quit.");
    System.err.println("\t-c <command> execute single command.");
    System.err.println("\t-b <filename> redirect command output.");
    System.err.println("\t-v {off} Verbose logging");
  }

  public String getRootPath(ContextConfigurator configurator) {
    return rootPath == null ? configurator.get(ROOT_PATH) : rootPath;
  }

  public String getConfigFile(ContextConfigurator configurator) {
    return configFile == null ? (getRootPath(configurator) + "/" + configurator.get(CONFIG_FILE)) : configFile;
  }

  public ContextConfigurator update(ContextConfigurator defaults) {
    ContextConfigurator result = defaults;

    result = result.withParameter(LOG_LEVEL, verboseLogging ? "verbose" : "normal");
    if (configFile != null)
      result = result.withParameter(CONFIG_FILE, configFile);
    if (port != null)
      result = result.withPort(port);
    if (rootPath != null)
      result = result.withRootPath(rootPath);
    if (rootDirectory != null)
      result = result.withRootDirectoryName(rootDirectory);
    if (output != null)
      result = result.withParameter(OUTPUT, output);
    if (logDirectory != null)
      result = result.withParameter(LOG_DIRECTORY, logDirectory);
    if (daysTillVersionsExpire != null)
      result = result.withParameter(VERSIONS_CONTROLLER_DAYS, daysTillVersionsExpire.toString());
    if (omitUpdate)
      result = result.withParameter(OMITTING_UPDATES, "true");
    if (installOnly)
      result = result.withParameter(INSTALL_ONLY, "true");
    if (command != null)
      result = result.withParameter(COMMAND, command);
    if (credentials != null)
      result = result.withParameter(CREDENTIALS, credentials);

    return result;
  }

}
