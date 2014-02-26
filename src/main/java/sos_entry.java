import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class sos_entry {

	public sos_entry() {
		// Do nothing
	}

	public void getObservation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.getOutputStream().write("getObs".getBytes());
		response.getOutputStream().write("getOBs part 2".getBytes());
	}

	public void getCapabilities(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {

		response.getOutputStream().write("getCaps".getBytes());	
		response.getOutputStream().write(request.getQueryString().getBytes());
		response.getOutputStream().write("getCaps".getBytes());
	}
}