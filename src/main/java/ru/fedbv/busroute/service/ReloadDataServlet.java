package ru.fedbv.busroute.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.fedbv.busroute.model.DataProvider;
import ru.fedbv.busroute.model.LoadResult;

/** 
 * Bus route reload data servlet class
 */
public class ReloadDataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Data provider property
     */
    private DataProvider dataProvider;

    /**
     * Process GET request to servlet. Reload data store.
     * Reload data store. Return {@LoadResult} as JSON:
     * <code>{"load_count": load_count, "error_count": error_count}</code>
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
        
        LoadResult loadResult = dataProvider.init();
        String jsonResponse = "{\"load_count\": " + loadResult.getLoad_count() +", \"error_count\": " + loadResult.getError_count() + "}";
        
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
     * Gets the value of data provider property {@link ReloadDataServlet.dataProvider}.
     * 
     * @return data provider property
     */
    public DataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * Sets the value of data provider property {@link ReloadDataServlet.dataProvider}.
     * 
     * @param dataProvider
     *        new data provider property
     */
    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
}