<%@page language="java" pageEncoding="utf-8" contentType="application/xml; charset=utf-8" import="java.io.*, java.util.*, java.net.*, javax.imageio.*, java.awt.image.*" %><%
String wfs_url = "https://geodienste.hamburg.de/HH_WMS_Geobasisdaten";
String wfs_url_regex = "http(s)?:\\/\\/geodienste\\.hamburg\\.de\\/HH_WMS_Geobasisdaten";
String layer = "6,10,18,26,14,2,22,30";

String req = "";
if (request.getQueryString() != null) {
	req = "?" + request.getQueryString();
	req = req.replaceAll("(LAYERS=(\\d*,*)*\\d?)", "LAYERS=" + layer);
	req = req.replaceAll("(layers=(\\d*,*)*\\d?)", "layers=" + layer);
	req = req.replaceAll("STYLES=default", "STYLES=");
	req = req.replaceAll("styles=default", "styles=");
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

				OutputStream out1 = response.getOutputStream();
				if (typ[1].equals("jpeg")) {
					typ[1] = "jpg";
				}
				ImageIO.write(bi, typ[1], out1);
				out.close();
			} else if (typ.length == 2) {

				URLConnection con = url.openConnection();
				InputStream is = con.getInputStream();

				InputStreamReader isr = new InputStreamReader(is);

				BufferedReader br = new BufferedReader(isr);

				String inputLine;
				while ((inputLine = br.readLine()) != null) {
					inputLine = inputLine.replaceAll(wfs_url_regex, request.getRequestURL().toString());

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
%>