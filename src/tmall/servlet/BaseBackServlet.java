package tmall.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import tmall.dao.CategoryDAO;
import tmall.dao.OrderDAO;
import tmall.dao.OrderItemDAO;
import tmall.dao.ProductDAO;
import tmall.dao.ProductImageDAO;
import tmall.dao.PropertyDAO;
import tmall.dao.PropertyValueDAO;
import tmall.dao.ReviewDAO;
import tmall.dao.UserDAO;
import tmall.util.Page;

public abstract class BaseBackServlet extends HttpServlet {
	public abstract String add(HttpServletRequest request, HttpServletResponse response, Page page);
	public abstract String delete(HttpServletRequest request, HttpServletResponse response, Page page);
	public abstract String edit(HttpServletRequest request, HttpServletResponse response, Page page) ;
	public abstract String update(HttpServletRequest request, HttpServletResponse response, Page page) ;
	public abstract String list(HttpServletRequest request, HttpServletResponse response, Page page) ;
	
	
	protected CategoryDAO categoryDAO = new CategoryDAO();
	protected OrderDAO orderDAO = new OrderDAO();
	protected OrderItemDAO orderItemDAO = new OrderItemDAO();
	protected ProductDAO productDAO = new ProductDAO();
	protected ProductImageDAO productImageDAO = new ProductImageDAO();
	protected PropertyDAO propertyDAO = new PropertyDAO();
	protected PropertyValueDAO propertyValueDAO = new PropertyValueDAO();
	protected ReviewDAO reviewDAO = new ReviewDAO();
	protected UserDAO userDAO = new UserDAO();

	public void service(HttpServletRequest request, HttpServletResponse response) {
		try {
			
			/*鑾峰彇鍒嗛〉淇℃伅*/
			int start= 0;
			int count = 5;
			try {
				start = Integer.parseInt(request.getParameter("page.start"));
			} catch (Exception e) {
				
			}
			try {
				count = Integer.parseInt(request.getParameter("page.count"));
			} catch (Exception e) {
			}
			Page page = new Page(start,count);
			
			/*鍊熷姪鍙嶅皠锛岃皟鐢ㄥ搴旂殑鏂规硶*/
			String method = (String) request.getAttribute("method");
			Method m = this.getClass().getMethod(method, javax.servlet.http.HttpServletRequest.class,
					javax.servlet.http.HttpServletResponse.class,Page.class);
			String redirect = m.invoke(this,request, response,page).toString();
			
			/*鏍规嵁鏂规硶鐨勮繑鍥炲�硷紝杩涜鐩稿簲鐨勫鎴风璺宠浆锛屾湇鍔＄璺宠浆锛屾垨鑰呬粎浠呮槸杈撳嚭瀛楃涓�*/
			
			if(redirect.startsWith("@"))
				response.sendRedirect(redirect.substring(1));
			else if(redirect.startsWith("%"))
				response.getWriter().print(redirect.substring(1));
			else
				request.getRequestDispatcher(redirect).forward(request, response);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public InputStream parseUpload(HttpServletRequest request, Map<String, String> params){
    	InputStream is =null;
    	 try {
             DiskFileItemFactory factory = new DiskFileItemFactory();
             ServletFileUpload upload = new ServletFileUpload(factory);
             // 脡猫脰脙脡脧麓芦脦脛录镁碌脛麓贸脨隆脧脼脰脝脦陋10M
             factory.setSizeThreshold(1024 * 10240);
               
             List items = upload.parseRequest(request);
             Iterator iter = items.iterator();
             while (iter.hasNext()) {
                 FileItem item = (FileItem) iter.next();
                 if (!item.isFormField()) {
                     // item.getInputStream() 禄帽脠隆脡脧麓芦脦脛录镁碌脛脢盲脠毛脕梅
                     is = item.getInputStream();
                 } else {
                     String paramName = item.getFieldName();
                     String paramValue = item.getString();
                     paramValue = new String(paramValue.getBytes("ISO-8859-1"), "UTF-8");
                     params.put(paramName, paramValue);
                 }
             }
               
         } catch (Exception e) {
             e.printStackTrace();
         }
    	return is;
    }
}
