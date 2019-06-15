package com.ankamagames.dofus;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.ankamagames.dofus.api.ApiServer;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        final Options options = configParameters();
        final CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            log.error(e.getMessage());
            help(options);
        }

        ApiServer server = new ApiServer(Integer.parseInt(cmd.getOptionValue("port", "p")));
        server.start();

        log.info("ApiServer started on port: " + server.getPort());
    }


    /**
     * Configure the command line options
     *
     * @return Options
     */
    private static Options configParameters() {

        final Option portOption = Option.builder("p")
            .longOpt("port")
            .desc("Port to open the websocket")
            .hasArg(true)
            .argName("port")
            .required(true)
            .build();

        final Options options = new Options();

        options.addOption(portOption);
        return options;
    }

    /**
     * Print cmd help options for the user
     *
     * @param options Command line options
     */
    private static void help(final Options options) {
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp("Main", options);
        System.exit(0);
    }

}
