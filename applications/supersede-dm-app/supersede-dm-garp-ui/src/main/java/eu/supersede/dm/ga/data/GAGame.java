package eu.supersede.dm.ga.data;

public class GAGame
{
    private static Long identifier = 0L;

    Long id;
    Long owner = 1L;
    String date = "";
    String status = "open";

    public GAGame()
    {
        id = identifier++;
    }

    public Long getOwner()
    {
        return this.owner;
    }

    public Long getId()
    {
        return this.id;
    }
}