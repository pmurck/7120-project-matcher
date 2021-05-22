<#-- @ftlvariable name="prioritizedDevs" type="java.util.List<com.pmurck.projectMatcher.model.Developer>" -->
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
    <script>
        $( function() {
            $( "#sortable" ).sortable({
                placeholder: "ui-state-highlight"
            });
            $( "#sortable" ).disableSelection();
        } );
    </script>
</head>
<body>

<@utils.nav></@utils.nav>

<main role="main" class="container">
    <div class="jumbotron">
        <h1>Priorizá los desarrolladores</h1>
        <p class="lead">Arrastrá los desarrolladores según tus prioridades. Los primeros (mas altos) son los de mayor valoracion</p>
        <form method="post">
            <button type="submit" class="btn btn-primary">Actualizar</button>
            <hr/>
            <ul class="list-group" id="sortable">
                <#list prioritizedDevs as dev>
                <li class="list-group-item list-group-item-action">
                    ${dev.user.firstName} ${dev.user.lastName} | ${dev.seniority.desc} | ${dev.availabilityHours}hs disponibilidad
                    <input type='hidden' name='devs' value='${dev.toID().toString()}'/>
                </li>
                </#list>
            </ul>
            <hr/>
            <button type="submit" class="btn btn-primary">Actualizar</button>
        </form>
    </div>
</main>

</body>
</html>

