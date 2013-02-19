/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.nio.serialization;

import com.hazelcast.nio.BufferObjectDataInput;

import java.io.IOException;
import java.util.Set;

/**
 * @mdogan 12/28/12
 */
public class DefaultPortableReader implements PortableReader {

    protected final ClassDefinition cd;
    private final PortableSerializer serializer;
    private final BufferObjectDataInput in;
    private final int offset;

    public DefaultPortableReader(PortableSerializer serializer, BufferObjectDataInput in, ClassDefinition cd) {
        this.in = in;
        this.serializer = serializer;
        this.cd = cd;
        this.offset = in.position();
    }

    public int getVersion() {
        return cd.getVersion();
    }

    public boolean hasField(String fieldName) {
        return cd.hasField(fieldName);
    }

    public Set<String> getFieldNames() {
        return cd.getFieldNames();
    }

    public int getFieldTypeId(String fieldName) {
        return cd.getFieldTypeId(fieldName);
    }

    public int getFieldClassId(String fieldName) {
        return cd.getFieldClassId(fieldName);
    }

    public int readInt(String fieldName) throws IOException {
        int pos = getPosition(fieldName);
        return in.readInt(pos);
    }

    public long readLong(String fieldName) throws IOException {
        int pos = getPosition(fieldName);
        return in.readLong(pos);
    }

    public String readUTF(String fieldName) throws IOException {
        final int currentPos = in.position();
        try {
            int pos = getPosition(fieldName);
            in.position(pos);
            return in.readUTF();
        } finally {
            in.position(currentPos);
        }
    }

    public boolean readBoolean(String fieldName) throws IOException {
        int pos = getPosition(fieldName);
        return in.readBoolean(pos);
    }

    public byte readByte(String fieldName) throws IOException {
        int pos = getPosition(fieldName);
        return in.readByte(pos);
    }

    public char readChar(String fieldName) throws IOException {
        int pos = getPosition(fieldName);
        return in.readChar(pos);
    }

    public double readDouble(String fieldName) throws IOException {
        int pos = getPosition(fieldName);
        return in.readDouble(pos);
    }

    public float readFloat(String fieldName) throws IOException {
        int pos = getPosition(fieldName);
        return in.readFloat(pos);
    }

    public short readShort(String fieldName) throws IOException {
        int pos = getPosition(fieldName);
        return in.readShort(pos);
    }

    public byte[] readByteArray(String fieldName) throws IOException {
        final int currentPos = in.position();
        try {
            int pos = getPosition(fieldName);
            in.position(pos);
            final int len = in.readInt();
            final byte[] values = new byte[len];
            for (int i = 0; i < len; i++) {
                values[i] = in.readByte();
            }
            return values;
        } finally {
            in.position(currentPos);
        }
    }

    public char[] readCharArray(String fieldName) throws IOException {
        final int currentPos = in.position();
        try {
            int pos = getPosition(fieldName);
            in.position(pos);
            final int len = in.readInt();
            final char[] values = new char[len];
            for (int i = 0; i < len; i++) {
                values[i] = in.readChar();
            }
            return values;
        } finally {
            in.position(currentPos);
        }
    }

    public int[] readIntArray(String fieldName) throws IOException {
        final int currentPos = in.position();
        try {
            int pos = getPosition(fieldName);
            in.position(pos);
            final int len = in.readInt();
            final int [] values = new int[len];
            for (int i = 0; i < len; i++) {
                values[i] = in.readInt();
            }
            return values;
        } finally {
            in.position(currentPos);
        }
    }

    public long[] readLongArray(String fieldName) throws IOException {
        final int currentPos = in.position();
        try {
            int pos = getPosition(fieldName);
            in.position(pos);
            final int len = in.readInt();
            final long[] values = new long[len];
            for (int i = 0; i < len; i++) {
                values[i] = in.readLong();
            }
            return values;
        } finally {
            in.position(currentPos);
        }
    }

    public double[] readDoubleArray(String fieldName) throws IOException {
        final int currentPos = in.position();
        try {
            int pos = getPosition(fieldName);
            in.position(pos);
            final int len = in.readInt();
            final double [] values = new double[len];
            for (int i = 0; i < len; i++) {
                values[i] = in.readDouble();
            }
            return values;
        } finally {
            in.position(currentPos);
        }
    }

    public float[] readFloatArray(String fieldName) throws IOException {
        final int currentPos = in.position();
        try {
            int pos = getPosition(fieldName);
            in.position(pos);
            final int len = in.readInt();
            final float [] values = new float[len];
            for (int i = 0; i < len; i++) {
                values[i] = in.readFloat();
            }
            return values;
        } finally {
            in.position(currentPos);
        }
    }

    public short[] readShortArray(String fieldName) throws IOException {
        final int currentPos = in.position();
        try {
            int pos = getPosition(fieldName);
            in.position(pos);
            final int len = in.readInt();
            final short [] values = new short[len];
            for (int i = 0; i < len; i++) {
                values[i] = in.readShort();
            }
            return values;
        } finally {
            in.position(currentPos);
        }
    }

    public Portable readPortable(String fieldName) throws IOException {
        FieldDefinition fd = cd.get(fieldName);
        if (fd == null) {
            throw throwUnknownFieldException(fieldName);
        }
        final int currentPos = in.position();
        try {
            int pos = getPosition(fd);
            in.position(pos);
            final boolean NULL = in.readBoolean();
            if (!NULL) {
                final ContextAwareDataInput ctxIn = (ContextAwareDataInput) in;
                try {
                    ctxIn.setDataClassId(fd.getClassId());
                    return serializer.read(in);
                } finally {
                    ctxIn.setDataClassId(cd.getClassId());
                }
            }
            return null;
        } finally {
            in.position(currentPos);
        }
    }

    private HazelcastSerializationException throwUnknownFieldException(String fieldName) {
        return new HazelcastSerializationException("Invalid field name: '" + fieldName
                + "' for ClassDefinition {id: " + cd.getClassId() + ", version: " + cd.getVersion() + "}");
    }

    public Portable[] readPortableArray(String fieldName) throws IOException {
        FieldDefinition fd = cd.get(fieldName);
        if (fd == null) {
            throw throwUnknownFieldException(fieldName);
        }
        final int currentPos = in.position();
        try {
            int pos = getPosition(fd);
            in.position(pos);
            final int len = in.readInt();
            final Portable[] portables = new Portable[len];
            if (len > 0) {
                final int offset = in.position();
                final ContextAwareDataInput ctxIn = (ContextAwareDataInput) in;
                try {
                    ctxIn.setDataClassId(fd.getClassId());
                    for (int i = 0; i < len; i++) {
                        final int start = in.readInt(offset + i * 4);
                        in.position(start);
                        portables[i] = serializer.read(in);
                    }
                } finally {
                    ctxIn.setDataClassId(cd.getClassId());
                }
            }
            return portables;
        } finally {
            in.position(currentPos);
        }
    }

    protected int getPosition(String fieldName) throws IOException {
        FieldDefinition fd = cd.get(fieldName);
        if (fd == null) {
            throw throwUnknownFieldException(fieldName);
        }
        return getPosition(fd);
    }

    protected int getPosition(FieldDefinition fd) throws IOException {
        return in.readInt(offset + fd.getIndex() * 4);
    }
}
