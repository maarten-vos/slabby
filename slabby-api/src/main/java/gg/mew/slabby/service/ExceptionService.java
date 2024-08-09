package gg.mew.slabby.service;

import gg.mew.slabby.exception.SlabbyException;

import java.util.UUID;

public interface ExceptionService {

    void logToConsole(final String message, final Throwable exception);

    void logToPlayer(final UUID uniqueId, final SlabbyException exception);

    boolean tryCatch(final UUID uniqueId, final Runnable action);

}
