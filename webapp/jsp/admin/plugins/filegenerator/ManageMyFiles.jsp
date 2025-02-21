<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

${ pageContext.setAttribute( 'strContent', temporaryFilesJspBean.processController( pageContext.request , pageContext.response ) ) }

${ pageContext.getAttribute( 'strContent' ) }

<%@ include file="../../AdminFooter.jsp" %>
