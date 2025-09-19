
import java.util.HashMap;
import java.util.Map;

public class Story {
    // Map zum Speichern der Storylines
    private Map<String, String> storyLines;

    // Konstruktor, um die Storylines zu initialisieren
    public Story() {
        storyLines = new HashMap<>();
        // Beispielhafte Storylines hinzufügen
        loadStorylines();
    }

    // Methode zum Laden von Storylines (hier einfach als Beispiel)
    private void loadStorylines() {
        // Story mit einem Schlüssel hinzufügen
        storyLines.put("intro", "Du befindest dich vor einem großen alten Gebäude, welches an ein Krankenhaus erinnert");
        storyLines.put("dragon_quest", "Du erfährst, dass ein Drache den Dungeon bewacht. Deine Reise beginnt.");
        storyLines.put("final_battle", "Du stehst dem Drachen gegenüber. Der Kampf beginnt!");
    }

    // Methode zum Abrufen einer Storyline anhand eines Keys
    public String getStory(String key) {
        return storyLines.getOrDefault(key, "Diese Story existiert nicht.");
    }

    // Methode zum Hinzufügen einer neuen Storyline
    public void addStory(String key, String storyText) {
        storyLines.put(key, storyText);
    }

    // Methode zum Entfernen einer Storyline
    public void removeStory(String key) {
        storyLines.remove(key);
    }

    public static void main(String[] args) {
        // Story-Objekt erstellen
        Story story = new Story();
        
        // Abrufen einer Storyline mit dem Key "intro"
        System.out.println("Intro Story: " + story.getStory("intro"));
        
        // Abrufen einer nicht vorhandenen Storyline
        System.out.println("Non-existent Story: " + story.getStory("unknown_key"));

        // Eine neue Storyline hinzufügen
        story.addStory("treasure_room", "Du betrittst einen geheimen Raum voller Schätze!");
        System.out.println("Treasure Room: " + story.getStory("treasure_room"));
    }
}
