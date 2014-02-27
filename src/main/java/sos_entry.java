import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.asascience.ncsos.outputformatter.OutputFormatter;
import com.asascience.ncsos.service.Parser;
import com.asascience.sos.dataproducts.PostgresDataReader;

public class sos_entry {

	private static org.slf4j.Logger _log = org.slf4j.LoggerFactory.getLogger(sos_entry.class);
	private static org.slf4j.Logger _logServerStartup = org.slf4j.LoggerFactory.getLogger("serverStartup");

	private HashMap<String, Object> respMap;

	public sos_entry() {
		// Do nothing
	}

	public void getObservation(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	public void getCapabilities(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.getOutputStream().write("getCaps".getBytes());
		response.getOutputStream().write(request.getQueryString().getBytes());
		response.getOutputStream().write("getCaps".getBytes());
	}

	public void processRequest(HttpServletRequest req, HttpServletResponse res) {
		respMap = new HashMap<String, Object>();

		try {
			// see http://tomcat.apache.org/tomcat-5.5-doc/config/context.html
			// ----- workdir
			String tempdir = System.getProperty("java.io.tmpdir");

			Parser md = new Parser();
			PostgresDataReader dataset = new PostgresDataReader();
			respMap = md.enhanceGETRequest(dataset, req.getQueryString(),req.getRequestURL() + "?".toString(), tempdir);

			Writer writer = res.getWriter();
			OutputFormatter output = (OutputFormatter) respMap.get("outputFormatter");
			res.setContentType(output.getContentType().toString());
			output.writeOutput(writer);
			writer.flush();
			writer.close();

		} catch (Exception e) {
			_log.error("Something went wrong", e);
			// close the dataset remove memory hang
		} finally {

		}

	}

}