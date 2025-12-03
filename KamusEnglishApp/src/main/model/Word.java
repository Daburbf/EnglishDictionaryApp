package main.model;

public class Word {
    private String english;
    private String indonesian;
    private String category;
    private String definitionEn;
    private String definitionId;
    private String gimmickType;
    
    public Word(String english, String indonesian, String category) {
        this(english, indonesian, category, "", "", null);
    }
    
    public Word(String english, String indonesian, String category, 
                String definitionEn, String definitionId, String gimmickType) {
        this.english = english;
        this.indonesian = indonesian;
        this.category = category;
        this.definitionEn = definitionEn;
        this.definitionId = definitionId;
        this.gimmickType = gimmickType;
    }

    public String getEnglish() { return english; }
    public String getIndonesian() { return indonesian; }
    public String getCategory() { return category; }
    public String getDefinitionEn() { return definitionEn; }
    public String getDefinitionId() { return definitionId; }
    public String getGimmickType() { return gimmickType; }
    
    public boolean hasGimmick() {
        return gimmickType != null && !gimmickType.isEmpty();
    }
    
    public boolean hasDefinition() {
        return definitionEn != null && !definitionEn.isEmpty() && 
               definitionId != null && !definitionId.isEmpty();
    }
    
    @Override
    public String toString() {
        if (!hasDefinition()) {
            return english + " - " + indonesian + " (" + category + ")";
        } else {
            return english + " - " + indonesian + " (" + category + ")\n" +
                   "EN: " + definitionEn + "\n" +
                   "ID: " + definitionId;
        }
    }
}