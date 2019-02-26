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
@WebServlet("/alkis")
public class alkis extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public alkis() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String req = "";
		if (request.getQueryString() != null) {
			req = "?" + request.getQueryString();
			req = req.replaceAll("(LAYERS=(\\d*%2C*)*\\d?)", "LAYERS=0%2C1%2C2%2C3%2C4%2C5%2C6%2C7%2C8%2C9%2C10%2C11%2C12%2C13%2C14%2C15%2C16%2C17%2C18%2C19%2C20%2C21%2C23%2C24%2C26%2C27%2C28%2C29%2C30%2C32%2C33%2C34%2C35%2C36%2C37");
		}
		
		String urlS = "http://geodienste.hamburg.de/HH_WMS_ALKIS_Basiskarte" + req;
		//response.getWriter().append(urlS);
		// response.getWriter().append(url.toString());
		if (urlS.lastIndexOf("GetCapabilities") == -1) {
			response.sendRedirect(urlS);
		} else {
			URL url = new URL(urlS);
			try {
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				//connection.setRequestMethod("HEAD");

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

						URLConnection con = url.openConnection();
						InputStream is = con.getInputStream();

						InputStreamReader isr = new InputStreamReader(is);

						BufferedReader br = new BufferedReader(isr);

						String inputLine;

						while ((inputLine = br.readLine()) != null)
							response.getWriter()
									.append((inputLine.replaceAll("http://geodienste.hamburg.de/HH_WMS_ALKIS_Basiskarte",
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
