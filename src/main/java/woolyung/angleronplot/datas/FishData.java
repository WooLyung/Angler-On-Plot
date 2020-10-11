package woolyung.angleronplot.datas;

public class FishData {
    public enum Rank {
        VALUELESS,
        COMMON,
        SPECIAL,
        RARE,
        LEGENDARY
    }

    public String name;
    public Rank rank;
    public float min_size;
    public float max_size;
    public float min_temp;
    public float max_temp;
    public float min_current;
    public float max_current;
    public float min_poll;
    public float max_poll;
}