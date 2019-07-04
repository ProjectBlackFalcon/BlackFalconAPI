package com.ankamagames.dofus.core.network;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.ankamagames.dofus.core.network.frames.Frame;
import com.ankamagames.dofus.network.NetworkMessage;
import com.ankamagames.dofus.util.Reflection;

@SuppressWarnings("unchecked")
public class Processor {
    private static Vector<Class<? extends Frame>> processFrames = new Vector<Class<? extends Frame>>();

    private Map<String, Process> processTable;

    static {
        try {
            Class<?>[] classesArray = Reflection.getClassesInPackage("com.ankamagames.dofus.core.network.frames");
            for(Class<?> cl : classesArray)
                if(cl.getSuperclass() == Frame.class)
                    processFrames.add((Class<? extends Frame>) cl);
        } catch(Exception e) {
            throw new Error(e);
        }
    }

    public Processor(DofusConnector connector) {
        this.processTable = new HashMap<String, Process>();
        Frame frame;
        Method[] methods;
        String msgName;
        for(Class<? extends Frame> processFrame : processFrames) {
            try {
                frame = processFrame.getConstructor(DofusConnector.class).newInstance(connector);
            } catch(Exception e) {
                e.printStackTrace();
                return;
            }
            methods = processFrame.getDeclaredMethods();
            for(Method method : methods)
                if(method.getName().equals("process")) {
                    msgName = method.getParameterTypes()[0].getSimpleName();
                    this.processTable.put(msgName, new Process(frame, method));
                }
        }
    }

    public void processMessage(NetworkMessage msg) {
        Process process = this.processTable.get(msg.getClass().getSimpleName());
        if(process == null) //
            return;
        process.process(msg);
    }

    private class Process {
        private Frame processFrame;
        private Method processMethod;

        private Process(Frame processFrame, Method processMethod) {
            this.processFrame = processFrame;
            this.processMethod = processMethod;
        }

        private void process(NetworkMessage msg) {
            try {
                this.processMethod.invoke(processFrame, msg);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
