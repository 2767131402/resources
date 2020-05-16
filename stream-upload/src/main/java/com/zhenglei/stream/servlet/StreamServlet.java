package com.zhenglei.stream.servlet;

import com.zhenglei.stream.utils.Configurations;
import com.zhenglei.stream.utils.IoUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;

@WebServlet("/upload")
public class StreamServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(StreamServlet.class);
	private static final long serialVersionUID = -8619685235661387895L;
	static final int BUFFER_LENGTH = 10240;
	static final String START_FIELD = "start";
	public static final String CONTENT_RANGE_HEADER = "content-range";

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doOptions(req, resp);

		String token = req.getParameter("token");
		String size = req.getParameter("size");
		String fileName = req.getParameter("name");
		PrintWriter writer = resp.getWriter();

		JSONObject json = new JSONObject();
		long start = 0L;
		boolean success = true;
		String message = "";
		try {
			File f = IoUtil.getTokenedFile(token);
			start = f.length();
			if ((token.endsWith("_0")) && ("0".equals(size)) && (0L == start)) {
				f.renameTo(IoUtil.getFile(fileName));
			}
		} catch (FileNotFoundException fne) {
			message = "Error: " + fne.getMessage();
			success = false;
		} finally {
			try {
				if (success) {
					json.put("start", start);
				}
				json.put("success", success);
				json.put("message", message);
			} catch (JSONException localJSONException2) {
			}
			writer.write(json.toString());
			IoUtil.close(writer);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doOptions(req, resp);

		String token = req.getParameter("token");
		String fileName = req.getParameter("name");
		Range range = IoUtil.parseRange(req);

		OutputStream out = null;
		InputStream content = null;
		PrintWriter writer = resp.getWriter();

		JSONObject json = new JSONObject();
		long start = 0L;
		boolean success = true;
		String message = "";
		File f = IoUtil.getTokenedFile(token);
		try {
			if (f.length() != range.getFrom()) {
				throw new StreamException(StreamException.ERROR_FILE_RANGE_START);
			}
			out = new FileOutputStream(f, true);
			content = req.getInputStream();
			int read = 0;
			byte[] bytes = new byte[10240];
			while ((read = content.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			start = f.length();
		} catch (StreamException se) {
			success = StreamException.ERROR_FILE_RANGE_START == se.getCode();
			message = "Code: " + se.getCode();
		} catch (FileNotFoundException fne) {
			message = "Code: " + StreamException.ERROR_FILE_NOT_EXIST;
			success = false;
		} catch (IOException io) {
			message = "IO Error: " + io.getMessage();
			success = false;
		} finally {
			IoUtil.close(out);
			IoUtil.close(content);
			if (range.getSize() == start) {
				try {
					IoUtil.getFile(fileName).delete();

					Files.move(f.toPath(), f.toPath().resolveSibling(fileName),	new CopyOption[0]);
					log.info("TK: `" + token + "`, NE: `" + fileName + "`");
					if (Configurations.isDeleteFinished()) {
						IoUtil.getFile(fileName).delete();
					}
				} catch (IOException e) {
					success = false;
					message = "Rename file error: " + e.getMessage();
				}
			}
			try {
				if (success) {
					json.put("start", start);
				}
				json.put("success", success);
				json.put("message", message);
			} catch (JSONException localJSONException4) {
			}
			writer.write(json.toString());
			IoUtil.close(writer);
		}
	}

	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/json;charset=utf-8");
		resp.setHeader("Access-Control-Allow-Headers", "Content-Range,Content-Type");
		resp.setHeader("Access-Control-Allow-Origin", Configurations.getCrossOrigins());
		resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
	}

	public void destroy() {
		super.destroy();
	}
}
