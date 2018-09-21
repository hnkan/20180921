
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class execute {
	final static String imgDomain = "https://stat.ameba.jp";
	final static String listPageHead = "https://ameblo.jp/angerme-ss-shin/imagelist-";
	static String imgOriginUrl;

	public static void main(String[] args) throws Exception {
		for (int i = 2018; i < 2019; i++) {
			for (int j = 6; j < 7; j++) {
				String listPage = "";
				String ym = "";
				String year = String.valueOf(i);
				String month = "";
				if (j < 10) {
					month = "0"+String.valueOf(j);
				} else {
					month = String.valueOf(j);
				}
				ym = year + month;
				listPage = listPageHead + ym + ".html";
				imgParsing(listPage);
				for (int k = 2; k < 7; k++) {
					boolean result = getImgAfter2page(ym, String.valueOf(k));
					if (!result) {
						System.out.println("read end");
						break;
					}
				}
			}
		}
		System.out.println("다운끝");

	}

	public static void imgParsing(String url) throws Exception {

		Document doc = Jsoup.connect(url).method(Connection.Method.GET).execute().parse();
		Elements aTags = doc.select("a[class=imgLink]");
		List<String> imgPageUrls = new ArrayList<>();
		for (Element atag : aTags) {
			imgPageUrls.add(atag.attr("abs:href"));
		}
		for (String imgPageUrl : imgPageUrls) {
			getImgUrl(imgPageUrl);
		}
	}

	public static void getImgUrl(String url) throws Exception {
		String resultUrl = "";
		Document doc = Jsoup.connect(url).method(Connection.Method.GET).execute().parse();
		Elements scripts = doc.getElementsByTag("script");
		scripts.forEach(script -> {
			script.dataNodes().forEach(node -> {
				String scriptTex = node.getWholeData();
				BufferedReader br = new BufferedReader(new StringReader(scriptTex));
				String line;
				try {
					while ((line = br.readLine()) != null) {
						if (line.contains("imgUrl")) {
							String[] lines = line.split(":");
							for (String s : lines) {
								if (s.contains("pageUrl") && s.contains("jpg")) {
									s = s.replace("pageUrl", "");
									s = s.replace("\"", "");
									s = s.replace(",", "");
									imgOriginUrl = imgDomain + s;
									getImg();
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});

	}

	public static boolean getImgAfter2page(String ym, String page) throws Exception {
		boolean result = false;
		String url = "https://blogimgapi.ameba.jp/image_list/get.jsonp?ameba_id=smileage-submember&target_ym=" + ym
				+ "&limit=18&page=" + page + "&sp=false";
		BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains("imgList\":null")) {
				return false;
			}
			if (line.contains("imgUrl")) {
				String[] lines = line.split(":");
				for (String s : lines) {
					if (s.contains("pageUrl") && s.contains("jpg")) {
						s = s.replace("pageUrl", "");
						s = s.replace("\"", "");
						s = s.replace(",", "");
						imgOriginUrl = imgDomain + s;
						getImg();
					}
				}
			}
		}
		return true;
	}

	public static void getImg() throws Exception {
		String[] imgNames = imgOriginUrl.split("/");
		String imgName = imgNames[4] + "_" + imgNames[10];
		File dir = new File("C:\\Users\\Hanee\\Pictures\\blog\\" + imgNames[4].substring(0, 6));
		if (!dir.exists()) {
			dir.mkdirs();
		}
		URL url = new URL(imgOriginUrl);
		BufferedImage img = ImageIO.read(url);
		File file = new File("C:\\Users\\Hanee\\Pictures\\blog\\" + imgNames[4].substring(0, 6) + "\\" + imgName);
		ImageIO.write(img, "jpg", file);
		System.out.println("사진 다운");
	}
}
