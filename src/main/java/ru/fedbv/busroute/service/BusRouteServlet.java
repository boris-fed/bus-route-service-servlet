package ru.fedbv.busroute.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.fedbv.busroute.model.BusRoute;
import ru.fedbv.busroute.model.DataProvider;

/** 
 * Bus route service servlet class
 */
public class BusRouteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Data provider property
     */
    private DataProvider dataProvider;

    /**
     * Process GET request to servlet. Accept two parameters: dep_sid, arr_sid.
     * Find bus route by two stop ids and return response as JSON:
     * <code>{"dep_sid": dep_sid, "arr_sid": arr_sid, "direct_bus_route": direct_bus_route}</code>
     * {@code direct_bus_route} is {@code true} if bus route contains both stops.
     * {@code direct_bus_route} is {@code false} if one/or both stops are {@code null} or bus route does not contains both stops
     *  
     * @param request
     *        http servlet request
     * 
     * @param response
     *        http servlet response
     * 
     * @throws ServletException, IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer dep_sid = getParameterValue(request, "dep_sid");
        Integer arr_sid = getParameterValue(request, "arr_sid");
        
        BusRoute busRoute = null;
        
        if (dep_sid != null && arr_sid != null) {
            busRoute = dataProvider.findByTwoStopIds(dep_sid, arr_sid);
        }
        
        String jsonResponse = "{\"dep_sid\": " + dep_sid + ", \"arr_sid\": " + arr_sid + ", \"direct_bus_route\": " + (busRoute != null) +"}";
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(jsonResponse);
    }
    
    /**
     * Stub for POST request to servlet  
     * 
     * @param request
     *        http servlet request
     * 
     * @param response
     *        http servlet response
     * 
     * @throws ServletException, IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    }

    /**
     * Gets the value of data provider property {@link BusRouteServlet.dataProvider}.
     * 
     * @return data provider property
     */
    public DataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * Sets the value of data provider property {@link BusRouteServlet.dataProvider}.
     * 
     * @param dataProvider
     *        new data provider property
     */
    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
    
    /**
     * Get the Integer value of servlet request parameter.
     * 
     * @param request
     *        http servlet request
     *        
     * @param paramName
     *        parameter name
     *        
     * @return Integer value of parameter        
     */
    private Integer getParameterValue(HttpServletRequest request, String paramName) {
        try {
            return Integer.valueOf(request.getParameter(paramName));
        } catch (NumberFormatException ignore) {
            
        }
        return null;
    }
}