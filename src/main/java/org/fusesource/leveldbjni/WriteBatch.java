/*
 * Copyright (C) 2011, FuseSource Corp.  All rights reserved.
 *
 *     http://fusesource.com
 *
 * The software in this package is published under the terms of the
 * CDDL license a copy of which has been included with this distribution
 * in the license.txt file.
 */
package org.fusesource.leveldbjni;

import org.fusesource.hawtjni.runtime.*;

/**
 * Provides a java interface to the C++ leveldb::WriteBatch class.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class WriteBatch extends NativeObject {

    @JniClass(name="leveldb::WriteBatch", flags={ClassFlag.CPP})
    private static class WriteBatchJNI {
        static {
            DB.LIBRARY.load();
        }

        @JniMethod(flags={MethodFlag.CPP_NEW}, cast="leveldb::WriteBatch *")
        public static final native long create();
        @JniMethod(flags={MethodFlag.CPP_DELETE})
        public static final native void delete(@JniArg(cast="leveldb::WriteBatch *") long ptr);

        @JniMethod(flags={MethodFlag.CPP})
        static final native void Put(
                @JniArg(cast="leveldb::WriteBatch *") long ptr,
                @JniArg(cast="const leveldb::Slice *", flags={ArgFlag.BY_VALUE}) long key,
                @JniArg(cast="const leveldb::Slice *", flags={ArgFlag.BY_VALUE}) long value
                );

        @JniMethod(flags={MethodFlag.CPP})
        static final native void Delete(
                @JniArg(cast="leveldb::WriteBatch *") long ptr,
                @JniArg(cast="const leveldb::Slice *", flags={ArgFlag.BY_VALUE}) long key
                );

        @JniMethod(flags={MethodFlag.CPP})
        static final native void Clear(
                @JniArg(cast="leveldb::WriteBatch *") long ptr
                );

    }

    public WriteBatch() {
        super(WriteBatchJNI.create());
    }

    public void delete() {
        assertAllocated();
        WriteBatchJNI.delete(self);
        self = 0;
    }

    public void put(byte[] key, byte[] value) {
        NativeBuffer keyBuffer = new NativeBuffer(key);
        try {
            NativeBuffer valueBuffer = new NativeBuffer(value);
            try {
                put(keyBuffer, valueBuffer);
            } finally {
                valueBuffer.delete();
            }
        } finally {
            keyBuffer.delete();
        }
    }

    private void put(NativeBuffer keyBuffer, NativeBuffer valueBuffer) {
        Slice keySlice = new Slice(keyBuffer);
        try {
            Slice valueSlice = new Slice(valueBuffer);
            try {
                put(keySlice, valueSlice);
            } finally {
                valueSlice.delete();
            }
        } finally {
            keySlice.delete();
        }
    }

    private void put(Slice keySlice, Slice valueSlice) {
        assertAllocated();
        WriteBatchJNI.Put(self, keySlice.pointer(), valueSlice.pointer());
    }


    public void delete(byte[] key) {
        NativeBuffer keyBuffer = new NativeBuffer(key);
        try {
            delete(keyBuffer);
        } finally {
            keyBuffer.delete();
        }
    }

    private void delete(NativeBuffer keyBuffer) {
        Slice keySlice = new Slice(keyBuffer);
        try {
            delete(keySlice);
        } finally {
            keySlice.delete();
        }
    }

    private void delete(Slice keySlice) {
        assertAllocated();
        WriteBatchJNI.Delete(self, keySlice.pointer());
    }

    public void clear() {
        assertAllocated();
        WriteBatchJNI.Clear(self);
    }

}
