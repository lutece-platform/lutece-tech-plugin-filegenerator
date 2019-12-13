<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="jspBean" scope="session" class="fr.paris.lutece.plugins.filegenerator.web.admin.TemporaryFilesJspBean" />
<%
	jspBean.init( request, fr.paris.lutece.plugins.filegenerator.web.admin.TemporaryFilesJspBean.VIEW_TEMP_FILES); 
	jspBean.doDownloadFile( request, response );
%>