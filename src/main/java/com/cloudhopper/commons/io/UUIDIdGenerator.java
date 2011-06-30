package com.cloudhopper.commons.io;

import java.util.UUID;

/**
 * ID generator that produces a UUID.
 * @author garth
 */
public class UUIDIdGenerator
    implements IdGenerator
{

    public UUIDIdGenerator() {}

    @Override
    public Id newId() { return new Id(UUID.randomUUID().toString()); }

}