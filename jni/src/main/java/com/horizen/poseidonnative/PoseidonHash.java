package com.horizen.poseidonnative;

import com.horizen.librustsidechains.FieldElement;
import com.horizen.librustsidechains.Library;

import java.util.Arrays;

public class PoseidonHash implements AutoCloseable {

    public static final int HASH_LENGTH = 96;

    private long poseidonHashPointer;

    static {
        Library.load();
    }

    private PoseidonHash(long poseidonHashPointer) {
        if (poseidonHashPointer == 0)
            throw new IllegalArgumentException("poseidonHashPointer must be not null.");
        this.poseidonHashPointer = poseidonHashPointer;
    }

    private static native PoseidonHash nativeGetPoseidonHash(FieldElement[] personalization);

    public static PoseidonHash getInstance(){
        return nativeGetPoseidonHash(new FieldElement[0]);
    }

    public static PoseidonHash getInstance(FieldElement[] personalization)
    {
       return nativeGetPoseidonHash(personalization);
    }

    private native void nativeUpdate(FieldElement input);

    public void update(FieldElement input) {
        if (poseidonHashPointer == 0)
            throw new IllegalArgumentException("PoseidonHash instance was freed.");
        nativeUpdate(input);
    }

    private native FieldElement nativeFinalize();

    public FieldElement finalizeHash() {
        if (poseidonHashPointer == 0)
            throw new IllegalArgumentException("PoseidonHash instance was freed.");
        return nativeFinalize();
    }

    private native FieldElement nativeReset(FieldElement[] personalization);

    public FieldElement reset(FieldElement[] personalization) {
        if (poseidonHashPointer == 0)
            throw new IllegalArgumentException("PoseidonHash instance was freed.");
        return nativeReset(personalization);
    }

    public FieldElement reset() {
        if (poseidonHashPointer == 0)
            throw new IllegalArgumentException("PoseidonHash instance was freed.");
        return nativeReset(new FieldElement[0]);
    }

    // For backward compatibility with previous implementation, if needed
    public static FieldElement computePoseidonHash(FieldElement[] inputs){
        PoseidonHash digest = PoseidonHash.getInstance();
        for (FieldElement fe: inputs)
            digest.update(fe);
        FieldElement hashOutput = digest.finalizeHash();

        digest.freePoseidonHash();
        return hashOutput;
    }

    private native void nativeFreePoseidonHash();

    public void freePoseidonHash(){
        if (poseidonHashPointer != 0) {
            nativeFreePoseidonHash();
            poseidonHashPointer = 0;
        }
    }

    @Override
    public void close() throws Exception {
        freePoseidonHash();
    }
}
