<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.filegenerator.web.admin.TemporaryFilesJspBean"%>

${ temporaryFilesJspBean.init( pageContext.request, TemporaryFilesJspBean.VIEW_TEMP_FILES ) }
${ temporaryFilesJspBean.doDeleteFile( pageContext.request ) }