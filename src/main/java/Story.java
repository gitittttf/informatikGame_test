public class Story {

    public Story() {
    }

    public void tellStory(StoryDatabank story) {
        switch (story) {
            case TEST_STORY -> System.out.println(story.getText());
            // weitere stories
            default -> System.out.prinln("Keine Story gefunden");
        }
    }
}
