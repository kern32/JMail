/**
 * Created by kernel32 on 13.10.2017.
 */
window.setInterval(function () {
    getStatistic()
}, 5000);

$(document).ready(function () {
    $sender_table_header = "<thead>" +
        "<tr bgcolor=\"#428bca\" style=\"color:#ffffff\">" +
        "<th>Sender</th>" +
        "<th>Status</th>" +
        "<th>Processed date</th>" +
        "<th>Sent letters</th>" +
        "<th>Maximum letters</th>" +
        "</tr></thead>";
    $real_receiver_table_header = "<thead>" +
        "<tr bgcolor=\"#428bca\" style=\"color:#ffffff\">" +
        "<th>Receiver type</th>" +
        "<th>Success sent</th>" +
        "<th>Failed sent</th>" +
        "<th>Not existing emails</th>" +
        "<th>Planned</th>" +
        "<th>Not planned</th>" +
        "<th>Total processed</th>" +
        "<th>Total not processed</th>" +
        "</tr></thead>";
    $fake_receiver_table_header = "<thead>" +
        "<tr bgcolor=\"#428bca\" style=\"color:#ffffff\">" +
        "<th>Receiver type</th>" +
        "<th>Success sent</th>" +
        "<th>Failed sent</th>" +
        "</tr></thead>";
});

function init() {
    document.getElementById("stop").disabled = true;
}

function start() {
    document.getElementById("stop").disabled = false;
    document.getElementById("start").disabled = true;
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open("POST", "./start", false); // false for synchronous request
    xmlHttp.send();
    return xmlHttp.responseText;
}

function stop() {
    document.getElementById("stop").disabled = true;
    document.getElementById("start").disabled = false;
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open("POST", "./stop", false); // false for synchronous request
    xmlHttp.send();
    return xmlHttp.responseText;
}

function getStatistic() {
    data = {"": ""};
    console.log("calling get statistic ajax");
    console.log("data: ", data);
    console.log("link: ", "./get");
    $.ajax({
        type: "GET",
        enctype : "multipart/form-data",
        url: "./get",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {
            console.log("success: ", data);
            if (data['senderList'] != null) {
                drawSenderTable(data['senderList']);
            }
            if (data['realReceiverList'] != null) {
                drawRealReceiverTable(data['realReceiverList']);
            }
            if (data['fakeReceiverList'] != null) {
                drawFakeReceiverTable(data['fakeReceiverList']);
            }
            var flag = data['sendingFlag']
            console.log("sendingFlag: ", flag);
            if(flag == true){
                document.getElementById("stop").disabled = false;
                document.getElementById("start").disabled = true;
            } else {
                document.getElementById("stop").disabled = true;
                document.getElementById("start").disabled = false;
            }
            document.getElementById("resetDate").innerHTML = "Next sender reset date: " + data['resetDate']
        },
        error: function (e) {
            console.log("ERROR: ", e);
        },
    });
    return false;
}

function drawSenderTable(data) {
    $("#senderListTable").html($sender_table_header);
    console.log("Building table size: ", data.length);
    for (var i = 0; i < data.length; i++) {
        drawSenderRow(data[i], i);
        console.log("Building row: ", data[i]);
    }
    console.log("End of Building table: ", data);
}

function drawSenderRow(rowData, index) {
    var row = $("<tr id=" + index + "/>");
    $("#senderListTable").append(row);
    console.log("Row index: ", index);
    $.each(rowData, function (index, value) {
        row.append($("<td>" + value + "</td>"));
    });
}

function drawRealReceiverTable(data) {
    $("#realReceiverListTable").html($real_receiver_table_header);
    console.log("Building table size: ", data.length);
    for (var i = 0; i < data.length; i++) {
        drawRealReceiverRow(data[i], i);
        console.log("Building row: ", data[i]);
    }
    console.log("End of Building table: ", data);
}

function drawRealReceiverRow(rowData, index) {
    var row = $("<tr id=" + index + "/>");
    $("#realReceiverListTable").append(row);
    console.log("Row index: ", index);
    $.each(rowData, function (index, value) {
        row.append($("<td>" + value + "</td>"));
    });
}

function drawFakeReceiverTable(data) {
    $("#fakeReceiverListTable").html($fake_receiver_table_header);
    console.log("Building table size: ", data.length);
    for (var i = 0; i < data.length; i++) {
        drawFakeReceiverRow(data[i], i);
        console.log("Building row: ", data[i]);
    }
    console.log("End of Building table: ", data);
}

function drawFakeReceiverRow(rowData, index) {
    var row = $("<tr id=" + index + "/>");
    $("#fakeReceiverListTable").append(row);
    console.log("Row index: ", index);
    $.each(rowData, function (index, value) {
        row.append($("<td>" + value + "</td>"));
    });
}

function importReceivers() {
    document.getElementById("import").disabled = true;
    data = {
        "real" : $('#real').val(),
        "fake": $('#fake').val(),
    };
    console.log("calling import receivers ajax");
    console.log("data: ", data);
    console.log("link: ", "./import");
    $.ajax({
        type : "POST",
        contentType : "application/json",
        data : JSON.stringify(data),
        dataType : 'json',
        timeout : 100000,
        url : "./import",
        success : function(data) {
            console.log("success: ", data);
            document.getElementById("import").disabled = false;
            $('#real').val("");
            $('#fake').val("");
        },
        error: function (e) {
            console.log("ERROR: ", e);
            document.getElementById("import").disabled = false;
            $('#real').val("");
            $('#fake').val("");
        },
    });
    return false;
}

