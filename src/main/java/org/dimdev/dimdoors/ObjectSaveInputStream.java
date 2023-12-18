package org.dimdev.dimdoors;

import java.io.*;

@SuppressWarnings("unused")
public class ObjectSaveInputStream extends ObjectInputStream {

    //  private static Logger logger = LoggerFactory.getLogger(ObjectSaveInputStream.class);

    public ObjectSaveInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        ObjectStreamClass resultClassDescriptor = super.readClassDescriptor(); // initially streams descriptor
        Class<?> localClass; // the class in the local JVM that this descriptor represents.
        try {
            localClass = Class.forName(resultClassDescriptor.getName());
        } catch (ClassNotFoundException e) {
            //   logger.error("No local class for " + resultClassDescriptor.getName(), e);
            return resultClassDescriptor;
        }
        ObjectStreamClass localClassDescriptor = ObjectStreamClass.lookup(localClass);
        if (localClassDescriptor != null) { // only if class implements serializable
            final long localSUID = localClassDescriptor.getSerialVersionUID();
            final long streamSUID = resultClassDescriptor.getSerialVersionUID();
            if (streamSUID != localSUID) { // check for serialVersionUID mismatch.
                String s = "Overriding serialized class version mismatch: " + "local serialVersionUID = " + localSUID +
                        " stream serialVersionUID = " + streamSUID;
                Exception e = new InvalidClassException(s);
                //     logger.error("Potentially Fatal Deserialization Operation.", e);
                resultClassDescriptor = localClassDescriptor; // Use local class descriptor for deserialization
            }
        }
        return resultClassDescriptor;
    }
}