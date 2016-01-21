package OnlineTool;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.*;
import java.net.*;

public class OnlineTool extends PluginBase {
	public int update = 5;
	public String token = "*****";
	public int group = 1;
	public String format = "На сервере сейчас: ONLINE/MAX_PLAYERS игроков!";
        public boolean debug = false;
	
	@Override
	public void onEnable(){
		createCfg();
		loadCfg();
		saveCfg();
		updateStatus();
	}

    public void upd(){
        if(debug){
             this.getLogger().notice("Отправлено с параметрами: group_id="+group+", text="+format.replace("ONLINE",String.valueOf(this.getServer().getOnlinePlayers().size())).replace("MAX_PLAYERS",String.valueOf(this.getServer().getMaxPlayers())));
        }
        sendGet("https://api.vk.com/method/status.set?group_id="+group+"&text="+format.replace("ONLINE",String.valueOf(this.getServer().getOnlinePlayers().size())).replace("MAX_PLAYERS",String.valueOf(this.getServer().getMaxPlayers())).replace(" ","%20")+"&access_token="+token);
    }
	
	public void updateStatus(){
		this.getServer().getScheduler().scheduleDelayedTask(new Runnable() {
			public void run() {
                upd();
				updateStatus();
			}
		},20*update);
	}
	
	private String sendGet(String url) {
		try {
		URL obj = new URL(url.toLowerCase().replace("а","%D0%B0").replace("б","%D0%B1").replace("в","%D0%B2").replace("г","%D0%B3").replace("д","%D0%B4").replace("е","%D0%B5").replace("ё","%D1%91").replace("ж","%D0%B6").replace("з","%D0%B7").replace("и","%D0%B8").replace("й","%D0%B9").replace("к","%D0%BA").replace("л","%D0%BB").replace("м","%D0%BC").replace("н","%D0%BD").replace("о","%D0%BE").replace("п","%D0%BF").replace("р","%D1%80").replace("с","%D1%81").replace("т","%D1%82").replace("у","%D1%83").replace("ф","%D1%84").replace("х","%D1%85").replace("ц","%D1%86").replace("ч","%D1%87").replace("ш","%D1%88").replace("щ","%D1%89").replace("ъ","%D1%8A").replace("ы","%D1%8B").replace("ь","%D1%8C").replace("э","%D1%8D").replace("ю","%D1%8E").replace("я","%D1%8F"));
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
		return response.toString();
		} catch(Exception e){}
		return null;
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
		update = cfg.getNested("onlinetool.update",5);
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
		cfg.setNested("onlinetool.update",update);
		cfg.setNested("onlinetool.token",token);
		cfg.setNested("onlinetool.group",group);
		cfg.setNested("onlinetool.format",format);
		cfg.setNested("onlinetool.debug",debug);
		this.saveConfig();
	}
}
