<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!-- Bootstrap -->
        <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">

        <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
          <script src="../../assets/js/html5shiv.js"></script>
          <script src="../../assets/js/respond.min.js"></script>
        <![endif]-->
    </head>
    <body>
        <div class="container">
            <h1 id="title">Drools-MAS</h1>
            <div class="panel panel-default">
                <div class="panel-heading"><h3>Configuration</h3></div>
                <div class="panel-body">
                    <table id="configTable" class="table table-condensed table-hover">
                        <thead>
                            <tr>
                                <th>Variable</th>
                                <th>Value</th>
                            </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="panel panel-default">
                <div class="panel-heading">
                    <div class="row">
                        <div class="col-md-1">
                            <h3>Sessions</h3>
                        </div>
                        <div class="col-md-1 col-md-offset-10" style="margin-top: 22px">
                            <a href='javascript:refreshSessionsTable()' title="refresh"><span class="glyphicon glyphicon-refresh"></span></a>
                        </div>
                    </div>
                </div>
                <div class="panel-body">
                    <table id="sessionsTable" class="table table-condensed table-hover">
                        <thead>
                            <tr>
                                <th>Session Id</th>
                                <th># Facts</th>
                            </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="panel panel-default">
                <div class="panel-heading">
                    <div class="row">
                        <div class="col-md-1">
                            <h3>Logs</h3>
                        </div>
                        <div class="col-md-4" style="margin-top: 22px">
                            <select id="logsTablePageSize">
                                <option value="20">20 records</option>
                                <option value="40">40 records</option>
                                <option value="60">60 records</option>
                                <option value="80">80 records</option>
                                <option value="100">100 records</option>
                                <option value="150">150 records</option>
                                <option value="200">200 records</option>
                            </select>
                        </div>
                        <div class="col-md-1 col-md-offset-6" style="margin-top: 22px">
                            <a href='MgmtConsoleServlet?action=writeLogLines' target="_blank" title="download full logs"><span class="glyphicon glyphicon-download-alt"></span></a>
                        </div>
                    </div>
                </div>
                <div class="panel-body">
                    <table id="logsTable" class="table table-condensed table-hover">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Message</th>
                            </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                    <ul class="pager">
                        <li id="logsOlderButton" class="next"><a href="javascript:goToNextLogsPage()">Older &rarr;</a></li>
                        <li id="logsNewerButton" class="previous disabled"><a href="javascript:goToPreviousLogsPage()">&larr; Newer</a></li>
                    </ul>
                </div>
            </div>
        </div>

        <!--session facts details modal window --> 
        <div class="modal fade" id="sessionFactsDetailsModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 id="sessionFactsDetailsModalTitle" class="modal-title"></h4>
                    </div>
                    <div class="modal-body">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-md-1">
                                        <h3>Facts</h3>
                                    </div>
                                    <div class="col-md-1 col-md-offset-10" style="margin-top: 22px">
                                        <a href='javascript:refreshSessionFactsDetailsModalTable()' title="refresh"><span class="glyphicon glyphicon-refresh"></span></a>
                                    </div>
                                </div>
                            </div>
                            <div class="panel-body">
                                <table id="sessionFactsDetailsModalTable" class="table table-condensed table-hover">
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>Class</th>
                                            <th># Instances</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
        <script src="//code.jquery.com/jquery.js"></script>
        <!-- Include all compiled plugins (below), or include individual files as needed -->
        <script src="js/bootstrap.min.js"></script>

        <script>//<![CDATA[

            var currentLogIndex = 0;

            (function() {

                //bind logsTablePageSize 'change' event
                $('#logsTablePageSize').change(function() {
                    goToLogsFirstPage();
                });

                //get agent configuration data
                $.ajax({
                    type: "GET",
                    url: "MgmtConsoleServlet?action=listAgentConfigProperties"
                }).done(function(data) {
                    document.title = data.agent_name;
                    $("#title").html(data.agent_name);

                    //fill log table
                    goToLogsFirstPage();

                    //fill config table
                    for (var key in data) {
                        if (data.hasOwnProperty(key)) {
                            var content = data[key];

                            //if the value is an url, then create a link 
                            var isUrl = content.match("^http:");
                            var url = content;
                            if (isUrl) {
                                content = "<a href='" + url + "'>" + url + "</a>"
                            }

                            $('#configTable > tbody:last').append('<tr><td>' + key + '</td><td id="configTd_' + key + '">' + content + '</td></tr>');

                            //check url availability
                            if (isUrl) {
                                $.ajax({
                                    type: "GET",
                                    url: "MgmtConsoleServlet?action=testUrl&url=" + url
                                }).done(function(d) {
                                    if (d.responseCode == "200") {
                                        $('#configTd_' + key).append("&nbsp;<span class='glyphicon glyphicon-ok-circle'></span>");
                                    } else {
                                        $('#configTd_' + key).addClass('danger')
                                        $('#configTd_' + key).append("&nbsp;<span class='glyphicon glyphicon-remove-circle' title='URL status code: " + d.responseCode + "'></span>");
                                    }
                                });
                            }
                        }
                    }

                });

                //get sessions' data
                refreshSessionsTable();
            })();

            function goToLogsFirstPage() {
                currentLogIndex = 0;
                refreshLogs();
            }

            function goToNextLogsPage() {
                currentLogIndex += parseInt($('#logsTablePageSize').val());
                refreshLogs();
            }

            function goToPreviousLogsPage() {
                currentLogIndex -= parseInt($('#logsTablePageSize').val());
                currentLogIndex = currentLogIndex < 0 ? 0 : currentLogIndex;
                refreshLogs();
            }

            function refreshLogs() {
                $.ajax({
                    type: "GET",
                    url: "MgmtConsoleServlet?action=getLogMessages&startIndex=" + currentLogIndex + "&numberOfRecords=" + $('#logsTablePageSize').val()
                }).done(function(d) {
                    //enable both 'newer' and 'older' buttons
                    $('#logsNewerButton').removeClass('disabled');
                    $('#logsOlderButton').removeClass('disabled');

                    if ($(d.messages).size() <= 0) {
                        //last page reached
                        //disable 'older' button
                        $('#logsOlderButton').addClass('disabled');

                        //reset currentLogIndex
                        currentLogIndex = currentLogIndex - parseInt($('#logsTablePageSize').val());
                    } else {
                        //clear old results
                        $('#logsTable > tbody:last').empty();

                        //add new results
                        $(d.messages).each(function(i, v) {
                            $('#logsTable > tbody:last').append('<tr><td>' + (currentLogIndex + 1 + i) + '</td><td>' + v.message + '</td></tr>');
                        });
                    }

                    //disable 'newer' button if we are in the first page
                    if (currentLogIndex == 0) {
                        $('#logsNewerButton').addClass('disabled');
                    }
                });
            }

            function refreshSessionsTable() {
                $.ajax({
                    type: "GET",
                    url: "MgmtConsoleServlet?action=getSessionsStatus"
                }).done(function(data) {
                    //clear old results
                    $('#sessionsTable > tbody:last').empty();

                    //sort results
                    data.sessions.sort(function(a, b) {
                        //mind session goes first
                        if (a.mind) {
                            return -1;
                        }
                        if (b.mind) {
                            return 1;
                        }
                        return a.sessionId < b.sessionId;
                    });

                    //add new results
                    $(data.sessions).each(function(i, s) {
                        $('#sessionsTable > tbody:last').append('<tr><td>' + s.sessionId + (s.mind ? " <span class='glyphicon glyphicon-star-empty' title='agent&#39;s mind session'></span>" : "") + '</td><td>' + s.objectCount + " <a href='javascript:showSessionFactsDetails(\"" + s.sessionId + "\");'><span class='glyphicon glyphicon-list' title='show details'></span></a></td></tr>");
                    });
                });
            }

            function showSessionFactsDetails(sessionId) {
                //set modal title
                $('#sessionFactsDetailsModalTitle').html(sessionId);

                //show the modal
                $('#sessionFactsDetailsModal').modal();

                //refresh table data
                refreshSessionFactsDetailsModalTable(sessionId);
            }

            function refreshSessionFactsDetailsModalTable(sessionId) {
                //id no sessionId is provided, use the title of the modal
                if (!sessionId) {
                    sessionId = $('#sessionFactsDetailsModalTitle').html();
                }

                //clear old results
                $('#sessionFactsDetailsModalTable > tbody:last').empty();

                //retrieve data
                $.ajax({
                    type: "GET",
                    url: "MgmtConsoleServlet?action=getSessionFactsDetails&sessionId=" + sessionId
                }).done(function(data) {
                    //order data
                    data.facts.sort(function(a, b) {
                        return a.count < b.count;
                    });

                    //show data on modal's table
                    $(data.facts).each(function(i, f) {
                        $('#sessionFactsDetailsModalTable > tbody:last').append('<tr><td>' + (i + 1) + '</td><td>' + f.class + '</td><td>' + f.count + '</td></tr>');
                    });
                });
            }

            //]]>
        </script>
    </body>
