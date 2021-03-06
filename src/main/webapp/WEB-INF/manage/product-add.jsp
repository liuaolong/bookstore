<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<jsp:include page="../static/back-head.jsp"/>

</head>
<body onload="getNowFormatDate()">
<div id="header" class="wrap">
	<div id="logo"><img src="images/logo.png" /></div>
	<div class="help"><a href="index.do">返回前台页面</a></div>
	<div class="navbar">
		<ul class="bar">
			<li ><a href="mana.do">首页</a></li>
			<li><a href="manaUser.do">用户</a></li>
			<li class="current"><a href="manaBook.do">商品</a></li>
			<li><a href="BookOrder.do">订单</a></li>

		</ul>
	</div>
</div>
<div id="childNav">
	<div class="welcome wrap">
		管理员您好，今天是<span id="time"></span>，欢迎回到管理后台。

	</div>
</div>
<div id="main" class="wrap">
	<div id="menu-mng" class="lefter">
		<div class="box">
			<dl>
				<dt>用户管理</dt>
				<dd><em><a href="addUserPage.do">新增</a></em><a href="manaUser.do">用户管理</a></dd>
				<dt>商品管理</dt>
				<dd><em><a href="addproductPage.do">新增</a></em><a href="manaBook.do">商品管理</a></dd>
<%--				<dd><em><a href="addBookTypePage.do">新增</a></em><a href="manaBookType.do">分类管理</a></dd>--%>
				<dt>订单管理</dt>
				<dd><a href="BookOrder.do">订单管理</a></dd>
			</dl>
		</div>
	</div>
	<div class="main">
		<h2>添加商品</h2>
		<div class="manage">
			<form action="addBook.do" method="post" enctype="multipart/form-data">
				<table class="form">
					<tr>
						<td class="field">商品名称：</td>
						<td><input type="text" class="text" name="bname" value="" /></td>
					</tr>
					<tr>
						<td class="field">商品描述：</td>
						<td><input type="text" class="text" name="detail" value="" /></td>
					</tr>
					<tr>
						<td class="field">商品价格：</td>
						<td><input type="text" class="text tiny" name="pirce" /> 元</td>
					</tr>
					<tr>
						<td class="field">所属分类：</td>
						<td>
							<select name="type" class="form-control" style="width: 90px;">
								<c:forEach items="${ bts}" var="bt">
								<option value="${bt }">${bt}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td class="field">作者：</td>
						<td><input type="text" class="text" name="writer" /></td>
					</tr>
					<tr>
						<td class="field">出版社：</td>
						<td><input type="text" class="text" name="printer" /></td>
					</tr>
					<tr>
						<td class="field">出版日期：</td>
						<td>
							<input class="Wdate" type="text" name="dateString" onClick="WdatePicker()">
						</td>
					</tr>
					<tr>
						<td class="field">商品图片：</td>
						<td><input type="file" class="text" name="image" id="image"/></td>
					</tr>
					<tr>
						<td class="field">库存：</td>
						<td><input type="text" class="text tiny" name="store" /></td>
					</tr>
					<tr>
						<td></td>
						<td><input class="btn btn-info" type="submit" name="submit" value="添加" /></td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	<div class="clear"></div>
</div>
<jsp:include page="../static/footer.jsp"/>
</body>
</html>
