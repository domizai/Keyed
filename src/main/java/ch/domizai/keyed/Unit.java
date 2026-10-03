package ch.domizai.keyed;

/** Unit of a Timeline's t; keys, durations and effect times are all read in it. */
public enum Unit {
    // Real time, or the fixed step if set.
    SECOND,
    // Advances 1 per draw(), independent of real time.
    FRAME
}
