package com.AccountCreator.util;

import com.AccountCreator.model.RSAccount;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.text.WordUtils;
import org.tbot.bot.Account;
import org.tbot.methods.Skills;
import org.tbot.util.TEnvironment;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;

public class AccountManager
{
    private static WordDictionary dict = new WordDictionary();
    private static Random rand = new Random();

    private static final String CREATION_URL = "https://secure.runescape.com/m=account-creation/g=oldscape/create_account_funnel.ws";
    private static final String ACCOUNT_BACKUP_FILENAME = "TopbotAccountBackup.txt";
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 12;
    private static final double CHANCE_REPLACE_LETTER = 0.20;
    private static final double CHANCE_APPEND_NUMBER = 0.20;
    private static final double CHANCE_CAP_FIRST_LETTER = 0.50;
    private static final List<String> emailDomains = new ArrayList<>(Arrays.asList("@spambooger.com", "@devnullmail.com", "@aol.com", "@vk.com",
                                                                                    "@yahoo.com", "@live.com", "@facebook.com"));

    private static String genRandName()
    {
        //Select a random length and random word.
        String word = dict.getRandWord(MIN_NAME_LENGTH + rand.nextInt(MAX_NAME_LENGTH - MIN_NAME_LENGTH));

        //If word is too short, keep adding random words to it
        while(word.length() < MAX_NAME_LENGTH)
            word += dict.getRandWord(MAX_NAME_LENGTH - word.length());

        if(word.length() > MAX_NAME_LENGTH)
            word = word.substring(0, MAX_NAME_LENGTH - 1);

        //Random chance of replacing letters with numbers
        String finalWord = "";
        HashMap<String, String> map = dict.getMapping();
        for(char c : word.toCharArray())
        {
            if(map.containsKey(""+c))
            {
                if(rand.nextDouble() < CHANCE_REPLACE_LETTER)
                    finalWord += map.get(""+c);
            }
            else
                finalWord += ""+c;
        }

        //Random chance of appending number to end of name
        if(finalWord.length() < MAX_NAME_LENGTH && rand.nextDouble() < CHANCE_APPEND_NUMBER)
            finalWord += ""+ rand.nextInt(10);

        //Random chance of capitalizing first letter
        return rand.nextDouble() < CHANCE_CAP_FIRST_LETTER ? WordUtils.capitalize(finalWord) : finalWord;
    }

    public static RSAccount createAccount(String password)
    {
        return createAccount(genRandName(), password);
    }

    public static RSAccount createAccount(String name,String password)
    {
        String randEmail = emailDomains.get(rand.nextInt(emailDomains.size()));

        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(CREATION_URL);
        postMethod.addParameter("trialactive", "true");
        postMethod.addParameter("onlyOneEmail", "1");
        postMethod.addParameter("age", new Integer(18 + rand.nextInt(50)).toString());
        postMethod.addParameter("displayname_present", "true");
        postMethod.addParameter("displayname", name);
        postMethod.addParameter("email1", name + randEmail);
        postMethod.addParameter("password1", password);
        postMethod.addParameter("password2", password);
        postMethod.addParameter("agree_email", "on");
        postMethod.addParameter("agree_pp_and_tac", "1");
        postMethod.addParameter("submit", "Join+Now");

        System.out.println("Creating account: " + name + randEmail + "...");

        try {
            httpClient.executeMethod(postMethod);
            int statusCode = postMethod.getStatusCode();
            if(statusCode == HttpStatus.SC_CONTINUE ||
                statusCode == HttpStatus.SC_MOVED_TEMPORARILY ||
                statusCode == HttpStatus.SC_OK)
            {
                //String resp = postMethod.getResponseBodyAsString();
                //System.out.println(resp);

                RSAccount acct = new RSAccount();
                acct.name = name;
                acct.email = name + randEmail;
                acct.password = password;

                return acct;
            }
            else
            {
                System.out.println("Error creating account:" + name + randEmail);
            }
        } catch (HttpException e) {
            System.out.println("Error creating account:" + name + randEmail);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error creating account:" + name + randEmail);
            e.printStackTrace();
        }

        return null;
    }

    //Backs up existing accounts in topbot
    public static void backupAccounts()
    {
        try
        {
            Files.deleteIfExists(Paths.get(ACCOUNT_BACKUP_FILENAME));
            FileWriter fw = new FileWriter(ACCOUNT_BACKUP_FILENAME, true);
            fw.write("\r\n"); //New line to separate between batches of bots
            for(Account acct : TEnvironment.getAccounts())
                fw.write(acct.getUsername() + ":" + acct.getPassword() + "\r\n");

            fw.close();
        }
        catch (IOException e) {
            System.out.println("Accounts backup failed");
            e.printStackTrace();
        }
    }

    //Delete all existing accounts in topbot
    public static void deleteAllAccounts()
    {
        try {
            Files.delete(Paths.get("data.dat"));
        }
        catch (NoSuchFileException e) {
            System.out.println("Couldn't find data.dat. Is it in the same directory?");
        }
        catch (IOException e) {
            System.out.println("Accounts deletion failed");
            e.printStackTrace();
        }
    }

    public static void saveAccounts(ArrayList<RSAccount> accts)
    {
        for(RSAccount acct : accts)
        {
            TEnvironment.addAccount(new Account(acct.email, acct.password, "", Skills.Skill.Fishing));
            System.out.println("Adding account: " + acct.email);
        }
        TEnvironment.saveAccounts();
        System.out.println("Accounts saved");
    }

    public static void saveAccount(RSAccount acct)
    {
        TEnvironment.addAccount(new Account(acct.email, acct.password, "", Skills.Skill.Fishing));
        TEnvironment.saveAccounts();
        System.out.println("Saved account: " + acct.email);
    }
}
