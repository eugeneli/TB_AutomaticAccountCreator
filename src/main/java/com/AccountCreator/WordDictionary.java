package com.AccountCreator;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class WordDictionary
{
    private final String DICTIONARY_FILE = "/dic.txt";
    private HashMap<Integer, ArrayList<String>> words = new HashMap<>();

    private HashMap<String, String> letterMapping = new HashMap<>();

    public WordDictionary()
    {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(DICTIONARY_FILE))))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                if(words.containsKey(line.length()))
                    words.get(line.length()).add(line);
                else
                    words.put(line.length(), new ArrayList<>());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        letterMapping.put("i", "1");
        letterMapping.put("I", "1");
        letterMapping.put("l", "1");
        letterMapping.put("L", "1");
        letterMapping.put("s", "5");
        letterMapping.put("S", "5");
        letterMapping.put("o", "0");
        letterMapping.put("O", "0");
        letterMapping.put("b", "6");
    }

    public String getRandWord(int length)
    {
        if(words.containsKey(length))
            return words.get(length).get(new Random().nextInt(words.get(length).size()));

        return RandomStringUtils.random(length);
    }

    public HashMap<String, String> getMapping() { return letterMapping; }
}
