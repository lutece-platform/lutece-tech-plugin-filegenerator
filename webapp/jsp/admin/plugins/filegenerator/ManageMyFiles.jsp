<jsp:useBean id="jspBean" scope="session" class="fr.paris.lutece.plugins.filegenerator.web.admin.TemporaryFilesJspBean" />
<% String strContent = jspBean.processController ( request , response ); %>
<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%=strContent %>

<%@ include file="../../AdminFooter.jsp" %>
