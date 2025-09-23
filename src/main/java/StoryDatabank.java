public enum StoryDatabank {

    RAUM_1("INTRO_ROOM", """
        Du wachst benommen in einem zerschlagenen Labor auf. Blutspuren ziehen sich über den Boden, und flackernde Lichter tauchen alles in grelles Rot.
        Eine Tür quietscht offen – dein einziger Ausweg. Ein einsamer Zombie schlurft durch die Trümmer. Du greifst nach einer Eisenstange ...
    """),

    RAUM_2("FLOOR_ROOM", """
        Der Boden ist übersät mit herumliegenden Dokumenten und zerbrochenem Glas. Zwei schlurfende Zombies bemerken dich sofort.
        Es ist klar: Du bist nicht mehr in einem normalen Forschungskomplex – irgendetwas ist hier entsetzlich schiefgelaufen.
    """),

    RAUM_3("ZOMBIE_ROOM", """
        In diesem verlassenen Aufenthaltsraum liegt alles durcheinander. Essensreste, zerstörte Möbel – als wäre hier gekämpft worden.
        Zwei Mini-Zombies brechen aus einem Lüftungsschacht hervor. Du musst schnell reagieren!
    """),

    RAUM_4("PANTRY", """
        Du betrittst eine Vorratskammer. Die Regale sind geplündert. Zwischen leeren Dosen und Konservengläsern lauern zwei Zombies.
        Sie scheinen hier eingeschlossen gewesen zu sein – ausgehungert und wütend.
    """),

    RAUM_5("LIBRARY_ROOM", """
        Eine alte Forschungsbibliothek. Bücher liegen verstreut, Monitore sind zerschlagen. Drei Gestalten bewegen sich im Dunkeln.
        Es sind zwei Mini-Zombies – und ein ehemals menschlicher Wissenschaftler, mutiert und feindselig.
    """),

    RAUM_6("DINING_HALL", """
        Ein riesiger Speisesaal. Überall zerstörtes Mobiliar und Blutflecken. Du hörst das Kratzen von Nägeln auf Metall.
        Gleich fünf Gegner nähern sich aus verschiedenen Richtungen – Mini-Zombies und mutierte Wissenschaftler. Es gibt keinen Rückzug.
    """),

    RAUM_7("LABORATORY", """
        Du erreichst das zentrale Forschungslabor. Hier wurde alles dokumentiert – und alles ist fehlgeschlagen.
        Vier Wissenschaftler-Zombies greifen dich gleichzeitig an. Ihre Bewegungen sind seltsam koordiniert. Als wären sie noch bei Bewusstsein ...
    """),

    RAUM_8("CORRIDOR", """
        Ein langer, dunkler Verbindungsgang. Die Wände sind mit Kratzspuren überzogen. Du hörst ein tiefes Grollen.
        Drei gigantische Zombies stellen sich dir in den Weg. Diese Kreaturen sind langsamer, aber unglaublich stark.
    """),

    RAUM_9("PANTRY", """
        Noch eine Vorratskammer? Nein ... hier stimmt etwas nicht. Die Luft ist dick, das Licht flackert nervös.
        Zwei Mini-Zombies stürmen auf dich zu. Es scheint fast, als würden sie dich in Richtung der nächsten Tür treiben ...
    """),

    RAUM_10("FINAL_ROOM", """
        Du trittst durch die letzte Sicherheitstür. Ein gigantisches Labor öffnet sich vor dir. Maschinen pfeifen, Flüssigkeiten blubbern in Tanks.
        In der Mitte steht es: das Ergebnis aller Experimente. Der Endboss – ein mutierter Superzombie, aus Dutzenden Leichen zusammengesetzt.
        Du bist allein. Es gibt kein Zurück. Nur den Kampf.
    """),

    RAUM_11("WIN_ROOM", """
        Der Superzombie fällt mit einem markerschütternden Schrei. Das Labor beginnt zu beben – Systeme kollabieren, Lichter explodieren.
        Du findest eine Notausgangskapsel – schwer beschädigt, aber funktionstüchtig. Mit letzter Kraft schleppst du dich hinein.
        Sekunden später schießt die Kapsel durch das Dach in die Freiheit. Du hast überlebt.
    """),

    BOOSTER_HEALING_STATION("HEALING_STATION", """
        In einem abgetrennten Raum findest du eine medizinische Station. Die Systeme laufen noch.
        Du legst dich auf die Liege – Laser und Nadeln aktivieren sich. Deine Wunden heilen vollständig.
    """),

    BOOSTER_MEDKIT_ROOM("MEDKIT_ROOM", """
        Zwischen umgeworfenen Schränken findest du ein tragbares Medkit. Es enthält Schmerzmittel, Verbände und Desinfektionsmittel.
        Du versorgst deine schlimmsten Verletzungen – genug, um durchzuhalten.
    """),

    BOOSTER_REGEN_ZONE("REGEN_ZONE", """
        Ein seltsamer Raum – beleuchtet von pulsierendem, blauen Licht. Die Luft ist warm und ruhig.
        Während du dich ausruhst, spürst du, wie deine Kräfte langsam zurückkehren. Du regenerierst kontinuierlich etwas Lebensenergie.
    """);

    public String roomName;
    public String story;

    StoryDatabank(String roomName, String story) {
        this.roomName = roomName;
        this.story = story;
    }
}
