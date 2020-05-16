package com.zhenglei.stream.servlet;

import com.zhenglei.stream.utils.Configurations;
import com.zhenglei.stream.utils.TokenUtil;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/tk")
public class TokenServlet extends HttpServlet {
	private static final long serialVersionUID = 2650340991003623753L;
	static final String FILE_NAME_FIELD = "name";
	static final String FILE_SIZE_FIELD = "size";
	static final String TOKEN_FIELD = "token";
	static final String SERVER_FIELD = "server";
	static final String SUCCESS = "success";
	static final String MESSAGE = "message";

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String name = req.getParameter("name");
		String size = req.getParameter("size");
		String token = TokenUtil.generateToken(name, size);

		PrintWriter writer = resp.getWriter();

		JSONObject json = new JSONObject();
		try {
			json.put("token", token);
			if (Configurations.isCrossed()) {
				json.put("server", Configurations.getCrossServer());
			}
			json.put("success", true);
			json.put("message", "");
		} catch (JSONException localJSONException) {
		}
		writer.write(json.toString());
	}

	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doHead(req, resp);
	}

	public void destroy() {
		super.destroy();
	}
}
