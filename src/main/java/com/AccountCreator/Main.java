package com.AccountCreator;

import com.AccountCreator.model.RSAccount;
import com.AccountCreator.util.AccountManager;
import com.AccountCreator.util.WorldManager;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.ArrayList;

public class Main
{
    private static final String DEFAULT_PW = "asdasd123";
    private static final int DEFAULT_ACCOUNT_NUM = 3;
    private static final String DEFAULT_SCRIPT = "399";

    private static Options options = new Options();
    private static String tbUsername;
    private static String tbPassword;
    private static String botPassword;
    private static int numAccounts;
    private static String script;

    private static WorldManager worlds = new WorldManager();
    private static ArrayList<RSAccount> accounts = new ArrayList<>();

    public static void main(String[] args)
    {
        options.addOption("tbn", "topbotname", true, "Your Topbot account username")
                .addOption("tbp", "topbotpassword", true, "Your Topbot account password")
                .addOption("n", "numaccounts", true, "Number of new accounts to be made (default 3)")
                .addOption("bp", "botpassword", true, "Password of newly made accounts (default 'asdasd123')")
                .addOption("s", "script", true, "Script to run after logging in");

        if(parseArgs(args))
        {
            //Backup existing accounts in topbot
            AccountManager.backupAccounts();

            //Create accounts
            for(int i = 0; i < numAccounts; i++)
                accounts.add(AccountManager.createAccount(botPassword));

            //Add new accounts to topbot
            AccountManager.saveAccounts(accounts);

            //Launch Topbot for each account and start tutorial script
            for(RSAccount acct : accounts)
                launchTopbot(acct, tbUsername, tbPassword, script);
        }
    }

    public static void launchTopbot(RSAccount acct, String tbUsername, String tbPassword, String script)
    {
        try
        {
            //ID: 399 is the tutorial script. Add it to account if you don't have it.
            String cmdString = "java -jar topbotclient.jar -s %script% -a %acct% -n %tbn% -pw %tbp% -w %w%";

            cmdString = cmdString.replaceAll("%script%", script)
                                .replaceAll("%acct%", acct.email)
                                .replaceAll("%tbn%", tbUsername)
                                .replaceAll("%tbp%", tbPassword)
                                .replaceAll("%w%", worlds.getRandomWorld());

            Runtime.getRuntime().exec(cmdString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean parseArgs(String[] args)
    {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd;
        try
        {
            cmd = parser.parse(options, args);

            if(cmd.hasOption("tbn") && cmd.hasOption("tbp"))
            {
                tbUsername = cmd.getOptionValue("tbn");
                tbPassword = cmd.getOptionValue("tbp");
            }
            else
            {
                System.out.println("Please enter your Topbot username and password");
                return false;
            }

            if(cmd.hasOption("n"))
            {
                numAccounts = Integer.parseInt(cmd.getOptionValue("n"));

                if(numAccounts >= 9)
                    System.out.println("WARNING: The number of accounts exceeds the number of f2p worlds!");
            }
            else
                numAccounts = DEFAULT_ACCOUNT_NUM;

            if(cmd.hasOption("bp"))
                botPassword = cmd.getOptionValue("bp");
            else
                botPassword = DEFAULT_PW;

            if(cmd.hasOption("s"))
                script = cmd.getOptionValue("s");
            else
                script = DEFAULT_SCRIPT;

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't parse arguments");
            return false;
        }
    }
}
