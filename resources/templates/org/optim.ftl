<#-- @ftlvariable name="data" type="com.pmurck.projectMatcher.OrgOptimData" -->
<#import "/utils.ftl" as utils>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <title>Project Matcher</title>
    <!-- Bootstrap core CSS -->
    <link href="/static/bootstrap.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
    <link href="https://unpkg.com/tabulator-tables@4.7.2/dist/css/tabulator.min.css" rel="stylesheet">
    <script type="text/javascript" src="https://unpkg.com/tabulator-tables@4.7.2/dist/js/tabulator.min.js"></script>
    <script type="text/javascript" src="https://oss.sheetjs.com/sheetjs/xlsx.full.min.js"></script>
</head>
<body>

<@utils.nav></@utils.nav>

<main role="main" class="container">
    <div class="jumbotron">
        <h1>Asignaciones para ${data.org.name}</h1>
        <div id="assignments">

        </div>
    </div>
</main>

</body>
<script>
    var tableData = [
        // primer row de no asignados
        {
            dev: "No Asignado",
            unassigned: ""
            <#list data.projects as project>
                <#list project.requirements as projReq>
                    , "${projReq.toID().toString()}":${data.matcher.unassignedHours(projReq)?string.computer}
                </#list>
            </#list>
        },
        <#list data.devs as dev>
            {
                dev: "${dev.user.firstName} ${dev.user.lastName} (${dev.seniority.desc})",
                unassigned: ${data.matcher.unassignedHours(dev)?string.computer}
            <#list data.projects as project>
                <#list project.requirements as projReq>
                        , "${projReq.toID().toString()}":${(data.matcher.assignedHours(dev, projReq)?string.computer)!"\"\""}
                </#list>
            </#list>
            },
        </#list>
    ]
    var table = new Tabulator("#assignments", {
        data: tableData,
        columnHeaderVertAlign:"middle", //align header contents to middle of cell
        tooltipsHeader:true,
        rowFormatter:function(row){
            //row - row component
            const data = row.getData();

            if(data.dev == "No Asignado"){
                row.getCells().forEach(function (cell) {
                    let value = cell.getValue();
                    if (value == "No Asignado") {
                        cell.getElement().innerHTML =  "<span style='font-weight:bold;'>" + value + "</span>";
                    }
                    else if (value > 0) {
                        cell.getElement().innerHTML =  "<span style='color:red; font-weight:bold;'>" + value + "</span>";
                    }else{
                        cell.setValue("", false);
                    }
                })
            }else{
                row.getCells().forEach(function (cell) {
                    let value = cell.getValue();
                    if (cell.getColumn().getField() != "unassigned" && value > 0) {
                        cell.getElement().innerHTML =  "<span style='font-weight:bold;'>" + value + "</span>";
                    }
                })
            };
        },
        columns:[
            {title:"Desarrollador", field: "dev", hozAlign:"center", frozen: true},
            {title:"No Asignado", field: "unassigned", hozAlign:"center", headerVertical:true, formatter:function(cell, formatterParams){
                    const value = cell.getValue();
                    if(value > 0){
                        return "<span style='color:red; font-weight:bold;'>" + value + "</span>";
                    }else{
                        return "";
                    }
                }
            },
            <#list data.projects as project>
            {
                title: "${project.name}",
                hozAlign: "center",
                columns:[
                <#list project.requirements as projReq>
                    {title:"${projReq.name}", field:"${projReq.toID().toString()}", hozAlign:"center", headerVertical:true},
                </#list>
                ],
            },
            </#list>
        ],
        dataLoaded:function(data){ //freeze first row on data load
            const firstRow = this.getRows()[0];
            if(firstRow){
                firstRow.freeze();
            }
        },
    });
</script>
</html>

