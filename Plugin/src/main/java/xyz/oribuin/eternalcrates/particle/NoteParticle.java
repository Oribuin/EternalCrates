package xyz.oribuin.eternalcrates.particle;

import java.util.Arrays;
import java.util.Optional;

public enum NoteParticle {
    GREEN_1(0), // there are literally like, 7 different greens, bite me
    GREEN_2(1),
    DARK_YELLOW(2), // looks kinda like dehydrated piss yellow
    LIGHT_ORANGE(3),
    ORANGE(4),
    DARK_ORANGE(5),
    RED(6),
    CRIMSON(8),
    LIGHT_CRIMSON(9),
    LIGHT_PURPLE(10),
    PURPLE(11),
    DARK_PURPLE(12),
    MIDNIGHT_BLUE(13),
    DARK_BLUE(14),
    BLUE(15),
    LIGHT_DARK_BLUE(16), // literally how else would you describe it.
    LIGHT_BLUE(16),
    LIGHTER_BLUE(17),
    MINT_BLUE(18),
    MINT_GREEN(19),
    LIGHT_GREEN(20), // fuck anything after 20, it's the same throughout the whole thing
    LIME_GREEN(21),
    LIMER_GREEN(22), // It's literally 5 forms of lime green man what am I supposed to do
    LIGHT_LIME_GREEN(23),
    LIGHT_LIMER_GREEN(24); // I ran out of greens

    private final int noteNumber;

    NoteParticle(int note) {
        this.noteNumber = note;
    }

    public int getNoteNumber() {
        return noteNumber;
    }

    public Optional<NoteParticle> matchNote(int note) {
        return Arrays.stream(NoteParticle.values()).filter(particle -> particle.getNoteNumber() == note).findFirst();
    }
}