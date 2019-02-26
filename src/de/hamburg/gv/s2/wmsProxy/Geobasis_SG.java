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
@WebServlet("/geobasis_sg")
public class Geobasis_SG extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Geobasis_SG() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String wfs_url = "http://geodienste.hamburg.de/HH_WMS_Geobasisdaten_SG";
		String layer = "5%2C29%2C25%2C9%2C21%2C17%2C13%2C1";

		String req = "";
		if (request.getQueryString() != null) {
			req = "?" + request.getQueryString();
			req = req.replaceAll("(LAYERS=(\\d*%2C*)*\\d?)", "LAYERS=" + layer);
		}

		String urlS = wfs_url + req;
		// response.getWriter().append(urlS);
		// response.getWriter().append(url.toString());
		if (urlS.lastIndexOf("GetCapabilities") == -1) {
			response.sendRedirect(urlS);
		} else {
			URL url = new URL(urlS);
			try {
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				// connection.setRequestMethod("HEAD");

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
						while ((inputLine = br.readLine()) != null) {
							inputLine = inputLine.replaceAll(wfs_url, request.getRequestURL().toString());

							String minScale1 = "(?<=<MinScaleDenominator xmlns=\"http:\\/\\/www.opengis.net\\/wms\">)(\\d+\\.\\d*)(?=<\\/MinScaleDenominator>)";
							String minScale2 = "(?<=<MinScaleDenominator>)(\\d+\\.\\d*)(?=<\\/MinScaleDenominator>)";
							String maxScale1 = "(?<=<MaxScaleDenominator xmlns=\"http:\\/\\/www.opengis.net\\/wms\">)(\\d+\\.\\d*)(?=<\\/MaxScaleDenominator>)";
							String maxScale2 = "(?<=<MaxScaleDenominator>)(\\d+\\.\\d*)(?=<\\/MaxScaleDenominator>)";
							inputLine = inputLine.replaceAll(minScale1, "9.449405");
							inputLine = inputLine.replaceAll(minScale2, "9.449405");
							inputLine = inputLine.replaceAll(maxScale1, "28349.159226");
							inputLine = inputLine.replaceAll(maxScale2, "28349.159226");

							response.getWriter().append(inputLine + "\n");
						}
						br.close();

					}
				} else if (zugriffMoeglich) {
					response.getWriter().append("Fehler " + connection.getResponseCode() + ' '
							+ connection.getResponseMessage() + "\n" + url.toString());
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
