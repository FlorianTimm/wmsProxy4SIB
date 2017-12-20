package de.hamburg.gv.s2.wmsProxy;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class test
 */
@WebServlet("/geobasis")
public class geobasis extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public geobasis() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// response.setHeader("Content-Type", "text/xml");
		// TODO Auto-generated method stub
		// response.getWriter().append("Served at:
		// ").append(request.getContextPath()).append(request.getQueryString()).append(request.getQueryString()).append(request.getQueryString());

		/*
		 * String[] parameterP = request.getQueryString().split("&");
		 * Map<String, String> parameter = new HashMap<String, String>(); //
		 * response.getWriter().append("Parameter: " + parameterP.length);
		 * 
		 * for (int i = 0; i < parameterP.length; i++) { //
		 * response.getWriter().append(parameterP[i]+"\n");
		 * 
		 * String[] paar = parameterP[i].split("="); if (paar.length == 2) {
		 * parameter.put(paar[0], paar[1]); //
		 * response.getWriter().append("Parameter"); } else if (paar.length ==
		 * 1) { parameter.put(paar[0], ""); } } String urlS = ""; for
		 * (Map.Entry<String, String> eintrag : parameter.entrySet()) { String
		 * key = eintrag.getKey(); String wert = eintrag.getValue(); if
		 * (urlS.equals("")) { urlS += "?"; } else { urlS += "&"; }
		 * 
		 * urlS += key + "=" + wert;
		 * 
		 * }
		 */
		String req = "";
		if (request.getQueryString() != null) {
			req = "?" + request.getQueryString();
			req = req.replaceAll("(LAYERS=(\\d*%2C*)*\\d?)", "LAYERS=1%2C5%2C9%2C13");
		}
		String urlS = "http://geodienste.hamburg.de/HH_WMS_Geobasisdaten" + req;

		// response.getWriter().append(url.toString());
		if (urlS.lastIndexOf("GetCapabilities") == -1) {
			response.sendRedirect(urlS);
		} else {
			URL url = new URL(urlS);
			try {
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("HEAD");

				boolean zugriffMoeglich = false;
				try {
					connection.setConnectTimeout(5000);
					connection.setReadTimeout(30000);
					connection.connect();
					zugriffMoeglich = true;
				} catch (Exception e) {
					zugriffMoeglich = false;
				}

				if (zugriffMoeglich && connection.getResponseCode() == 200) {
					String contentType = connection.getContentType();
					String[] typ = contentType.split("/");
					response.setContentType(contentType);
					if (typ.length == 2 && typ[0].equals("image")) {

						// response.getWriter().append(contentType + "\n<br>" +
						// url.getQuery());
						// URL imgUrl = new URL(
						// "http://geodienste.hamburg.de/HH_WMS_Geobasisdaten?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=false&t=43&zufall=0.8169354975319985&LAYERS=1%2C5%2C9%2C13&WIDTH=512&HEIGHT=512&CRS=EPSG%3A25832&STYLES=&BBOX=566074.6000983826%2C5933629.266033529%2C567429.2660335296%2C5934983.931968676");
						// Proxy proxy = new Proxy(Proxy.Type.HTTP, new
						// InetSocketAddress("wall.lit.hamburg.de", 80));
						URLConnection con = url.openConnection();
						InputStream is = con.getInputStream();

						BufferedImage bi = ImageIO.read(is);

						OutputStream out = response.getOutputStream();
						if (typ[1].equals("jpeg")) {
							typ[1] = "jpg";
						}
						ImageIO.write(bi, typ[1], out);
						out.close();
					} else if (typ.length == 2) {
						// response.getWriter().append("WMS?");

						// Proxy proxy = new Proxy(Proxy.Type.HTTP, new
						// InetSocketAddress("wall.lit.hamburg.de", 80));
						URLConnection con = url.openConnection();
						InputStream is = con.getInputStream();

						InputStreamReader isr = new InputStreamReader(is);

						BufferedReader br = new BufferedReader(isr);

						String inputLine;
						// response.setHeader("Content-Type", "text/xml");
						// response.getWriter().append("Hallo" +
						// url.toString());
						while ((inputLine = br.readLine()) != null)
							response.getWriter()
									.append((inputLine.replaceAll("http://geodienste.hamburg.de/HH_WMS_Geobasisdaten",
											request.getRequestURL().toString())) + "\n");
						br.close();

					}
				} else if (zugriffMoeglich) {
					response.getWriter().append("Fehler " + connection.getResponseCode() + "\n<br>" + url.getPath());
				} else {
					response.getWriter().append("Fehler\n<br>Server antwortete nicht!\n<br>" + url.getPath());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
