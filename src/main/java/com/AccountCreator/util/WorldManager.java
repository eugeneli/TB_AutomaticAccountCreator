package com.AccountCreator.util;

import java.util.*;

public class WorldManager
{
    private ArrayList<String> f2p;
    //private ArrayList<String> p2p;

    public WorldManager()
    {
        f2p = new ArrayList<>(Arrays.asList("301", "308", "316", "326", "335", "381", "382", "383", "384"));
        Collections.shuffle(f2p, new Random(System.nanoTime()));


        /*
        p2p = new ArrayList<>(Arrays.asList("302", "303", "304", "305", "306", "309", "310", "311", "312",
                                            "313", "314", "317", "318", "319", "320", "321", "322", "327",
                                            "328", "329", "330", "333", "334", "336", "338", "341", "342",
                                            "343", "344", "345", "346", "349", "350", "351", "352", "353",
                                            "354", "357", "358", "359", "360", "361", "362", "365", "366",
                                            "367", "368", "369", "370", "373", "374", "375", "376", "377",
                                            "378", "386"));
        */
    }

    public String getRandomWorld()
    {
        //Warn if the amount of accounts they're creating exceeds the number of f2p worlds.
        return f2p.remove(0);
    }
}
