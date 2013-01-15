<% String str=request.getParameter("str");%>
<html>
<head>
	<title></title>
</head>
<body onload="func_call('<%=str%>')">
    <form name="call" method="post">
</body>
</html>

<script>
function func_call(str)
{

	document.call.action="../../"+str+".do";
	//alert(document.call.action);
	document.call.submit();
}
</script>
