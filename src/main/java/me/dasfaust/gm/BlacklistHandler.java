package me.dasfaust.gm;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import me.dasfaust.gm.tools.GMLogger;
import me.dasfaust.gm.trade.WrappedStack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BlacklistHandler
{
	public static boolean cauldron = false;
	public static Gson gson;
	public static File blacklistFile;
	public static List<String> blacklist;
	
	public static void init()
	{
		gson = new Gson();
		
		if (Core.isCauldron)
		{
			GMLogger.debug("We're running on Cauldron!");
			cauldron = true;
		}
		
		blacklist = new ArrayList<>();
		
		reload();
	}

	public static void reload()
	{
		blacklistFile = new File(Core.instance.getDataFolder().getAbsolutePath() + File.separator + "blacklist.json");
		if (!blacklistFile.exists())
		{
			try
			{
				if (!blacklistFile.createNewFile()) {
					GMLogger.severe("Can't create blacklist.json:");
					return;
				}
			}
			catch (IOException e)
			{
				GMLogger.severe(e, "Can't create blacklist.json:");
				return;
			}
			try
			{
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(blacklistFile), StandardCharsets.UTF_8));
				out.write(gson.toJson(blacklist));
				out.close();
			}
			catch (IOException e)
			{
				GMLogger.severe(e, "Can't write to blacklist.json:");
				return;
			}
		}
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(blacklistFile), StandardCharsets.UTF_8));
			StringBuilder all = new StringBuilder();
			String cur;
			while((cur = in.readLine()) != null)
			{
				all.append(cur);
			}
			in.close();
			blacklist = Lists.newArrayList(gson.fromJson(all.toString().toLowerCase(), String[].class));
			GMLogger.debug(String.format("Blacklist has %s entries", blacklist.size()));
		}
		catch (Exception e)
		{
			GMLogger.severe(e, "Can't load blacklist.json:");
		}
	}
	
	public static boolean check(WrappedStack stack)
	{
		GMLogger.debug(String.format("Checking item [%s:%s] against blacklist...", stack.getMaterial().toString(), stack.getDamage()));
		String id = String.format("%s:%s", stack.getMaterial().toString().toLowerCase(), stack.getDamage());
		String idAny = String.format("%s:%s", stack.getMaterial().toString().toLowerCase(), -1);
		GMLogger.debug(id);
		return blacklist.contains(id) || blacklist.contains(idAny);
	}
}
