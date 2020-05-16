package com.zhenglei.stream.servlet;

import com.zhenglei.stream.utils.IoUtil;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

@WebServlet("/fd")
public class FormDataServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(FormDataServlet.class);
	private static final long serialVersionUID = -1905516389350395696L;
	static final String FILE_FIELD = "file";
	public static final int BUFFER_LENGTH = 10485760;
	static final int MAX_FILE_SIZE = 104857600;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doOptions(req, resp);
		req.setCharacterEncoding("utf-8");
		PrintWriter writer = resp.getWriter();

		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart) {
			writer.println("ERROR: It's not Multipart form.");
			return;
		}
		JSONObject json = new JSONObject();
		long start = 0L;
		boolean success = true;
		String message = "";

		ServletFileUpload upload = new ServletFileUpload();
		InputStream in = null;
		String token = null;
		try {
			String filename = null;
			FileItemIterator iter = upload.getItemIterator(req);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				in = item.openStream();
				if (item.isFormField()) {
					String value = Streams.asString(in);
					if ("token".equals(name)) {
						token = value;
					}
					System.out.println(name + ":" + value);
				} else {
					if ((token == null) || (token.trim().length() < 1)) {
						token = req.getParameter("token");
					}
					filename = item.getName();
					if ((token == null) || (token.trim().length() < 1)) {
						token = filename;
					}
					start = IoUtil.streaming(in, token, filename);
				}
			}
			log.info("Form Saved : " + filename);
		} catch (FileUploadException fne) {
			success = false;
			message = "Error: " + fne.getLocalizedMessage();
		} finally {
			try {
				if (success) {
					json.put("start", start);
				}
				json.put("success", success);
				json.put("message", message);
			} catch (JSONException e) {
			}
			writer.write(json.toString());
			IoUtil.close(in);
			IoUtil.close(writer);
		}
	}

	public void destroy() {
		super.destroy();
	}
}
