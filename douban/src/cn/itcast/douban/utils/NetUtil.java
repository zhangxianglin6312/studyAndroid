package cn.itcast.douban.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import cn.itcast.douban.domain.NewBook;

import com.google.gdata.client.douban.DoubanService;

public class NetUtil {

	/**
	 * 登陆豆瓣服务器 换取后门的钥匙
	 * 
	 * @param email 用户名
	 * @param password 密码
	 * @param captcha_solution 验证码对应的英文字母
	 * @param captcha_id 验证码id
	 * @return
	 */
	public static List<String> login(String email, String password,String captcha_solution,String captcha_id) {

		// 4.如果授权豆瓣就会开启一个后门( key secret).
		ArrayList<String> lists;
		try {
			// 1.网站b (客户端b)需要在豆瓣上申请一个key 和 secret
			String apiKey = "0f84a27e6da546b12e8de77b48efd413";
			String secret = "15ac704ffbe18f2a";

			DoubanService myService = new DoubanService("黑马9期小豆瓣", apiKey,
					secret);
			// 2.拿着申请的key 换取一个临时的钥匙.
			String path = myService.getAuthorizationUrl(null);
			System.out.println(path);

			// 3.代码模拟登陆的流程
			// 登陆到豆瓣的服务器上.
			// source=simple&redir=http%3A%2F%2Fwww.douban.com&
			// form_email=itcastweibo@sina.cn&form_password=a11111&user_login=%E7%99%BB%E5%BD%95
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://www.douban.com/accounts/login");
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("source", "simple"));
			parameters.add(new BasicNameValuePair("redir",
					"http://www.douban.com"));
			parameters.add(new BasicNameValuePair("form_email", email));
			parameters.add(new BasicNameValuePair("form_password", password));
			if((!TextUtils.isEmpty(captcha_solution)) && (!TextUtils.isEmpty(captcha_id))){
				parameters.add(new BasicNameValuePair("captcha-solution", captcha_solution));
				parameters.add(new BasicNameValuePair("captcha-id", captcha_id));
			
			}
			parameters.add(new BasicNameValuePair("user_login", "登录"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
			post.setEntity(entity);

			HttpResponse response = client.execute(post);
			System.out.println(response.getStatusLine().getStatusCode());
			// 成功登陆到豆瓣的服务器上了.
			// 模拟用户点击登陆的操作
			CookieStore cookie = client.getCookieStore();// 获取成功登陆的cookies

			DefaultHttpClient submitclient = new DefaultHttpClient();
			submitclient.setCookieStore(cookie);
			HttpPost submitpost = new HttpPost(path);
			List<NameValuePair> submitParams = new ArrayList<NameValuePair>();
			submitParams.add(new BasicNameValuePair("ck", "nhJv"));
			String oauth_token = path.substring(path.lastIndexOf("=") + 1,
					path.length());
			submitParams
					.add(new BasicNameValuePair("oauth_token", oauth_token));
			submitParams.add(new BasicNameValuePair("oauth_callback", ""));
			submitParams.add(new BasicNameValuePair("ssid", "4dbf5992"));
			submitParams.add(new BasicNameValuePair("confirm", "同意"));
			submitpost.setEntity(new UrlEncodedFormEntity(submitParams));
			HttpResponse submitResp = submitclient.execute(submitpost);

			System.out.println(response.getStatusLine().getStatusCode());

			lists = myService.getAccessToken();
			System.out.println("oauth token:" + lists.get(0));
			System.out.println("token secret:" + lists.get(1));
			// myService.setAccessToken("8df5620adc95a7a4a9e0262095c0f6ec",
			// "cc25e77a56ea6549");
			return lists;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 检查是否需要验证码 如果需要 则返回验证码的id
	 * 
	 * @return
	 */
	public static String getCaptchaId() {
		try {
			String path = "http://www.douban.com/accounts/login";
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream is = conn.getInputStream();
			// 解析这个is里面的内容 判断是否有input的标签.
			Source source = new Source(is);
			List<Element> inputs = source.getAllElements("input");
			for (Element input : inputs) {
				System.out.println(input.getAttributeValue("name"));
				if ("captcha-id".equals(input.getAttributeValue("name"))) {
					String id = input.getAttributeValue("value");
					/*
					 * String captchapath =
					 * "http://www.douban.com/misc/captcha?id="
					 * +id+"&amp;size=s"; System.out.println(captchapath);
					 */
					return id;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}
/**
 * 获取验证码对应的图片
 * @param path 验证码的路径
 * @return
 */
	public static Bitmap getBitmap(String path){
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream is = conn.getInputStream();
			return BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取所有的新书信息
	 * @return
	 * @throws Exception
	 */
	public static List<NewBook> getNewBooks() throws Exception{
		URL url = new URL("http://book.douban.com/latest");
		List<NewBook> newbooks = new ArrayList<NewBook>();
		URLConnection conn = url.openConnection();
		Source source = new Source(conn);
		List<Element> lis = source.getAllElements("li");
		for (Element li : lis) {
			List<Element> childrenElements = li.getChildElements();
			if (childrenElements.size() == 2
					&& "div".equals(childrenElements.get(0).getName())
					&& "a".equals(childrenElements.get(1).getName())) {
				NewBook newbook = new NewBook();
				Element divelement = childrenElements.get(0);
				String title = divelement.getChildElements().get(0)
						.getTextExtractor().toString();
				String description = divelement.getChildElements().get(1)
						.getTextExtractor().toString();
				String summary = divelement.getChildElements().get(2)
						.getTextExtractor().toString();

				newbook.setTitle(title);
				newbook.setDescription(description);
				newbook.setSummary(summary);
				
				Element aelement = childrenElements.get(1);
				String iconpath = aelement.getChildElements().get(0).getAttributeValue("src");
				newbook.setIconpath(iconpath);
				newbooks.add(newbook);
				newbook = null;
			}
		}
		
		
		return newbooks;
	}
}
