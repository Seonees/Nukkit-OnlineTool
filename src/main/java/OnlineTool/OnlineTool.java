package OnlineTool;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Config;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class OnlineTool extends PluginBase {
    public String token = "*****";
    public int group = 1;
    public String format = "На сервере сейчас: ONLINE/MAX_PLAYERS игроков!";
    public boolean debug = false;

    @Override
    public void onEnable(){
        createCfg();
        loadCfg();
        saveCfg();
    }
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		updateStatus();
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		updateStatus();
	}

    public void updateStatus(){
        if(debug){
            this.getLogger().notice("Отправлено с параметрами: group_id="+group+", text="+format.replace("ONLINE",String.valueOf(this.getServer().getOnlinePlayers().size())).replace("MAX_PLAYERS",String.valueOf(this.getServer().getMaxPlayers())));
        }
        sendGet("https://api.vk.com/method/status.set?group_id="+group+"&text="+format.replace("ONLINE",String.valueOf(this.getServer().getOnlinePlayers().size())).replace("MAX_PLAYERS",String.valueOf(this.getServer().getMaxPlayers())).replace(" ","%20")+"&access_token="+token);
    }

    private void sendGet(final String url) {
        Server.getInstance().getScheduler().scheduleAsyncTask(new AsyncTask() {
				@Override
				public void onRun() {
					try {
						URL obj = new URL(URLEncoder.encode(url,"UTF-8"));
						HttpURLConnection con = (HttpURLConnection) obj.openConnection();
						con.setRequestMethod("GET");
						con.setRequestProperty("User-Agent", "Mozilla/5.0");
						BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();
						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						in.close();
					} catch(Exception e){}
				}
			});

    }

    private void createCfg(){
        this.getDataFolder().mkdirs();
        File f = new File(this.getDataFolder(),"config.yml");
        if (!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException e) {}
        }
    }

    private void loadCfg(){
        Config cfg = this.getConfig();
        token = cfg.getNested("onlinetool.token","*****");
        group = cfg.getNested("onlinetool.group",1);
        format = cfg.getNested("onlinetool.format","На сервере сейчас: ONLINE/MAX_PLAYERS игроков!");
        debug = cfg.getNested("onlinetool.debug",false);
        if(token.length()!=85){
            this.getLogger().warning("Токен недействителен. Пожалуйста, заполните его в конфиге.");
        }
        if(group==1){
            this.getLogger().warning("ID группы недействителен. Пожалуйста, заполните его в конфиге.");
        }
    }

    private void saveCfg(){
        Config cfg = this.getConfig();
        cfg.setNested("onlinetool.token",token);
        cfg.setNested("onlinetool.group",group);
        cfg.setNested("onlinetool.format",format);
        cfg.setNested("onlinetool.debug",debug);
        this.saveConfig();
    }
}
