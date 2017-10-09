<%@page import="java.net.InetAddress"%>
sirh.jobs.version=${version}<br/>
sirh.jobs.hostaddress=<%=InetAddress.getLocalHost().getHostAddress() %><br/>
sirh.jobs.canonicalhostname=<%=InetAddress.getLocalHost().getCanonicalHostName() %><br/>
sirh.jobs.hostname=<%=InetAddress.getLocalHost().getHostName() %><br/>
sirh.jobs.tomcat.version=<%= application.getServerInfo() %><br/>
sirh.jobs.tomcat.catalina_base=<%= System.getProperty("catalina.base") %><br/>
<% 
HttpSession theSession = request.getSession( false );

// print out the session id
if( theSession != null ) {
  //pw.println( "<BR>Session Id: " + theSession.getId() );
  synchronized( theSession ) {
    // invalidating a session destroys it
    theSession.invalidate();
    //pw.println( "<BR>Session destroyed" );
  }
}
%>
