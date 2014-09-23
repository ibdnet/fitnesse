//Last Modified: 09/16/14
//Last Modified by: Deborah DeVine
//Last Modified Desc: Implement Spring

package fitnesseMain;

import fitnesse.ConfigurationParameter;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//import org.springframework.context.support.FileSystemXmlApplicationContext;

import fitnesse.ContextConfigurator;
import fitnesse.FitNesse;
//import fitnesse.PluginException;
import fitnesse.FitNesseContext;
import fitnesse.Updater;
import fitnesse.components.PluginsClassLoader;
//import fitnesse.http.SimpleResponse;
import fitnesse.reporting.ExitCodeListener;
//import fitnesse.reporting.TestTextFormatter;
import fitnesse.updates.UpdaterImplementation;

import java.io.*;
import java.util.concurrent.TimeUnit;
//import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
//import org.apache.catalina.connector.Connector;
//import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static fitnesse.ConfigurationParameter.*;

//Tell Spring to automatically inject any dependencies that are marked in
//our classes with @Autowired
@EnableAutoConfiguration

//Tell Spring to turn on WebMVC (e.g., it should enable the DispatcherServlet
//so that requests can be routed to our Controllers)
@EnableWebMvc
//Tell Spring that this object represents a Configuration for the
//application
//@EnableJpaRepositories(basePackageClasses = Video.class)	

@Configuration
//Tell Spring to go and scan our controller package (and all sub packages) to
//find any Controllers or other components that are part of our application.
//Any class in this package that is annotated with @Controller is going to be
//automatically discovered and connected to the DispatcherServlet.
@ComponentScan(basePackages = "fitnesseMain")
//@ComponentScan
//@Import(OAuth2SecurityConfiguration.class)

public class FitNesseMain  {
  private static final Logger LOG = Logger.getLogger(FitNesseMain.class.getName());
  
 // private static ApplicationContext applicationContext = (ApplicationContext) new ClassPathXmlApplicationContext("classpath*:/app.xml");
  private final ExitCodeListener exitCodeListener = new ExitCodeListener();

  private static Arguments arguments;
 // private static final SpringApplication app =  new SpringApplication();
 
  public static void main(String[] args) throws Exception {
	//arguments = null;  
   // applicationContext  = (ApplicationContext) new ClassPathXmlApplicationContext ("classpath*:/META-INF/app.xml");
    //setWebEnvironment(true);
	 
   // Arguments arguments = (Arguments) applicationContext.getBean("seychelles");

    try {
     arguments = new Arguments(args);
    // arguments = (Arguments) applicationContext.getBean("seychelles");
       arguments.setPort(8080);
      arguments.setOmitUpdate(true);
    } catch (IllegalArgumentException e) {
      Arguments.printUsage();
      exit(1);
    }
//    Integer exitCode = new FitNesseMain().launchFitNesse(arguments);
//    if (exitCode != null) {
//      exit(exitCode);
 //   } 
    //  SpringApplication.
   // app.setWebEnvironment(true);
	  SpringApplication.run(FitNesseMain.class, args);
  }

  public FitNesseMain () throws Exception {
	  
	  Integer exitCode = this.launchFitNesse(arguments);
	    if (exitCode != null) {
	      exit(exitCode);
	    } 
  
  }
 
 

  protected static void exit(int exitCode) {
    System.exit(exitCode);
  }


  public Integer launchFitNesse(Arguments arguments) throws Exception {
    ContextConfigurator contextConfigurator = ContextConfigurator.systemDefaults();
    contextConfigurator = contextConfigurator.updatedWith(System.getProperties());
    contextConfigurator = contextConfigurator.updatedWith(ConfigurationParameter.loadProperties(new File(arguments.getConfigFile(contextConfigurator))));
    contextConfigurator = arguments.update(contextConfigurator);

    return launchFitNesse(contextConfigurator);
  }

  public Integer launchFitNesse(ContextConfigurator contextConfigurator) throws Exception {
    configureLogging("verbose".equalsIgnoreCase(contextConfigurator.get(LOG_LEVEL)));
    loadPlugins(contextConfigurator.get(ConfigurationParameter.ROOT_PATH));

    if (contextConfigurator.get(COMMAND) != null) {
      contextConfigurator.withTestSystemListener(exitCodeListener);
    }

    FitNesseContext context = contextConfigurator.makeFitNesseContext();

    logStartupInfo(context);

    update(context);

    if ("true".equalsIgnoreCase(contextConfigurator.get(INSTALL_ONLY))) {
      return null;
    }

    return launch(context);
  }

  private boolean update(FitNesseContext context) throws IOException {
    if (!"true".equalsIgnoreCase(context.getProperty(OMITTING_UPDATES.getKey()))) {
      Updater updater = new UpdaterImplementation(context);
      return updater.update();
    }
    return false;
  }

  private void loadPlugins(String rootPath) throws Exception {
    new PluginsClassLoader(rootPath).addPluginsToClassLoader();
  }

  private Integer launch(FitNesseContext context) throws Exception {
   if (!"true".equalsIgnoreCase(context.getProperty(INSTALL_ONLY.getKey()))) {
      String command = context.getProperty(COMMAND.getKey());
      if (command != null) {
        String output = context.getProperty(OUTPUT.getKey());
      //  executeSingleCommand(context.fitNesse, command, output);

        return exitCodeListener.getFailCount();
      } 
//      else {
//        context.fitNesse.start();
//      }
    }
    return null;
  }


private void executeSingleCommand(FitNesse fitNesse, String command, String outputFile) throws Exception {

    LOG.info("Executing command: " + command);

    OutputStream os;

    boolean outputRedirectedToFile = outputFile != null;

    if (outputRedirectedToFile) {
      LOG.info("-----Command Output redirected to " + outputFile + "-----");
      os = new FileOutputStream(outputFile);
    } else {
      LOG.info("-----Command Output-----");
      os = System.out;
    }

    fitNesse.executeSingleCommand(command, os);
    fitNesse.stop();

    if (outputRedirectedToFile) {
      os.close();
    } else {
      LOG.info("-----Command Complete-----");
    }
  }

  private void logStartupInfo(FitNesseContext context) {
    // This message is on standard output for backward compatibility with Jenkins Fitnesse plugin.
    // (ConsoleHandler of JUL uses standard error output for all messages).
    System.out.println("Bootstrapping FitNesse, the fully integrated standalone wiki and acceptance testing framework.");
    
    LOG.info("root page: " + context.root);
    LOG.info("logger: " + (context.logger == null ? "none" : context.logger.toString()));
    LOG.info("authenticator: " + context.authenticator);
    LOG.info("page factory: " + context.pageFactory);
    LOG.info("page theme: " + context.pageFactory.getTheme());
    LOG.info("Starting FitNesse on port: " + context.port);
  }

  public void configureLogging(boolean verbose) {
    if (loggingSystemPropertiesDefined()) {
      return;
    }

    InputStream in = FitNesseMain.class.getResourceAsStream((verbose ? "verbose-" : "") + "logging.properties");
    try {
      LogManager.getLogManager().readConfiguration(in);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Log configuration failed", e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          LOG.log(Level.SEVERE, "Unable to close Log configuration file", e);
        }
      }
    }
    LOG.finest("Configured verbose logging");
  }

  private boolean loggingSystemPropertiesDefined() {
    return System.getProperty("java.util.logging.config.class") != null ||
            System.getProperty("java.util.logging.config.file") != null;
  }
  
 
  // This version uses the Tomcat web container and configures it to
 	// support HTTPS. The code below performs the configuration of Tomcat
 	// for HTTPS. Each web container has a different API for configuring
 	// HTTPS. 
 	//
 	// The app now requires that you pass the location of the keystore and
 	// the password for your private key that you would like to setup HTTPS
 	// with. In Eclipse, you can set these options by going to:
 	//    1. Run->Run Configurations
 	//    2. Under Java Applications, select your run configuration for this app
 	//    3. Open the Arguments tab
 	//    4. In VM Arguments, provide the following information to use the
 	//       default keystore provided with the sample code:
 	//
 	//       -Dkeystore.file=src/main/resources/private/keystore -Dkeystore.pass=changeit
 	//
 	//    5. Note, this keystore is highly insecure! If you want more securtiy, you 
 	//       should obtain a real SSL certificate:
 	//
 	//       http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html
 	//
//    @Bean
//    public EmbeddedServletContainerFactory servletContainer() {
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
//        tomcat.addAdditionalTomcatConnectors(createConnector());
//        return tomcat;
//    }
//    
     @Bean
  public EmbeddedServletContainerFactory servletContainer() {
      TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
      factory.setPort(arguments.getPort());
//      //factory.setSessionTimeout(10, TimeUnit.MINUTES);
//     // factory.addErrorPages(new ErrorPage(HttpStatus.404, "/notfound.html"));
//      
      return factory;
  }

//private Connector createConnector() {
	//Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
	//Connector connector = new Connector();
    //Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
   // try {
       // File keystore = new ClassPathResource("keystore").getFile();
       // File truststore = new ClassPathResource("keystore").getFile();
      //  connector.setScheme("http");
       // connector.setSecure(true);
     //   connector.setPort(arguments.getPort());
      //  protocol.setSSLEnabled(true);
//        protocol.setKeystoreFile(keystore.getAbsolutePath());
//        protocol.setKeystorePass("changeit");
//        protocol.setTruststoreFile(truststore.getAbsolutePath());
//        protocol.setTruststorePass("changeit");
//        protocol.setKeyAlias("apitester");
     //   return connector;
   // }
//    catch (IOException ex) {
//        throw new IllegalStateException("can't access keystore: [" + "keystore"
//                + "] or truststore: [" + "keystore" + "]", ex);
//    }

//}

//}
        
  
 	
 }