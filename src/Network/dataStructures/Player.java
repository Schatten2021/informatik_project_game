package Network.dataStructures;

public class Player {
    public final String name;
    public float HP;
    public float MP;
    public float HPRegen;
    public float MPRegen;
    public Player(String name, float HP, float MP, float HPRegen, float MPRegen) {
        this.name = name;
        this.HP = HP;
        this.MP = MP;
        this.HPRegen = HPRegen;
        this.MPRegen = MPRegen;
    }
}
