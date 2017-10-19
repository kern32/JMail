<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="resources/css/bootstrap.css">
    <link href="resources/css/main.css" rel="stylesheet">
    <script src="resources/js/jquery-1.10.2.js"></script>
    <script type="text/javascript" src="resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="resources/js/main.js"></script>
    <title>Mail Message Sending</title>
</head>
<body onload="init(); getStatistic();">
<h1>Statistic</h1>
<br><br>
<div class="pull-left">
    <br/>
    <button id="start" type="button" class="btn btn-primary btn-md" onclick="start()">Start email sending</button>
    <button id="stop" type="button" class="btn btn-primary btn-md" onclick="stop()">Stop process</button>
    <label class="btn btn-default"> <acronym title="email"> Real receivers </acronym> </label>
    <input type="text" name="real" id="real">
    <label class="btn btn-default"> <acronym title="email:pass or email"> Fake Receivers </acronym> </label>
    <input type="text" name="fake" id="fake"/>
    <button id="import" type="button" class="btn btn-primary btn-md" onclick="importReceivers()">Import</button>
    <br/><br/>
    <div id="resetDate"></div>
    <br/>
    <table class="table table-striped table-bordered table-condensed" id="senderListTable"/>
    <table class="table table-striped table-bordered table-condensed" id="realReceiverListTable"/>
    <table class="table table-striped table-bordered table-condensed" id="fakeReceiverListTable"/>
    <br/>
</div>
</body>
</html>
