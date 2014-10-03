package oasis.oasissky;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author DarkSeraphim
 */
public class IslandIndicesUtil
{

    // Just a tad more OO and less breakable
    public static class Indices
    {
        private final int x, z;

        private Indices(int x, int z)
        {
            this.x = x;
            this.z = z;
        }

        public int getX()
        {
            return this.x;
        }

        public int getZ()
        {
            return this.z;
        }
    }


    // These variables keep track of the island indices generation
    private int r = 1;
    private int x = 1;
    private int z;

    // This queue will store freed (x,z) index tuples
    private LinkedList<Indices> queue = new LinkedList<Indices>();

    // To prevent errors, I use regex to check whether a queue entry
    // in the config is an actual legitimate entry
    private static final Pattern isInt = Pattern.compile("-?[0-9]+,-?[0-9]+");

    /**
     * @param data The place where it will load generator data from.
     * Generator data is the data that it will base the next free island coords on
     * <p>
     * <i>Make sure the data param uses the same path as
     * {@link IslandIndicesUtil#save(ConfigurationSection)}</i>
     */
    public IslandIndicesUtil(ConfigurationSection data)
    {
        r = data.getInt("r");
        x = data.getInt("x");
        z = data.getInt("z");
        String[] coords;
        for(String entry : data.getStringList("queue"))
        {
            if(!isInt.matcher(entry).matches())
                continue;
            coords = entry.split(",");
            if(coords.length != 2)
                continue;
            queue.add(new Indices(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
        }
    }

    /**
     * In the case you want to stop (onDisble()?) invoke this
     * with the same(!) ConfigurationSection as you load with
     *
     * This will maintain the generator state and ensures that
     * islands won't be used twice
     * @param data The ConfigurationSection to save to.
     * <p>
     * <i>Make sure the data param uses the same path as
     * {@link IslandIndicesUtil(ConfigurationSection)}</i>
     */
    public void save(ConfigurationSection data)
    {
        data.set("r", this.r);
        data.set("x", this.x);
        data.set("z", this.z);
        List<String> q = new ArrayList<String>();
        for(Indices entry : this.queue)
        {
            // Idk, just checking
            if(entry == null)
                continue;
            q.add(String.format("%d,%d", entry.getX(), entry.getZ()));
        }
        data.set("queue", q);
    }

    /**
     * In the case that you want to free an island
     * @param x The X index of the island
     * @param z The Z index of the island
     */
    public void freeIsland(int x, int z)
    {
        this.queue.add(new Indices(x, z));
    }

    /**
     * This will return a free location wrapped
     * in an Indices object
     */
    public Indices getNextIslandIndices()
    {
        Indices t = this.queue.poll();
        if(t != null)
            return t;
        if(r == 0)
        {
            r++;
            x++;
            return new Indices(0, 0);
        }
        t = new Indices(x, z);
        if(x == z)
        {
            if(z == r)
            {
                x--;
            }
            else
            {
                z++;
            }
        }
        else if(z == r)
        {
            if(x == -r)
            {
                z--;
            }
            else
            {
                x--;
            }
        }
        else if(x == -r)
        {
            if(z == -r)
            {
                x++;
            }
            else
            {
                z--;
            }
        }
        else //if(z == -r)
        {
            if(x == r)
            {
                z++;
            }
            else
            {
                x++;
            }
        }
        if(z == 0 && x == r)
        {
            r++;
            x++;
        }
        return t;
    }
}