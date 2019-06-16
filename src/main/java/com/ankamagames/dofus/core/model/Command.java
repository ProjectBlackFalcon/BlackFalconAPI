package com.ankamagames.dofus.core.model;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Command {

    private static final Logger log = Logger.getLogger(Command.class);

    private String command;
    private Map<String, Object> parameters;

    public Command() {
    }

    public Command(final String command, final Map<String, Object> parameters) {
        this.command = command;
        this.parameters = parameters;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(final Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public static String serialize(final Command command) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(command);
        } catch (JsonProcessingException e) {
            log.error("Could not serialize command", e);
            return null;
        }
    }


    public static Command deserialize(final String command) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(command, Command.class);
        } catch (IOException e) {
            log.error("Could not deserialize command", e);
            return null;
        }
    }

    @Override
    public String toString() {
        return "{" +
            "command='" + command + '\'' +
            ", parameters=" + parameters +
            '}';
    }
}
